use std::slice;
use crate::playback::{CzernicePlayback, CzerniceEqBandInfo};
use maudio::audio::sample_rate::SampleRate;
use maudio::data_source::sources::decoder::DecoderBuilder;
use maudio::engine::node_graph::node_builder::NodeBuilder;
use maudio::engine::node_graph::nodes::filters::biquad::BiquadNodeBuilder;
use maudio::engine::node_graph::nodes::NodeOps;
use maudio::engine::node_graph::NodeGraphOps;
use maudio::engine::Engine;
use maudio::{MaResult, MaudioError};
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
    pub reverb_delay: f32,
    pub num_filters: usize,
    pub p_eq_info: *const CzerniceEqBandInfo,
    pub eq_info_count: u32,
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
        let mut filters = Vec::with_capacity(ci.num_filters);
        for _ in 0..ci.num_filters {
            let filter = BiquadNodeBuilder::new(&graph, ci.channels, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
                .in_channel_count(ci.channels).out_channel_count(ci.channels).build()?;
            filters.push(filter);
        }

        let mut node_builder = NodeBuilder::effect();
        node_builder.set_in_channel_count(0, ci.channels).set_out_channel_count(0, ci.channels);
        let effect = node_builder.build(&graph, CzerniceEffect::new(1, 16))?;

        let mut node_builder2 = NodeBuilder::effect();
        node_builder2.set_in_channel_count(0, ci.channels).set_out_channel_count(0, ci.channels);
        let delay = node_builder2.build(
            &graph, CustomDelayEffectBecauseMAudioDefaultOneIsStupidDumbRetardedAndBroken::new(0.0, 1.0, 0.25, ci.reverb_delay, ci.sample_rate))?;

        sound.as_node().attach_output_bus(0, &filters[0].as_node(), 0)?;

        for i in 0 .. (ci.num_filters - 1) {
            filters[i].as_node().attach_output_bus(0, &filters[i + 1].as_node(), 0)?;
        }

        filters[ci.num_filters - 1].as_node().attach_output_bus(0, &effect.as_node(), 0)?;
        effect.as_node().attach_output_bus(0, &delay.as_node(), 0)?;
        delay.as_node().attach_output_bus(0, &graph.endpoint(), 0)?;

        let eq = filters.try_into().ok().ok_or(MaudioError::other("idk something broke"))?;
        
        let eq_info = unsafe { slice::from_raw_parts(ci.p_eq_info, ci.eq_info_count as usize) }.to_vec();
        
        let _box = Box::new(CzernicePlayback { sound, eq, effect, delay, _decoder: decoder, eq_info });
        Ok(Box::into_raw(_box))
    }
}

