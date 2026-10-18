package net.xoknox.czernicesweeper.czerniceaudio.mem.struct;

import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryStack;
import net.xoknox.czernicesweeper.czerniceaudio.mem.Struct;
import net.xoknox.czernicesweeper.czerniceaudio.mem.StructBuffer;

import java.nio.ByteBuffer;

import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.*;

/**
 *  Layout:
 *  <pre><code>
 *  struct EqBandInfo {
 *      f64 frequency,
 *      f64 q,
 *      f32 gain_db,
 *      SampleRate sample_rate
 *  }</code></pre>
 */
public class CzerniceEqBandInfo extends Struct<CzerniceEqBandInfo> {

    private static final int SIZEOF;

    public static final int
            OF_FREQUENCY,
            OF_Q,
            OF_GAIN_DB,
            OF_SAMPLE_RATE;

    static {
        Layout layout = __struct(
                __f64(),
                __f64(),
                __f32(),
                __u32()
        );

        OF_FREQUENCY = layout.offsetof(0);
        OF_Q = layout.offsetof(1);
        OF_GAIN_DB = layout.offsetof(2);
        OF_SAMPLE_RATE = layout.offsetof(3);

        SIZEOF = layout.getSize();
    }

    public CzerniceEqBandInfo(long address, ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected CzerniceEqBandInfo create(long address, ByteBuffer container) {
        return new CzerniceEqBandInfo(address, container);
    }

    public CzerniceEqBandInfo frequency(double frequency) {
        putDouble(address() + OF_FREQUENCY, frequency);
        return this;
    }

    public CzerniceEqBandInfo q(double q) {
        putDouble(address() + OF_Q, q);
        return this;
    }

    public CzerniceEqBandInfo gainDb(float gain) {
        putFloat(address() + OF_GAIN_DB, gain);
        return this;
    }

    public CzerniceEqBandInfo sampleRate(int sampleRate) {
        putInt(address() + OF_SAMPLE_RATE, sampleRate);
        return this;
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public static CzerniceEqBandInfo malloc() {
        return new CzerniceEqBandInfo(memAlloc(SIZEOF), null);
    }

    public static CzerniceEqBandInfo calloc() {
        return new CzerniceEqBandInfo(memCalloc(SIZEOF), null);
    }

    public static CzerniceEqBandInfo create(long address) {
        return new CzerniceEqBandInfo(address, null);
    }

    public static CzerniceEqBandInfo malloc(MemoryStack stack) {
        return new CzerniceEqBandInfo(stack.malloc(SIZEOF), null);
    }

    public static CzerniceEqBandInfo calloc(MemoryStack stack) {
        return new CzerniceEqBandInfo(stack.calloc(SIZEOF), null);
    }

    public static class Buffer extends StructBuffer<CzerniceEqBandInfo, Buffer> {

        private static final CzerniceEqBandInfo FACTORY = CzerniceEqBandInfo.create(-1L);

        public Buffer(ByteBuffer container, int capacity) {
            super(container, capacity);
        }

        public Buffer(long address, int capacity) {
            super(address, capacity);
        }

        public static Buffer malloc(int capacity) {
            return new Buffer(memAlloc(SIZEOF), capacity);
        }

        public static Buffer calloc(int capacity) {
            return new Buffer(memCalloc(SIZEOF), capacity);
        }

        public static Buffer malloc(int capacity, MemoryStack stack) {
            return new Buffer(stack.malloc(SIZEOF), capacity);
        }

        public static Buffer calloc(int capacity, MemoryStack stack) {
            return new Buffer(stack.calloc(SIZEOF), capacity);
        }

        @Override
        public CzerniceEqBandInfo getElementFactory() {
            return FACTORY;
        }
    }

}
