mod engine;
mod playback;

use std::alloc::Layout;
use std::ffi::c_void;
use std::ptr;
use crate::engine::{CzerniceAudioEngine, CzerniceSoundCreateInfo};
use crate::playback::{with_czernice_playback, with_czernice_playback_faulty, CzernicePlayback, EqBandInfo, EqBandModificationSubmitInfo, EqGainModificationSubmitInfo};
use jni::errors::{Error, LogErrorAndDefault};
use jni::objects::{JByteBuffer, JClass};
use jni::sys::{jboolean, jfloat, jint, jlong, jobject};
use jni::{jni_sig, jni_str, EnvUnowned};
use libc::size_t;
use maudio::MaResult;
use mvutils::lazy;
use mvutils::version::Version;
use paste::paste;

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
        CzerniceAudioEngine::new().map(|sth| sth.addr() as jlong).or_default_with_err(0)
    }

    fn nretireEngine(_env, _class, engine: jlong) {
        let engine = unsafe { &mut *(engine.cast_unsigned() as *mut CzerniceAudioEngine) };
        CzerniceAudioEngine::retire_engine(engine)
    }

    fn ncreateSound(_env, _class, engine: jlong, create_info: jlong) -> jlong {
        let ci = unsafe { &mut *(create_info.cast_unsigned() as *mut CzerniceSoundCreateInfo) };
        let engine = unsafe { &mut *(engine.cast_unsigned() as *mut CzerniceAudioEngine) };

        engine.create_sound(ci).map(|sth| sth.addr() as jlong).or_default_with_err(0)
    }

    fn nretireSound(_env, _class, sound: jlong) {
        let sound = unsafe { &mut *(sound.cast_unsigned() as *mut CzernicePlayback) };
        CzernicePlayback::retire_sound(sound);
    }

    fn nstartSound(_env, _class, sound: jlong) -> jboolean {
        with_czernice_playback_faulty(sound, |sound| sound.start()).is_ok_with_err()
    }

    fn nstopSound(_env, _class, sound: jlong) -> jboolean {
        with_czernice_playback_faulty(sound, |sound| sound.stop()).is_ok_with_err()
    }

    fn nstopFade(_env, _class, sound: jlong, fade: jlong) -> jboolean {
         with_czernice_playback_faulty(sound, |sound| sound.stop_fade(fade.cast_unsigned())).is_ok_with_err()
    }

    fn nsetVolume(_env, _class, sound: jlong, volume: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_volume(volume)).simply_err()
    }

    fn nsetSpeed(_env, _class, sound: jlong, speed: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_speed(speed)).simply_err()
    }

    fn nsetBalance(_env, _class, sound: jlong, balance: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_balance(balance)).simply_err()
    }

    fn nsetDoppler(_env, _class, sound: jlong, doppler: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_doppler(doppler)).simply_err()
    }

    fn nsetRolloff(_env, _class, sound: jlong, rolloff: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_rolloff(rolloff)).simply_err()
    }

    fn nsetWet(_env, _class, sound: jlong, wet: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_wet(wet)).simply_err()
    }

    fn nsetDry(_env, _class, sound: jlong, dry: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_dry(dry)).simply_err()
    }

    fn nsetDecayFrames(_env, _class, sound: jlong, decay_frames: jfloat) {
        with_czernice_playback(sound, |sound| sound.set_decay_frames(decay_frames)).simply_err()
    }

    fn nisPlaying(_env, _class, sound: jlong) -> jboolean {
        with_czernice_playback(sound, |sound| sound.is_playing()).or_default_with_err(false)
    }

     fn nsetLooping(_env, _class, sound: jlong, looping: jboolean) {
        with_czernice_playback(sound, |sound| sound.set_looping(looping)).simply_err()
    }

    fn nsetEqGain(_env, _class, sound: jlong, gain_submit_info: jlong) {
        let eq_gain_submit_info = unsafe { &mut *(gain_submit_info.cast_unsigned() as *mut EqGainModificationSubmitInfo) };
        with_czernice_playback(sound, |sound| sound.set_eq_gain(eq_gain_submit_info)).simply_err()
    }

    fn nsetEqBand(_env, _class, sound: jlong, band_submit_info: jlong) {
        let eq_band_submit_info = unsafe { &mut *(band_submit_info.cast_unsigned() as *mut EqBandModificationSubmitInfo) };
        with_czernice_playback(sound, |sound| sound.set_eq_band(eq_band_submit_info)).simply_err()
    }

    fn ngetEqBandInfo(_env, _class, sound: jlong, count: jlong, band_info: jlong) {
        let count_ptr = unsafe { &mut *(count.cast_unsigned() as *mut usize) };
        let band_info_ptr = unsafe { &mut *(band_info.cast_unsigned() as *mut EqBandInfo) };
        with_czernice_playback(sound, |sound| sound.get_eq_band_info(count_ptr, band_info_ptr)).simply_err()
    }

    fn nsetSampleRateReduction(_env, _class, sound: jlong, reduction: jint) {
        with_czernice_playback(sound, |sound| sound.set_sample_rate_reduction(reduction as u32)).simply_err()
    }

    fn nsetBitDepth(_env, _class, sound: jlong, depth: jint) {
        with_czernice_playback(sound, |sound| sound.set_bit_depth(depth as u32)).simply_err()
    }

    fn getPointerSize(_env, _class) -> jint {
        size_of::<*const u32>() as jint
    }

    fn nmemAlloc(_env, _class, size: jlong) -> jlong {
        unsafe {
            libc::malloc(size as size_t).addr() as jlong
        }
    }

    fn nmemCalloc(_env, _class, nobj: jlong, size: jlong) -> jlong {
        unsafe {
            libc::calloc(nobj as usize, size as usize).addr() as jlong
        }
    }

    fn nmemFree(_env, _class, address: jlong) {
        unsafe {
            libc::free(address as *mut _)
        }
    }

    fn nmemAddress(env, _class, buffer: jobject) -> jlong {
        unsafe {
            env.with_env(|a| {
                a.get_direct_buffer_address(&JByteBuffer::from_raw(a, buffer)).map(|addr| addr as jlong)
            }).resolve::<LogErrorAndDefault>()
        }
    }

    fn nmemByteBuffer(env, _class, address: jlong, capacity: jint) -> jobject {
        unsafe {
            env.with_env(|a| {
                Ok::<_, Error>(a.new_direct_byte_buffer(address as *mut u8, capacity as usize)
                    .map(|buffer| buffer.into_raw())
                    .unwrap_or(std::ptr::null_mut()))
            }).resolve::<LogErrorAndDefault>()
        }
    }
}