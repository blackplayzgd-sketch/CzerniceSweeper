package net.xoknox.czernicesweeper.czerniceaudio.mem;

import java.nio.*;

import static net.xoknox.czernicesweeper.czerniceaudio.mem.Pointer._64_BIT;

public class MemoryUtil {

    public static final long NULL = 0L;
    public static final boolean CHECKS = false;

    static final sun.misc.Unsafe UNSAFE = getUnsafeInstance();

    // LWJGL Code
    private static sun.misc.Unsafe getUnsafeInstance() {
        java.lang.reflect.Field[] fields = sun.misc.Unsafe.class.getDeclaredFields();

        /*
        Different runtimes use different names for the Unsafe singleton,
        so we cannot use .getDeclaredField and we scan instead. For example:

        Oracle: theUnsafe
        PERC : m_unsafe_instance
        Android: THE_ONE
        */
        for (java.lang.reflect.Field field : fields) {
            if (!field.getType().equals(sun.misc.Unsafe.class)) {
                continue;
            }

            int modifiers = field.getModifiers();
            if (!(java.lang.reflect.Modifier.isStatic(modifiers) && java.lang.reflect.Modifier.isFinal(modifiers))) {
                continue;
            }

            try {
                field.setAccessible(true);
                return (sun.misc.Unsafe)field.get(null);
            } catch (Exception ignored) {
            }
            break;
        }

        throw new RuntimeException("Failed to get unsafe");
    }

    ///////////////////////////
    //      ALLOCATION       //
    ///////////////////////////
    public static native long nmalloc_aligned(long align, long size);
    public static native long nmalloc(long size);

    public static native long ncalloc(long count, long size);

    public static native long nrealloc(long ptr, long size);

    public static native void nfree(long ptr);

    ///////////////////////////
    //    MEM OPERATIONS     //
    ///////////////////////////
    public static native void nmem_cpy(long dst, long src, long size);
    public static native void nmem_move(long dst, long src, long size);

    public static native void nmem_set(long ptr, int value, long size);

    public static native int nmem_cmp(long a, long b, long size);

    public static native long nmem_address(Buffer buf);
    public static native ByteBuffer nmem_byte_buffer(long address, int capacity);

    public static native int getPointerSize();


    ///////////////////////////
    //      ALLOCATION       //
    ///////////////////////////
    public static long malloc(long align, long size) {
        if (CHECKS) {
            if (align == 0) throw new IllegalStateException("Alignment cannot be 0");
            if (size == 0) return NULL;
        }

        return nmalloc_aligned(align, size);
    }

    public static long malloc(long size) {
        if (CHECKS) {
            if (size == 0) return NULL;
        }

        return nmalloc(size);
    }

    public static long calloc(long count, long size) {
        if (CHECKS) {
            if (size == 0 || count == 0) return NULL;
        }

        return ncalloc(count, size);
    }

    public static long calloc(long size) {
        return calloc(1, size);
    }

    public static long realloc(long ptr, long size) {
        if (CHECKS) {
            if (ptr == NULL) throw new IllegalStateException("Tried to realloc a NULL pointer");
            if (size == 0) return NULL;
        }

        return nrealloc(ptr, size);
    }

    public static long realloc(long ptr, long align, long oldSize, long size) {
        if (CHECKS) {
            if (ptr == NULL) throw new IllegalStateException("Tried to realloc a NULL pointer");
            if (size == 0) return NULL;
        }

        long new_ptr = malloc(align, size);
        mem_cpy(new_ptr, ptr, Math.min(oldSize, size));
        return new_ptr;
    }

    public static void free(long ptr) {
        if (CHECKS) {
            if (ptr == NULL) throw new IllegalStateException("Cannot free a NULL pointer");
        }

        nfree(ptr);
    }

    ///////////////////////////
    //    MEM OPERATIONS     //
    ///////////////////////////
    public static void mem_cpy(long dst, long src, long size) {
        if (CHECKS) {
            if (size == 0) return;
            if (dst == NULL) throw new IllegalStateException("Destination can not be NULL");
            if (src == NULL) throw new IllegalStateException("Source can not be NULL");
            if (src + size >= dst) throw new IllegalStateException("Source and destination can not overlap");
        }

        nmem_cpy(dst, src, size);
    }

    public static void mem_move(long dst, long src, long size) {
        if (CHECKS) {
            if (size == 0) return;
            if (dst == NULL) throw new IllegalStateException("Destination can not be NULL");
            if (src == NULL) throw new IllegalStateException("Source can not be NULL");
        }

        nmem_move(dst, src, size);
    }

    public static void mem_set(long ptr, int value, long size) {
        if (CHECKS) {
            if (size == 0) return;
            if (ptr == NULL) throw new IllegalStateException("Destination can not be NULL");
        }

        nmem_set(ptr, value, size);
    }

    public static int mem_cmp(long a, long b, long size) {
        return nmem_cmp(a, b, size);
    }

    public static ByteBuffer mem_byte_buffer(long address, int capacity) {
        if (CHECKS) {
            if (address == NULL) throw new IllegalStateException("Address may not be NULL");
        }

        return nmem_byte_buffer(address, capacity);
    }

    public static long memAddress(ByteBuffer buf) {
        if (CHECKS) {
            if (buf == null) throw new NullPointerException("Buffer may not be null");
        }

        return nmem_address(buf);
    }

    public static long memAddress(ShortBuffer buf) {
        if (CHECKS) {
            if (buf == null) throw new NullPointerException("Buffer may not be null");
        }

        return nmem_address(buf) + buf.position() * 2L;
    }

    public static long memAddress(IntBuffer buf) {
        if (CHECKS) {
            if (buf == null) throw new NullPointerException("Buffer may not be null");
        }

        return nmem_address(buf) + buf.position() * 4L;
    }

    public static long memAddress(LongBuffer buf) {
        if (CHECKS) {
            if (buf == null) throw new NullPointerException("Buffer may not be null");
        }

        return nmem_address(buf) + buf.position() * 8L;
    }

    public static long memAddress(FloatBuffer buf) {
        if (CHECKS) {
            if (buf == null) throw new NullPointerException("Buffer may not be null");
        }

        return nmem_address(buf) + buf.position() * 4L;
    }

    public static long memAddress(DoubleBuffer buf) {
        if (CHECKS) {
            if (buf == null) throw new NullPointerException("Buffer may not be null");
        }

        return nmem_address(buf) + buf.position() * 8L;
    }


    public static ByteBuffer mallocBytes(long align, int size) {
        return mem_byte_buffer(malloc(align, size), size).order(ByteOrder.nativeOrder());
    }

    public static ByteBuffer mallocBytes(int size) {
        return mem_byte_buffer(malloc(size), size).order(ByteOrder.nativeOrder());
    }

    public static IntBuffer mallocInts(long align, int size) {
        return mallocBytes(align, Integer.BYTES * size).asIntBuffer();
    }

    public static IntBuffer mallocInts(int size) {
        return mallocBytes(Integer.BYTES * size).asIntBuffer();
    }

    public static ByteBuffer callocBytes(int size) {
        return mem_byte_buffer(calloc(1, size), size);
    }

    public static IntBuffer callocInts(int size) {
        return callocBytes(Integer.BYTES * size).asIntBuffer();
    }

    // Setters

    public static void putByte(long address, byte b) {
        UNSAFE.putByte(address, b);
    }

    public static void putShort(long address, short b) {
        UNSAFE.putShort(address, b);
    }

    public static void putInt(long address, int b) {
        UNSAFE.putInt(address, b);
    }

    public static void putLong(long address, long b) {
        UNSAFE.putLong(address, b);
    }

    public static void putFloat(long address, float b) {
        UNSAFE.putFloat(address, b);
    }

    public static void putDouble(long address, double b) {
        UNSAFE.putDouble(address, b);
    }

    public static void putAddress(long address, long value) {
        if (_64_BIT) {
            putLong(address, value);
        } else {
            putInt(address, (int) value);
        }
    }

    // Getters

    public static byte getByte(long address) {
        return UNSAFE.getByte(address);
    }

    public static short getShort(long address) {
        return UNSAFE.getShort(address);
    }

    public static int getInt(long address) {
        return UNSAFE.getInt(address);
    }

    public static long getLong(long address) {
        return UNSAFE.getLong(address);
    }

    public static float getFloat(long address) {
        return UNSAFE.getFloat(address);
    }

    public static double getDouble(long address) {
        return UNSAFE.getDouble(address);
    }

    public static long getAddress(long address) {
        return _64_BIT
                ? getLong(address)
                : getInt(address) & 0xFFFF_FFFFL;
    }

}
