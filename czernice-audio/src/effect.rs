use maudio::engine::node_graph::node_on_process::{EffectCallback, InputBusses, OutputBusses};
use maudio::MaResult;
use std::sync::atomic::{AtomicU32, Ordering};

#[repr(transparent)]
pub struct AtomicF32 {
    inner: AtomicU32
}

impl AtomicF32 {
    pub fn new(value: f32) -> Self {
        Self {
            inner: AtomicU32::new(value.to_bits())
        }
    }

    pub fn store(&self, value: f32, ordering: Ordering) {
        self.inner.store(value.to_bits(), ordering)
    }

    pub fn swap(&self, value: f32, ordering: Ordering) -> f32 {
        f32::from_bits(self.inner.swap(value.to_bits(), ordering))
    }

    pub fn load(&self, ordering: Ordering) -> f32 {
        f32::from_bits(self.inner.load(ordering))
    }
}

pub struct CzerniceEffect {
    pub rate_divisor: AtomicU32,
    pub bit_depth: AtomicU32,
    phase: u32,
    held: [f32; 2],
}

impl CzerniceEffect {
    pub fn new(rate_divisor: u32, bit_depth: u32) -> Self {
        Self {
            rate_divisor: AtomicU32::new(rate_divisor),
            bit_depth: AtomicU32::new(bit_depth),
            phase: 0,
            held: [0.0; 2],
        }
    }

    pub fn set_rate_divisor(&self, rate_divisor: u32) {
        self.rate_divisor.store(rate_divisor, Ordering::Relaxed);
    }

    pub fn set_bit_depth(&self, bit_depth: u32) {
        self.bit_depth.store(bit_depth, Ordering::Relaxed);
    }
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

pub struct CustomDelayEffectBecauseMAudioDefaultOneIsStupidDumbRetardedAndBroken {
    pub wet: AtomicF32,
    pub dry: AtomicF32,
    pub feedback: AtomicF32,
    buffer: Vec<f32>,
    position: usize,
}

impl CustomDelayEffectBecauseMAudioDefaultOneIsStupidDumbRetardedAndBroken {
    pub fn new(wet: f32, dry: f32, feedback: f32, delay_seconds: f32, sample_rate: u32) -> Self {
        Self {
            buffer: vec![0.0; ((sample_rate as f32 * delay_seconds).round() as usize).max(1) * 2],
            position: 0,
            wet: AtomicF32::new(wet),
            dry: AtomicF32::new(dry),
            feedback: AtomicF32::new(feedback),
        }
    }

    pub fn set_wet(&self, wet: f32) {
        self.wet.store(wet, Ordering::Relaxed);
    }

    pub fn set_dry(&self, dry: f32) {
        self.dry.store(dry, Ordering::Relaxed);
    }

    pub fn set_feedback(&self, feedback: f32) {
        self.feedback.store(feedback, Ordering::Relaxed);
    }
}

impl EffectCallback for CustomDelayEffectBecauseMAudioDefaultOneIsStupidDumbRetardedAndBroken {
    fn on_audio(&mut self, input: &InputBusses<'_>, output: &mut OutputBusses<'_>) -> MaResult<u32> {
        let wet = self.wet.load(Ordering::Relaxed);
        let dry = self.dry.load(Ordering::Relaxed);
        let feedback = self.feedback.load(Ordering::Relaxed);

        let (Some(src), Some(dst)) = (input.get_bus(0), output.get_mut_bus(0)) else { return Ok(0); };

        let frames = (src.len().min(dst.len()) / 2) as u32;

        for frame in 0..frames as usize {
            let delay1 = self.buffer[self.position * 2];
            let delay2 = self.buffer[self.position * 2 + 1];
            let curr1 = src[frame * 2];
            let curr2 = src[frame * 2 + 1];
            self.buffer[self.position * 2] = curr1 + delay1 * feedback;
            self.buffer[self.position * 2 + 1] = curr2 + delay2 * feedback;

            dst[frame * 2] = delay1 * wet + curr1 * dry;
            dst[frame * 2 + 1] = delay2 * wet + curr2 * dry;

            self.position += 1;
            self.position %= self.buffer.len() / 2;
        }
        Ok(frames)

    }
}
