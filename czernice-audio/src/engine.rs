use std::slice;
use crate::playback::{CzerniceEffect, CzernicePlayback, EqBandInfo};
use maudio::audio::sample_rate::SampleRate;
use maudio::data_source::sources::decoder::DecoderBuilder;
use maudio::engine::node_graph::node_builder::NodeBuilder;
use maudio::engine::node_graph::nodes::effects::delay::DelayNodeBuilder;
use maudio::engine::node_graph::nodes::filters::biquad::BiquadNodeBuilder;
use maudio::engine::node_graph::nodes::NodeOps;
use maudio::engine::node_graph::NodeGraphOps;
use maudio::engine::Engine;
use maudio::{MaResult, MaudioError};
use std::sync::atomic::AtomicU32;

pub fn with_czernice_engine<F, T>(engine: i64, f: F) -> MaResult<T> where F: FnOnce(&mut CzerniceAudioEngine) -> MaResult<T> {
    let engine = unsafe { &mut *(engine.cast_unsigned() as *mut CzerniceAudioEngine) };
    f(engine)
}

pub struct CzerniceAudioEngine {
    pub engine: Engine,
}

#[repr(C)]
pub struct CzerniceSoundCreateInfo {
    pub data_ptr: usize,
    pub data_len: usize,
    pub sample_rate: SampleRate,
    pub channels: u32,
    pub num_filters: usize,
    pub eq_info_ptr: usize,
    pub eq_info_len: usize
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
            .sample_rate(ci.sample_rate);

        let decoder = builder.copy_memory(unsafe { slice::from_raw_parts(ci.data_ptr as *const _, ci.data_len) })?;
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
        let effect = node_builder.build(&graph, CzerniceEffect {
            rate_divisor: AtomicU32::new(1),
            bit_depth: AtomicU32::new(16),
            phase: 0,
            held: [0.0; 2],
        })?;

        // TODO: replace with custom impl, this one is retarded as shit
        //let delay = DelayNodeBuilder::new(&graph, 2, SampleRate::Sr44100, 11_025, 0.6).dry(1.0).wet(0.7).build()?;

        sound.as_node().attach_output_bus(0, &filters[0].as_node(), 0)?;

        for i in 0 .. (ci.num_filters - 1) {
            filters[i].as_node().attach_output_bus(0, &filters[i + 1].as_node(), 0)?;
        }

        filters[ci.num_filters - 1].as_node().attach_output_bus(0, &effect.as_node(), 0)?;
        effect.as_node().attach_output_bus(0, &graph.endpoint(), 0)?;
        // delay.as_node().attach_output_bus(0, &graph.endpoint(), 0)?;

        let eq = filters.try_into().ok().ok_or(MaudioError::other("idk something broke"))?;
        
        let eq_info = unsafe { slice::from_raw_parts(ci.eq_info_ptr as *const _, ci.eq_info_len) }.to_vec();
        
        let _box = Box::new(CzernicePlayback { sound, eq, effect, decoder, eq_info });
        Ok(Box::into_raw(_box))
    }
}

