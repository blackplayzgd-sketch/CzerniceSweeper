#![allow(clippy::missing_safety_doc, clippy::missing_panics_doc, clippy::not_unsafe_ptr_arg_deref, clippy::needless_range_loop, clippy::wrong_self_convention)]

pub mod engine;
pub mod playback;
pub mod effect;
pub mod memory;

use crate::engine::{CzerniceAudioEngine, CzerniceSoundCreateInfo};
use crate::playback::{with_czernice_playback, with_czernice_playback_faulty, CzernicePlayback, CzerniceEqBandInfo, CzerniceEqBandModificationSubmitInfo, CzerniceEqGainModificationSubmitInfo};
use jni::errors::{Error, LogErrorAndDefault};
use jni::sys::{jboolean, jfloat, jint, jlong};
use jni::{jni_sig, jni_str};
use maudio::MaResult;
use mvutils::lazy;
use mvutils::version::Version;

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

#[macro_export]
macro_rules! czernicefn {
    (
        $namespace:ident, ($env:ident, $class:ident) => {
            $(
                fn $name:ident($($pname:ident: $pty:ty),*) $(-> $ret:ty)? $code:block
            )*
        }
    ) => {
        paste::paste! {
            #[allow(dead_code, non_snake_case)]
            fn $namespace() {}

            $(
                #[allow(unused, unused_mut)]
                #[unsafe(no_mangle)]
                pub unsafe extern "system" fn [<Java_ $namespace _ $name>](
                    mut $env: jni::EnvUnowned,
                    $class: jni::objects::JClass,
                    $($pname: $pty,)*
                ) $(-> $ret)? {
                    #[allow(dead_code, non_snake_case)]
                    fn $name() {}
                    $code
                }
            )*
        }
    };
}


///////////////////////////
//         AUDIO         //
///////////////////////////
czernicefn!(net_xoknox_czernicesweeper_czerniceaudio_CzerniceAudio, (env, class) => {
    fn nversion() -> jint {
        VERSION.as_vulkan_version().cast_signed()
    }

    fn verify(crash: jboolean) -> jboolean {
        let matches = env.with_env::<_, bool, Error>(|e| {
            let a = e.get_static_field(class, jni_str!("EXPECTED_VERSION"), jni_sig!(int))?;

            let version = a.i()?;

            Ok(version == VERSION.as_vulkan_version().cast_signed())
        }).resolve::<LogErrorAndDefault>();

        assert!(!(crash && !matches), "GASP ALARM! Version mismatch!");

        matches
    }

    fn ncreateEngine() -> jlong {
        CzerniceAudioEngine::new().map(|sth| sth.addr() as jlong).or_default_with_err(0)
    }

    fn nretireEngine(engine: jlong) {
        let engine = unsafe { &mut *(engine.cast_unsigned() as *mut CzerniceAudioEngine) };
        CzerniceAudioEngine::retire_engine(engine);
    }

    fn ncreateSound(engine: jlong, create_info: jlong) -> jlong {
        let ci = unsafe { &mut *(create_info.cast_unsigned() as *mut CzerniceSoundCreateInfo) };
        let engine = unsafe { &mut *(engine.cast_unsigned() as *mut CzerniceAudioEngine) };

        engine.create_sound(ci).map(|sth| sth.addr() as jlong).or_default_with_err(0)
    }

    fn nretireSound(sound: jlong) {
        let sound = unsafe { &mut *(sound.cast_unsigned() as *mut CzernicePlayback) };
        CzernicePlayback::retire_sound(sound);
    }

    fn nstartSound(sound: jlong) -> jboolean {
        with_czernice_playback_faulty(sound, |sound| sound.start()).is_ok_with_err()
    }

    fn nstopSound(sound: jlong) -> jboolean {
        with_czernice_playback_faulty(sound, |sound| sound.stop()).is_ok_with_err()
    }

    fn nstopFade(sound: jlong, fade: jlong) -> jboolean {
         with_czernice_playback_faulty(sound, |sound| sound.stop_fade(fade.cast_unsigned())).is_ok_with_err()
    }

    fn nsetVolume(sound: jlong, volume: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_volume(volume)).simply_err();
    }

    fn nsetSpeed(sound: jlong, speed: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_speed(speed)).simply_err();
    }

    fn nsetBalance(sound: jlong, balance: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_balance(balance)).simply_err();
    }

    fn nsetDoppler(sound: jlong, doppler: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_doppler(doppler)).simply_err();
    }

    fn nsetRolloff(sound: jlong, rolloff: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_rolloff(rolloff)).simply_err();
    }

    fn nsetWet(sound: jlong, wet: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_wet(wet)).simply_err();
    }

    fn nsetDry(sound: jlong, dry: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_dry(dry)).simply_err();
    }

    fn nsetFeedback(sound: jlong, feedback: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_feedback(feedback)).simply_err();
    }

    fn nisPlaying(sound: jlong) -> jboolean {
        with_czernice_playback(sound, |sound| sound.is_playing()).or_default_with_err(false)
    }

     fn nsetLooping(sound: jlong, looping: jboolean) {
        with_czernice_playback(sound, |sound| sound.set_looping(looping)).simply_err();
    }

    fn nsetEqGain(sound: jlong, gain_submit_info: jlong) {
        let eq_gain_submit_info = unsafe { &mut *(gain_submit_info.cast_unsigned() as *mut CzerniceEqGainModificationSubmitInfo) };
        with_czernice_playback(sound, |sound| sound.set_eq_gain(eq_gain_submit_info)).simply_err();
    }

    fn nsetEqBand(sound: jlong, band_submit_info: jlong) {
        let eq_band_submit_info = unsafe { &mut *(band_submit_info.cast_unsigned() as *mut CzerniceEqBandModificationSubmitInfo) };
        with_czernice_playback(sound, |sound| sound.set_eq_band(eq_band_submit_info)).simply_err();
    }

    fn ngetEqBandInfo(sound: jlong, count: jlong, band_info: jlong) {
        let count_ptr = unsafe { &mut *(count.cast_unsigned() as *mut u32) };
        let band_info_ptr = unsafe { &mut *(band_info.cast_unsigned() as *mut CzerniceEqBandInfo) };
        with_czernice_playback(sound, |sound| sound.get_eq_band_info(count_ptr, band_info_ptr)).simply_err();
    }

    fn nsetSampleRateReduction(sound: jlong, reduction: jint) {
        with_czernice_playback(sound, |sound| sound.set_sample_rate_reduction(reduction.cast_unsigned())).simply_err();
    }

    fn nsetBitDepth(sound: jlong, depth: jint) {
        with_czernice_playback(sound, |sound| sound.set_bit_depth(depth.cast_unsigned())).simply_err();
    }

    // fn nsetOnEndCallback(sound: jlong, callback: jobject) {
    //
    // }
});