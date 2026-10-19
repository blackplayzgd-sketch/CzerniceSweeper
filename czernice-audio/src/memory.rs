use std::ffi::c_void;
use jni::errors::{Error, LogErrorAndDefault};
use jni::objects::JByteBuffer;
use jni::sys::{jbyte, jfloat, jint, jlong, jobject};
use libc::size_t;
use crate::czernicefn;

czernicefn!(net_xoknox_czernicesweeper_czerniceaudio_mem_MemoryUtil, (env, class) => {
    fn getPointerSize() -> jint {
        size_of::<*const u32>() as jint
    }

    // Allocation
    fn nmalloc_1aligned(align: jlong, size: jlong) -> jlong {
        unsafe {
            libc::aligned_alloc(align as size_t, size as size_t).addr() as jlong
        }
    }

    fn nmalloc(size: jlong) -> jlong {
        unsafe {
            libc::malloc(size as size_t).addr() as jlong
        }
    }

    fn ncalloc(count: jlong, size: jlong) -> jlong {
        unsafe {
            libc::calloc(count as size_t, size as size_t).addr() as jlong
        }
    }

    fn nrealloc(ptr: jlong, size: jlong) -> jlong {
        unsafe {
            libc::realloc(ptr as *mut c_void, size as size_t).addr() as jlong
        }
    }

    fn nfree(ptr: jlong) {
        unsafe {
            libc::free(ptr as *mut _)
        }
    }

    // Memory Operations
    fn nmem_1cpy(dst: jlong, src: jlong, size: jlong) {
        unsafe {
            libc::memcpy(dst as *mut _, src as *mut _, size as size_t);
        }
    }

    fn nmem_1move(dst: jlong, src: jlong, size: jlong) {
        unsafe {
            libc::memmove(dst as *mut _, src as *mut _, size as size_t);
        }
    }

    fn nmem_1set(ptr: jlong, value: jint, size: jlong) {
        unsafe {
            libc::memset(ptr as *mut _, value, size as size_t);
        }
    }

    fn nmem_1cmp(a: jlong, b: jlong, size: jlong) -> jint {
        unsafe {
            libc::memcmp(a as *mut _, b as *mut _, size as size_t)
        }
    }


    fn nmem_1address(buffer: JByteBuffer) -> jlong {
        unsafe {
            env.with_env(|a| {
                a.get_direct_buffer_address(&buffer).map(|addr| addr as jlong)
            }).resolve::<LogErrorAndDefault>()
        }
    }

    fn nmem_1byte_1buffer(address: jlong, capacity: jint) -> jobject {
        unsafe {
            env.with_env(|a| {
                Ok::<_, Error>(a.new_direct_byte_buffer(address as *mut u8, capacity as usize)
                    .map(|buffer| buffer.into_raw())
                    .unwrap_or(std::ptr::null_mut()))
            }).resolve::<LogErrorAndDefault>()
        }
    }
});