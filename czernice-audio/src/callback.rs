use jni::objects::{Global, JObject};
use jni::{jni_sig, JavaVM, jni_str};

pub struct CzerniceCallbackData {
    pub jvm: JavaVM,
    pub callback: Global<JObject<'static>>,
}

pub unsafe extern "C" fn on_czernice_sound_end(
    user_data: *mut core::ffi::c_void,
    _sound: *mut maudio::maudio_sys::ffi::ma_sound
) {
    if user_data.is_null() {
        return;
    }

    let data = unsafe { Box::from_raw(user_data as *mut CzerniceCallbackData) };

    // retarded code is retarded and something something calling retire sounds fucks this function midway through the call
    std::thread::spawn(move || {
        data.jvm.attach_current_thread(|env| {
            let obj = data.callback.as_obj();
            // ts shit so ass lifetime error so i have to map this value :wilted_rose:
            env.call_method(
                obj,
                jni_str!("run"),
                jni_sig!("()V"),
                &[]
            ).map(|_| ())
        }).unwrap();
    });
}