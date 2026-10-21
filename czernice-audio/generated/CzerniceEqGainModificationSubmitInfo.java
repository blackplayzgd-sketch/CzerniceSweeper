package net.xoknox.czernicesweeper.czerniceaudio.structs;

import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryStack;
import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil;
import net.xoknox.czernicesweeper.czerniceaudio.mem.Struct;
import net.xoknox.czernicesweeper.czerniceaudio.mem.StructBuffer;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.*;

/**
 *  CzerniceEqGainModificationSubmitInfo Java Struct
 *
 *  <pre><code>
 *  struct CzerniceEqGainModificationSubmitInfo {
 *     usize band,
 *     f32 gain_db,
 *     u32 sample_rate
 *  }</code></pre>
 */
public class CzerniceEqGainModificationSubmitInfo extends Struct<CzerniceEqGainModificationSubmitInfo> {

    public static final int SIZEOF, ALIGNOF;

    protected static final int
                OF_BAND,
                OF_GAIN_DB,
                OF_SAMPLE_RATE;

    static {
        Layout layout = __struct(
                __member(POINTER_SIZE),
                __member(4),
                __member(4)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        OF_BAND = layout.offsetof(0);
        OF_GAIN_DB = layout.offsetof(1);
        OF_SAMPLE_RATE = layout.offsetof(2);
    }

    protected CzerniceEqGainModificationSubmitInfo(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected CzerniceEqGainModificationSubmitInfo create(long address, @Nullable ByteBuffer container) {
        return new CzerniceEqGainModificationSubmitInfo(address, container);
    }

    public CzerniceEqGainModificationSubmitInfo(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    public long band() { return nband(address()); }

    public float gainDb() { return ngainDb(address()); }

    public int sampleRate() { return nsampleRate(address()); }


    public CzerniceEqGainModificationSubmitInfo band(long value) { nband(address(), value); return this; }

    public CzerniceEqGainModificationSubmitInfo gainDb(float value) { ngainDb(address(), value); return this; }

    public CzerniceEqGainModificationSubmitInfo sampleRate(int value) { nsampleRate(address(), value); return this; }


    public CzerniceEqGainModificationSubmitInfo set(
        long band,
        float gainDb,
        int sampleRate
    ) {
        band(band);
        gainDb(gainDb);
        sampleRate(sampleRate);

        return this;
    }

    public CzerniceEqGainModificationSubmitInfo set(CzerniceEqGainModificationSubmitInfo src) {
        mem_cpy(address(), src.address(), SIZEOF);
        return this;
    }


    public static CzerniceEqGainModificationSubmitInfo malloc() {
        return new CzerniceEqGainModificationSubmitInfo(MemoryUtil.malloc(SIZEOF), null);
    }

    public static CzerniceEqGainModificationSubmitInfo calloc() {
        return new CzerniceEqGainModificationSubmitInfo(MemoryUtil.calloc(SIZEOF), null);
    }

    public static CzerniceEqGainModificationSubmitInfo create(long address) {
        return new CzerniceEqGainModificationSubmitInfo(address, null);
    }

    public static CzerniceEqGainModificationSubmitInfo malloc(MemoryStack stack) {
        return new CzerniceEqGainModificationSubmitInfo(stack.malloc(SIZEOF), null);
    }

    public static CzerniceEqGainModificationSubmitInfo calloc(MemoryStack stack) {
        return new CzerniceEqGainModificationSubmitInfo(stack.calloc(SIZEOF), null);
    }

    public static CzerniceEqGainModificationSubmitInfo.Buffer malloc(int capacity) {
        return new CzerniceEqGainModificationSubmitInfo.Buffer(MemoryUtil.malloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static CzerniceEqGainModificationSubmitInfo.Buffer calloc(int capacity) {
        return new CzerniceEqGainModificationSubmitInfo.Buffer(MemoryUtil.calloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static CzerniceEqGainModificationSubmitInfo.Buffer create(int capacity, long address) {
        return new CzerniceEqGainModificationSubmitInfo.Buffer(address, capacity);
    }

    public static CzerniceEqGainModificationSubmitInfo.Buffer malloc(int capacity, MemoryStack stack) {
        return new CzerniceEqGainModificationSubmitInfo.Buffer(stack.malloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static CzerniceEqGainModificationSubmitInfo.Buffer calloc(int capacity, MemoryStack stack) {
        return new CzerniceEqGainModificationSubmitInfo.Buffer(stack.calloc(ALIGNOF, SIZEOF * capacity), capacity);
    }


    public static long nband(long struct) { return getAddress(struct + CzerniceEqGainModificationSubmitInfo.OF_BAND); }

    public static float ngainDb(long struct) { return getFloat(struct + CzerniceEqGainModificationSubmitInfo.OF_GAIN_DB); }

    public static int nsampleRate(long struct) { return getInt(struct + CzerniceEqGainModificationSubmitInfo.OF_SAMPLE_RATE); }


    public static void nband(long struct, long value) { putAddress(struct + CzerniceEqGainModificationSubmitInfo.OF_BAND, value); }

    public static void ngainDb(long struct, float value) { putFloat(struct + CzerniceEqGainModificationSubmitInfo.OF_GAIN_DB, value); }

    public static void nsampleRate(long struct, int value) { putInt(struct + CzerniceEqGainModificationSubmitInfo.OF_SAMPLE_RATE, value); }


    public static class Buffer extends StructBuffer<CzerniceEqGainModificationSubmitInfo, Buffer> {
        private static final CzerniceEqGainModificationSubmitInfo ELEMENT_FACTORY = CzerniceEqGainModificationSubmitInfo.create(-1L);

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
        public CzerniceEqGainModificationSubmitInfo getElementFactory() {
            return ELEMENT_FACTORY;
        }
    }
}