mod engine;
mod playback;

use jni::errors::{Error, LogErrorAndDefault};
use jni::objects::JClass;
use jni::sys::{jboolean, jbyteArray, jfloat, jint, jlong};
use jni::{EnvUnowned, jni_sig, jni_str};
use maudio::MaResult;
use mvutils::lazy;
use mvutils::version::Version;
use paste::paste;
use crate::engine::{with_czernice_engine, CzerniceAudioEngine, ENGINES};
use crate::playback::{with_czernice_playback, with_czernice_playback_faulty};

pub trait OrDefaultWithError<T> {
    fn or_default_with_err(self, default: T) -> T;
    fn is_ok_with_err(self) -> bool;
    fn simply_err(self);
}

impl<T> OrDefaultWithError<T> for MaResult<T> {
    fn or_default_with_err(self, default: T) -> T {
        match self {
            Ok(t) => t,
            Err(e) => {
                eprintln!("{e}");
                default
            }
        }
    }

    fn is_ok_with_err(self) -> bool {
        match self {
            Ok(_) => true,
            Err(e) => {
                eprintln!("{e}");
                false
            }
        }
    }

    fn simply_err(self) {
        if let Err(e) = self {
            eprintln!("{e}");
        }
    }
}

lazy! {
    static VERSION: Version = Version::parse("v1.0.0").unwrap();
}

macro_rules! czernicefn {
    {
        $(
            fn $name:ident($env:ident, $class:ident $(,$pname:ident: $pty:ty)*) $(-> $ret:ty)? $code:block
        )*
    } => {
        paste! {
            $(
                #[unsafe(no_mangle)]
                pub unsafe extern "system" fn [<Java_net_xoknox_czernicesweeper_czerniceaudio_CzerniceAudio_ $name>](
                    mut $env: EnvUnowned,
                    $class: JClass,
                    $($pname: $pty,)*
                ) $(-> $ret)? {
                    #[allow(non_snake_case)]
                    #[allow(dead_code)]
                    fn $name() {}
                    $code
                }
            )*
        }
    };
}

czernicefn! {
    fn nversion(_env, _class) -> jint {
        VERSION.as_vulkan_version().cast_signed()
    }

    fn verify(env, class, crash: jboolean) -> jboolean {
        let matches = env.with_env::<_, bool, Error>(|e| {
            let a = e.get_static_field(class, jni_str!("EXPECTED_VERSION"), jni_sig!(int))?;

            let version = a.i()?;

            Ok(version == VERSION.as_vulkan_version().cast_signed())
        }).resolve::<LogErrorAndDefault>();

        if crash && !matches {
            panic!("GASP ALARM! Version mismatch!")
        }

        matches
    }

    fn ncreateEngine(_env, _class) -> jlong {
        CzerniceAudioEngine::new().or_default_with_err(0).cast_signed()
    }

    fn nretireEngine(_env, _class, engine: jlong) -> jboolean {
        ENGINES.lock().remove(&engine.cast_unsigned()).is_some()
    }

    fn ncreateSound(env, _class, engine: jlong, bytes: jbyteArray) -> jlong {
        env.with_env::<_, jlong, Error>(|e| {
            let array = unsafe { jni::objects::JByteArray::from_raw(e, bytes) };
            let data = e.convert_byte_array(&array)?;
            Ok(with_czernice_engine(engine, |engine| {
                engine.create_sound(&data)
            }).or_default_with_err(0).cast_signed())
        }).resolve::<LogErrorAndDefault>()
    }

    fn nretireSound(_env, _class, engine: jlong, sound: jlong) -> jboolean {
        with_czernice_engine(engine, |engine| {
            Ok(engine.remove_sound(sound.cast_unsigned()))
        }).or_default_with_err(false)
    }

    fn nstartSound(_env, _class, engine: jlong, sound: jlong) -> jboolean {
        with_czernice_playback_faulty(engine, sound, |sound| sound.start()).is_ok_with_err()
    }

    fn nstopSound(_env, _class, engine: jlong, sound: jlong) -> jboolean {
        with_czernice_playback_faulty(engine, sound, |sound| sound.stop()).is_ok_with_err()
    }

    fn nstopFade(_env, _class, engine: jlong, sound: jlong, fade: jlong) -> jboolean {
         with_czernice_playback_faulty(engine, sound, |sound| sound.stop_fade(fade.cast_unsigned())).is_ok_with_err()
    }

    fn nsetVolume(_env, _class, engine: jlong, sound: jlong, volume: jfloat) {
        with_czernice_playback(engine, sound, |sound| sound.set_volume(volume)).simply_err()
    }

    fn nsetSpeed(_env, _class, engine: jlong, sound: jlong, speed: jfloat) {
        with_czernice_playback(engine, sound, |sound| sound.set_speed(speed)).simply_err()
    }

    fn nsetBalance(_env, _class, engine: jlong, sound: jlong, balance: jfloat) {
        with_czernice_playback(engine, sound, |sound| sound.set_balance(balance)).simply_err()
    }

    fn nsetDoppler(_env, _class, engine: jlong, sound: jlong, doppler: jfloat) {
        with_czernice_playback(engine, sound, |sound| sound.set_doppler(doppler)).simply_err()
    }

    fn nsetRolloff(_env, _class, engine: jlong, sound: jlong, rolloff: jfloat) {
        with_czernice_playback(engine, sound, |sound| sound.set_rolloff(rolloff)).simply_err()
    }

    fn nsetWet(_env, _class, engine: jlong, sound: jlong, wet: jfloat) {
        with_czernice_playback(engine, sound, |sound| sound.set_wet(wet)).simply_err()
    }

    fn nsetDry(_env, _class, engine: jlong, sound: jlong, dry: jfloat) {
        with_czernice_playback(engine, sound, |sound| sound.set_dry(dry)).simply_err()
    }

    fn nsetDecayFrames(_env, _class, engine: jlong, sound: jlong, decay_frames: jfloat) {
        with_czernice_playback(engine, sound, |sound| sound.set_decay_frames(decay_frames)).simply_err()
    }

    fn nisPlaying(_env, _class, engine: jlong, sound: jlong) -> jboolean {
        with_czernice_playback(engine, sound, |sound| sound.is_playing()).or_default_with_err(false)
    }

     fn nsetLooping(_env, _class, engine: jlong, sound: jlong, looping: jboolean) {
        with_czernice_playback(engine, sound, |sound| sound.set_looping(looping)).simply_err()
    }

    fn nsetEq(_env, _class, engine: jlong, sound: jlong, band: jint, gain: jfloat) {
        with_czernice_playback(engine, sound, |sound| sound.set_eq(band as usize, gain)).simply_err()
    }

    fn nsetSampleRateReduction(_env, _class, engine: jlong, sound: jlong, reduction: jint) {
        with_czernice_playback(engine, sound, |sound| sound.set_sample_rate_reduction(reduction as u32)).simply_err()
    }

    fn nsetBitDepth(_env, _class, engine: jlong, sound: jlong, depth: jint) {
        with_czernice_playback(engine, sound, |sound| sound.set_bit_depth(depth as u32)).simply_err()
    }
}
