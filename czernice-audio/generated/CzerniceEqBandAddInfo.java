package net.xoknox.czernicesweeper.czerniceaudio.structs;

import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryStack;
import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil;
import net.xoknox.czernicesweeper.czerniceaudio.mem.Struct;
import net.xoknox.czernicesweeper.czerniceaudio.mem.StructBuffer;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.*;

/**
 *  CzerniceEqBandAddInfo Java Struct
 *
 *  <pre><code>
 *  struct CzerniceEqBandAddInfo {
 *     usize index,
 *     f64 frequency,
 *     f64 q,
 *     f32 gain_db,
 *     u32 sample_rate
 *  }</code></pre>
 */
public class CzerniceEqBandAddInfo extends Struct<CzerniceEqBandAddInfo> {

    public static final int SIZEOF, ALIGNOF;

    protected static final int
                OF_INDEX,
                OF_FREQUENCY,
                OF_Q,
                OF_GAIN_DB,
                OF_SAMPLE_RATE;

    static {
        Layout layout = __struct(
                __member(8),
                __member(8),
                __member(8),
                __member(4),
                __member(4)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        OF_INDEX = layout.offsetof(0);
        OF_FREQUENCY = layout.offsetof(1);
        OF_Q = layout.offsetof(2);
        OF_GAIN_DB = layout.offsetof(3);
        OF_SAMPLE_RATE = layout.offsetof(4);
    }

    protected CzerniceEqBandAddInfo(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected CzerniceEqBandAddInfo create(long address, @Nullable ByteBuffer container) {
        return new CzerniceEqBandAddInfo(address, container);
    }

    public CzerniceEqBandAddInfo(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    public long index() { return nindex(address()); }

    public double frequency() { return nfrequency(address()); }

    public double q() { return nq(address()); }

    public float gainDb() { return ngainDb(address()); }

    public int sampleRate() { return nsampleRate(address()); }


    public CzerniceEqBandAddInfo index(long value) { nindex(address(), value); return this; }

    public CzerniceEqBandAddInfo frequency(double value) { nfrequency(address(), value); return this; }

    public CzerniceEqBandAddInfo q(double value) { nq(address(), value); return this; }

    public CzerniceEqBandAddInfo gainDb(float value) { ngainDb(address(), value); return this; }

    public CzerniceEqBandAddInfo sampleRate(int value) { nsampleRate(address(), value); return this; }


    public CzerniceEqBandAddInfo set(

        long index,
        double frequency,
        double q,
        float gainDb,
        int sampleRate
    ) {
        index(index);
        frequency(frequency);
        q(q);
        gainDb(gainDb);
        sampleRate(sampleRate);

        return this;
    }

    public CzerniceEqBandAddInfo set(CzerniceEqBandAddInfo src) {
        mem_cpy(address(), src.address(), SIZEOF);
        return this;
    }


    public static CzerniceEqBandAddInfo malloc() {
        return new CzerniceEqBandAddInfo(MemoryUtil.malloc(SIZEOF), null);
    }

    public static CzerniceEqBandAddInfo calloc() {
        return new CzerniceEqBandAddInfo(MemoryUtil.calloc(SIZEOF), null);
    }

    public static CzerniceEqBandAddInfo create(long address) {
        return new CzerniceEqBandAddInfo(address, null);
    }

    public static CzerniceEqBandAddInfo malloc(MemoryStack stack) {
        return new CzerniceEqBandAddInfo(stack.malloc(SIZEOF), null);
    }

    public static CzerniceEqBandAddInfo calloc(MemoryStack stack) {
        return new CzerniceEqBandAddInfo(stack.calloc(SIZEOF), null);
    }

    public static CzerniceEqBandAddInfo.Buffer malloc(int capacity) {
        return new CzerniceEqBandAddInfo.Buffer(MemoryUtil.malloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static CzerniceEqBandAddInfo.Buffer calloc(int capacity) {
        return new CzerniceEqBandAddInfo.Buffer(MemoryUtil.calloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static CzerniceEqBandAddInfo.Buffer create(int capacity, long address) {
        return new CzerniceEqBandAddInfo.Buffer(address, capacity);
    }

    public static CzerniceEqBandAddInfo.Buffer malloc(int capacity, MemoryStack stack) {
        return new CzerniceEqBandAddInfo.Buffer(stack.malloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static CzerniceEqBandAddInfo.Buffer calloc(int capacity, MemoryStack stack) {
        return new CzerniceEqBandAddInfo.Buffer(stack.calloc(ALIGNOF, SIZEOF * capacity), capacity);
    }


    public static long nindex(long struct) { return getAddress(struct + CzerniceEqBandAddInfo.OF_INDEX); }

    public static double nfrequency(long struct) { return getDouble(struct + CzerniceEqBandAddInfo.OF_FREQUENCY); }

    public static double nq(long struct) { return getDouble(struct + CzerniceEqBandAddInfo.OF_Q); }

    public static float ngainDb(long struct) { return getFloat(struct + CzerniceEqBandAddInfo.OF_GAIN_DB); }

    public static int nsampleRate(long struct) { return getInt(struct + CzerniceEqBandAddInfo.OF_SAMPLE_RATE); }


    public static void nindex(long struct, long value) { putAddress(struct + CzerniceEqBandAddInfo.OF_INDEX, value); }

    public static void nfrequency(long struct, double value) { putDouble(struct + CzerniceEqBandAddInfo.OF_FREQUENCY, value); }

    public static void nq(long struct, double value) { putDouble(struct + CzerniceEqBandAddInfo.OF_Q, value); }

    public static void ngainDb(long struct, float value) { putFloat(struct + CzerniceEqBandAddInfo.OF_GAIN_DB, value); }

    public static void nsampleRate(long struct, int value) { putInt(struct + CzerniceEqBandAddInfo.OF_SAMPLE_RATE, value); }


    public static class Buffer extends StructBuffer<CzerniceEqBandAddInfo, Buffer> {
        private static final CzerniceEqBandAddInfo ELEMENT_FACTORY = CzerniceEqBandAddInfo.create(-1L);

        public Buffer(ByteBuffer container) {
            super(container, container.remaining() / SIZEOF);
        }

        public Buffer(long address, int cap) {
            super(address, cap);
        }

        @Override
        protected Buffer self() { return this; }

        @Override
        protected Buffer create(long address, int capacity) {
            return new Buffer(address, capacity);
        }

        @Override
        public CzerniceEqBandAddInfo getElementFactory() {
            return ELEMENT_FACTORY;
        }
    }
}