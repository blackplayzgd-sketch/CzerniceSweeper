use std::slice;
use crate::playback::{CzernicePlayback, CzerniceEqBandInfo, band_coefficients};
use maudio::audio::sample_rate::SampleRate;
use maudio::data_source::sources::decoder::DecoderBuilder;
use maudio::engine::node_graph::node_builder::NodeBuilder;
use maudio::engine::node_graph::nodes::filters::biquad::BiquadNodeBuilder;
use maudio::engine::node_graph::nodes::NodeOps;
use maudio::engine::node_graph::NodeGraphOps;
use maudio::engine::Engine;
use maudio::MaResult;
use czernice_audio_proc_macro::{java_compatible};
use crate::effect::{CustomDelayEffectBecauseMAudioDefaultOneIsStupidDumbRetardedAndBroken, CzerniceEffect};

pub struct CzerniceAudioEngine {
    pub engine: Engine,
}

#[repr(C)]
#[java_compatible(package = "net.xoknox.czernicesweeper.czerniceaudio.structs")]
pub struct CzerniceSoundCreateInfo {
    pub p_data: *const u8,
    pub data_count: u32,
    pub sample_rate: u32,
    pub channels: u32,

    // eq stuff
    pub p_eq_info: *const CzerniceEqBandInfo,
    pub eq_info_count: u32,

    // reverb stuff
    pub reverb_delay: f32,
    pub wet: f32,
    pub dry: f32,
    pub feedback: f32,

    // low quality effect stuff
    pub rate_divisor: u32,
    pub bit_depth: u32,
}

impl CzerniceAudioEngine {
    pub fn new() -> MaResult<*mut CzerniceAudioEngine> {
        let engine = Engine::new()?;

        let this = Box::new(Self { engine });
        Ok(Box::into_raw(this))
    }

    pub fn retire_engine(engine: *mut CzerniceAudioEngine) {
        unsafe { let _ = Box::from_raw(engine); }
    }

    pub fn create_sound(&self, ci: *mut CzerniceSoundCreateInfo) -> MaResult<*mut CzernicePlayback> {
        let ci = unsafe { ci.as_ref().unwrap() };

        let mut builder = DecoderBuilder::new_f32();
        builder
            .channels(ci.channels)
            .sample_rate(ci.sample_rate.try_into().unwrap_or(SampleRate::Custom(0)));

        let decoder = builder.copy_memory(unsafe { slice::from_raw_parts(ci.p_data, ci.data_count as usize) })?;
        let sound = self.engine.new_sound_from_source(&decoder)?;

        let graph = sound.node_graph();

        let mut filters = Vec::with_capacity(ci.eq_info_count as usize);
        let eq_info = unsafe { slice::from_raw_parts(ci.p_eq_info, ci.eq_info_count as usize) };
        for i in 0..ci.eq_info_count as usize {
            let band_data = &eq_info[i];
            let coefficients = band_coefficients(band_data)?;

            let filter = BiquadNodeBuilder::new(&graph, ci.channels, coefficients[0] as f32, coefficients[1] as f32, coefficients[2] as f32, coefficients[3] as f32, coefficients[4] as f32, coefficients[5] as f32)
                .in_channel_count(ci.channels).out_channel_count(ci.channels).build()?;
            filters.push(filter);
        }

        let mut node_builder = NodeBuilder::effect();
        node_builder.set_in_channel_count(0, ci.channels).set_out_channel_count(0, ci.channels);
        let effect = node_builder.build(&graph, CzerniceEffect::new(ci.rate_divisor, ci.bit_depth))?;

        let mut node_builder2 = NodeBuilder::effect();
        node_builder2.set_in_channel_count(0, ci.channels).set_out_channel_count(0, ci.channels);
        let delay = node_builder2.build(
            &graph, CustomDelayEffectBecauseMAudioDefaultOneIsStupidDumbRetardedAndBroken::new(ci.wet, ci.dry, ci.feedback, ci.reverb_delay, ci.sample_rate))?;

        sound.as_node().attach_output_bus(0, &filters[0].as_node(), 0)?;

        for i in 0 .. (ci.eq_info_count as usize - 1) {
            filters[i].as_node().attach_output_bus(0, &filters[i + 1].as_node(), 0)?;
        }

        filters[ci.eq_info_count as usize - 1].as_node().attach_output_bus(0, &effect.as_node(), 0)?;
        effect.as_node().attach_output_bus(0, &delay.as_node(), 0)?;
        delay.as_node().attach_output_bus(0, &graph.endpoint(), 0)?;

        let eq_info = eq_info.to_vec();
        
        let _box = Box::new(CzernicePlayback { sound, eq: filters, effect, delay, _decoder: decoder, eq_info });
        Ok(Box::into_raw(_box))
    }
}

