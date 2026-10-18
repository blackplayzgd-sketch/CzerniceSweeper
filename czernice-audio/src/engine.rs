use std::collections::HashMap;
use std::sync::atomic::AtomicU32;
use maudio::audio::sample_rate::SampleRate;
use maudio::data_source::sources::decoder::DecoderBuilder;
use maudio::engine::Engine;
use maudio::engine::node_graph::node_builder::NodeBuilder;
use maudio::engine::node_graph::NodeGraphOps;
use maudio::engine::node_graph::nodes::effects::delay::DelayNodeBuilder;
use maudio::engine::node_graph::nodes::filters::biquad::BiquadNodeBuilder;
use maudio::engine::node_graph::nodes::NodeOps;
use maudio::{MaResult, MaudioError};
use mvutils::lazy;
use mvutils::utils::next_id;
use parking_lot::Mutex;
use crate::playback::{CzerniceEffect, CzernicePlayback};

pub type CzerniceId = u64;

lazy! {
    pub static ENGINES: Mutex<HashMap<CzerniceId, CzerniceAudioEngine>> = Mutex::new(HashMap::new());
}

pub fn with_czernice_engine<F, T>(engine: i64, f: F) -> MaResult<T> where F: FnOnce(&mut CzerniceAudioEngine) -> MaResult<T> {
    let mut engines = ENGINES.lock();
    if let Some(engine) = engines.get_mut(&engine.cast_unsigned()) {
        f(engine)
    } else {
        Err(MaudioError::other("CzerniceAudioEngine not found"))
    }
}

pub struct CzerniceAudioEngine {
    pub engine: Engine,
    pub sounds: HashMap<CzerniceId, CzernicePlayback>,
}

impl CzerniceAudioEngine {
    pub fn new() -> MaResult<CzerniceId> {
        let engine = Engine::new()?;
        let mut id = next_id("czernice_audio");
        if id == 0 {
            id = next_id("czernice_audio");
        }
        ENGINES.lock().insert(id, CzerniceAudioEngine { engine, sounds: HashMap::new() });
        Ok(id)
    }

    pub fn create_sound(&mut self, data: &[u8]) -> MaResult<CzerniceId> {
        let mut builder = DecoderBuilder::new_f32();
        builder.channels(2).sample_rate(SampleRate::Sr44100);
        let decoder = builder.copy_memory(data.to_vec())?;
        let sound = self.engine.new_sound_from_source(&decoder)?;
        let graph = sound.node_graph();
        let mut filters = Vec::with_capacity(3);
        for _ in 0..3 {
            let filter = BiquadNodeBuilder::new(&graph, 2, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
                .in_channel_count(2).out_channel_count(2).build()?;
            filters.push(filter);
        }
        sound.as_node().attach_output_bus(0, &filters[0].as_node(), 0)?;
        filters[0].as_node().attach_output_bus(0, &filters[1].as_node(), 0)?;
        filters[1].as_node().attach_output_bus(0, &filters[2].as_node(), 0)?;
        let mut node_builder = NodeBuilder::effect();
        node_builder.set_in_channel_count(0, 2).set_out_channel_count(0, 2);
        let effect = node_builder.build(&graph, CzerniceEffect {
            rate_divisor: AtomicU32::new(1),
            bit_depth: AtomicU32::new(16),
            phase: 0,
            held: [0.0; 2],
        })?;
        filters[2].as_node().attach_output_bus(0, &effect.as_node(), 0)?;
        let delay = DelayNodeBuilder::new(&graph, 2, SampleRate::Sr44100, 11_025, 0.25)
            .in_channel_count(2).out_channel_count(2).mix(0.0).build()?;
        effect.as_node().attach_output_bus(0, &delay.as_node(), 0)?;
        delay.as_node().attach_output_bus(0, &graph.endpoint(), 0)?;
        let id = next_id("czernice_id");
        let eq = filters.try_into().ok().ok_or(MaudioError::other("idk something broke"))?;
        self.sounds.insert(id, CzernicePlayback { sound, eq, effect, delay });
        Ok(id)
    }

    pub fn remove_sound(&mut self, id: CzerniceId) -> bool {
        self.sounds.remove(&id).is_some()
    }
}

