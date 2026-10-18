package net.xoknox.czernicesweeper.czerniceaudio.mem;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static net.xoknox.czernicesweeper.czerniceaudio.CzerniceAudio.*;
import static net.xoknox.czernicesweeper.czerniceaudio.mem.Pointer._64_BIT;

public class MemoryUtil {

    public static final long NULL = 0L;

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

    public static long memAlloc(long size) {
        if (size == 0) throw new RuntimeException("Allocation size can not be 0");
        return nmemAlloc(size);
    }

    public static long memCalloc(long nobj, long size) {
        if (size == 0 || nobj == 0) throw new RuntimeException("Neither nobj or size can not be 0");
        return nmemCalloc(nobj, size);
    }

    public static long memCalloc(long size) {
        return memCalloc(1, size);
    }

    public static void memFree(long address) {
        if (address == NULL) throw new RuntimeException("Cannot free NULL address");
        nmemFree(address);
    }

    public static long memAddress(ByteBuffer buffer) {
        if (buffer == null) throw new RuntimeException("Buffer may not be NULL");
        return nmemAddress(buffer);
    }

    public static ByteBuffer memByteBuffer(long address, int capacity) {
        if (address == NULL) throw new RuntimeException("Memory address cannot be NULL");
        if (capacity == 0) throw new RuntimeException("Capacity may not be 0");
        return nmemByteBuffer(address, capacity);
    }

    public static ByteBuffer mallocBytes(int size) {
        return memByteBuffer(memAlloc(size), size);
    }

    public static IntBuffer mallocInts(int size) {
        return mallocBytes(Integer.BYTES * size).asIntBuffer();
    }

    public static ByteBuffer callocBytes(int size) {
        return memByteBuffer(memCalloc(size), size);
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
