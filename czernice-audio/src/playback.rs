use std::ptr;
use maudio::engine::node_graph::nodes::Node;
use maudio::engine::node_graph::nodes::filters::biquad::BiquadNode;
use maudio::sound::Sound;
use maudio::MaResult;
use std::sync::atomic::Ordering;
use maudio::data_source::sources::decoder::{Decoder, Owned};
use czernice_audio_proc_macro::java_compatible;
use crate::effect::{CustomDelayEffectBecauseMAudioDefaultOneIsStupidDumbRetardedAndBroken, CzerniceEffect};

pub fn with_czernice_playback<F, T>(playback: i64, f: F) -> MaResult<T> where F: FnOnce(&mut CzernicePlayback) -> T {
    let playback = unsafe { &mut *(playback.cast_unsigned() as *mut CzernicePlayback) };
    Ok(f(playback))
}

pub fn with_czernice_playback_faulty<F, T>(playback: i64, f: F) -> MaResult<T> where F: FnOnce(&mut CzernicePlayback) -> MaResult<T> {
    let playback = unsafe { &mut *(playback.cast_unsigned() as *mut CzernicePlayback) };
    f(playback)
}

pub fn band_coefficients(
    playback: &CzernicePlayback,
    band: usize,
    gain_db: f32,
    sample_rate: i32,
) -> MaResult<[f64; 6]> {
    // values pulled out of my ass, may need tweaking
    // let (freq, q): (f64, f64) = match band {
    //     0 => (200.0, 0.7),
    //     1 => (1000.0, 1.0),
    //     2 => (4000.0, 0.75),
    //     _ => return Err(MaudioError::other("band index out of bounds")),
    // };
    let CzerniceEqBandInfo { frequency, q, .. } = playback.eq_info[band];

    let a = 10.0_f64.powf(gain_db as f64 / 40.0);
    let w = std::f64::consts::TAU * frequency / sample_rate as f64;
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
    pub eq: Vec<BiquadNode>,
    pub effect: Node<maudio::engine::node_graph::node_on_process::Effect<CzerniceEffect>>,
    pub delay: Node<maudio::engine::node_graph::node_on_process::Effect<CustomDelayEffectBecauseMAudioDefaultOneIsStupidDumbRetardedAndBroken>>,
    pub _decoder: Decoder<f32, Owned>,
    pub eq_info: Vec<CzerniceEqBandInfo>
}

#[repr(C)]
#[java_compatible(package = "net.xoknox.czernicesweeper.czerniceaudio.structs")]
pub struct CzerniceEqBandModificationSubmitInfo {
    pub band: usize,
    pub frequency: f64,
    pub q: f64
}

#[repr(C)]
#[java_compatible(package = "net.xoknox.czernicesweeper.czerniceaudio.structs")]
pub struct CzerniceEqGainModificationSubmitInfo {
    pub band: usize,
    pub gain_db: f32,
    pub sample_rate: u32
}

#[repr(C)]
#[derive(Clone)]
#[java_compatible(package = "net.xoknox.czernicesweeper.czerniceaudio.structs")]
pub struct CzerniceEqBandInfo {
    pub frequency: f64,
    pub q: f64,
    pub gain_db: f32,
    pub sample_rate: u32
}

unsafe impl Send for CzernicePlayback {}

impl CzernicePlayback {
    pub fn retire_sound(sound: *mut CzernicePlayback) {
        unsafe { let _ = Box::from_raw(sound); }
    }

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

    pub fn set_eq_gain(&mut self, si: *mut CzerniceEqGainModificationSubmitInfo) -> MaResult<()> {
        let si = unsafe { si.as_ref().unwrap() };

        let coefficients = band_coefficients(self, si.band, si.gain_db, si.sample_rate as i32)?;

        let curr: &mut CzerniceEqBandInfo = &mut self.eq_info[si.band];
        curr.gain_db = si.gain_db;
        curr.sample_rate = si.sample_rate;

        self.eq[si.band].reinit(coefficients[0], coefficients[1], coefficients[2], coefficients[3], coefficients[4], coefficients[5])
    }

    pub fn set_eq_band(&mut self, si: *mut CzerniceEqBandModificationSubmitInfo) {
        let si = unsafe { si.as_ref().unwrap() };

        let curr: &mut CzerniceEqBandInfo = &mut self.eq_info[si.band];
        curr.frequency = si.frequency;
        curr.q = si.q;
    }

    pub fn get_eq_band_info(&mut self, count: &mut usize, data: *mut CzerniceEqBandInfo) {
        if (data.is_null()) {
            *count = self.eq_info.len();
        } else {
            unsafe {
                ptr::copy(self.eq_info.as_ptr(), data, *count);
            }
        }
    }

    pub fn set_doppler(&self, doppler: f32) {
        self.sound.set_doppler_factor(doppler);
    }

    pub fn set_rolloff(&self, rolloff: f32) {
        self.sound.set_rolloff(rolloff);
    }

    pub fn set_wet(&self, wet: f32) {
        self.delay.set_wet(wet);
    }

    pub fn set_dry(&self, dry: f32) {
        self.delay.set_dry(dry);
    }

    pub fn set_feedback(&self, feedback: f32) {
        self.delay.set_feedback(feedback);
    }

    pub fn set_sample_rate_reduction(&self, sample_rate_reduction: u32) {
        let reduction = sample_rate_reduction.clamp(1, 256);
        self.effect.set_rate_divisor(reduction);
    }

    pub fn set_bit_depth(&self, bit_depth: u32) {
        let depth = bit_depth.clamp(0, 32);
        self.effect.set_bit_depth(depth);
    }
}
