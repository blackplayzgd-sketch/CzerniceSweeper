use crate::engine::with_czernice_engine;
use maudio::engine::node_graph::node_on_process::{EffectCallback, InputBusses, OutputBusses};
use maudio::engine::node_graph::nodes::Node;
use maudio::engine::node_graph::nodes::effects::delay::DelayNode;
use maudio::engine::node_graph::nodes::filters::biquad::BiquadNode;
use maudio::sound::Sound;
use maudio::{MaResult, MaudioError};
use std::sync::atomic::{AtomicU32, Ordering};

pub fn with_czernice_playback<F, T>(engine: i64, playback: i64, f: F) -> MaResult<T> where F: FnOnce(&mut CzernicePlayback) -> T {
    with_czernice_engine(engine, |engine| {
        if let Some(playback) = engine.sounds.get_mut(&playback.cast_unsigned()) {
            Ok(f(playback))
        } else {
            Err(MaudioError::other("CzernicePlayback not found"))
        }
    })
}

pub fn with_czernice_playback_faulty<F, T>(engine: i64, playback: i64, f: F) -> MaResult<T> where F: FnOnce(&mut CzernicePlayback) -> MaResult<T> {
    with_czernice_engine(engine, |engine| {
        if let Some(playback) = engine.sounds.get_mut(&playback.cast_unsigned()) {
            f(playback)
        } else {
            Err(MaudioError::other("CzernicePlayback not found"))
        }
    })
}

pub struct CzerniceEffect {
    pub rate_divisor: AtomicU32,
    pub bit_depth: AtomicU32,
    pub phase: u32,
    pub held: [f32; 2],
}

impl EffectCallback for CzerniceEffect {
    fn on_audio(&mut self, input: &InputBusses<'_>, output: &mut OutputBusses<'_>) -> MaResult<u32> {
        let divisor = self.rate_divisor.load(Ordering::Relaxed);
        let bits = self.bit_depth.load(Ordering::Relaxed);

        let (Some(src), Some(dst)) = (input.get_bus(0), output.get_mut_bus(0)) else { return Ok(0); };

        let frames = (src.len().min(dst.len()) / 2) as u32;

        let levels = ((1u32 << (bits - 1)) - 1).max(1) as f32;
        for frame in 0..frames as usize {
            if self.phase == 0 {
                self.held[0] = (src[frame * 2] * levels).round() / levels;
                self.held[1] = (src[frame * 2 + 1] * levels).round() / levels;
            }
            dst[frame * 2] = self.held[0];
            dst[frame * 2 + 1] = self.held[1];
            self.phase += 1;
            if self.phase >= divisor { self.phase = 0; }
        }
        Ok(frames)
    }
}

pub fn band_coefficients(
    band: usize,
    gain_db: f32,
    sample_rate: f32,
) -> MaResult<[f64; 6]> {
    // values pulled out of my ass, may need tweaking
    let (freq, q): (f64, f64) = match band {
        0 => (200.0, 0.7),
        1 => (1000.0, 1.0),
        2 => (4000.0, 0.75),
        _ => return Err(MaudioError::other("band index out of bounds")),
    };

    let a = 10.0_f64.powf(gain_db as f64 / 40.0);
    let w = std::f64::consts::TAU * freq / sample_rate as f64;
    let alpha = w.sin() / (2.0 * q);
    let c = w.cos();

    Ok([
        1.0 + alpha * a,
        -2.0 * c,
        1.0 - alpha * a,
        1.0 + alpha / a,
        -2.0 * c,
        1.0 - alpha / a,
    ])
}

pub struct CzernicePlayback {
    pub sound: Sound,
    pub eq: [BiquadNode; 3],
    pub effect: Node<maudio::engine::node_graph::node_on_process::Effect<CzerniceEffect>>,
    pub delay: DelayNode,
}

unsafe impl Send for CzernicePlayback {}

impl CzernicePlayback {
    pub fn start(&self) -> MaResult<()> {
        self.sound.play_sound()
    }

    pub fn stop(&self) -> MaResult<()> {
        self.sound.stop_sound()
    }

    pub fn stop_fade(&self, millis: u64) -> MaResult<()> {
        self.sound.stop_with_fade_millis(millis)
    }

    pub fn set_volume(&self, volume: f32) {
        self.sound.set_volume(volume);
    }

    pub fn set_speed(&self, speed: f32) {
        self.sound.set_pitch(speed)
    }

    pub fn set_balance(&self, balance: f32) {
        self.sound.set_pan(balance.clamp(-1.0, 1.0));
    }

    pub fn set_looping(&self, looping: bool) {
        self.sound.set_looping(looping);
    }

    pub fn is_playing(&self) -> bool {
        self.sound.is_playing()
    }

    pub fn set_eq(&mut self, band: usize, gain_db: f32) -> MaResult<()> {
        let coefficients = band_coefficients(band, gain_db, 44100.0)?;
        self.eq[band].reinit(coefficients[0], coefficients[1], coefficients[2], coefficients[3], coefficients[4], coefficients[5])
    }

    pub fn set_doppler(&self, doppler: f32) {
        self.sound.set_doppler_factor(doppler);
    }

    pub fn set_rolloff(&self, rolloff: f32) {
        self.sound.set_rolloff(rolloff);
    }

    pub fn set_wet(&mut self, wet: f32) {
        self.delay.set_wet(wet);
    }

    pub fn set_dry(&mut self, dry: f32) {
        self.delay.set_dry(dry);
    }

    pub fn set_decay_frames(&mut self, decay_frames: f32) {
        self.delay.set_decay_frames(decay_frames);
    }

    pub fn set_sample_rate_reduction(&mut self, sample_rate_reduction: u32) {
        let reduction = sample_rate_reduction.clamp(1, 256);
        self.effect.rate_divisor.store(reduction, Ordering::Relaxed);
    }

    pub fn set_bit_depth(&mut self, bit_depth: u32) {
        let depth = bit_depth.clamp(0, 32);
        self.effect.bit_depth.store(depth, Ordering::Relaxed);
    }
}
