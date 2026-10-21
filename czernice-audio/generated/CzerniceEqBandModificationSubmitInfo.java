package net.xoknox.czernicesweeper.czerniceaudio.structs;

import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryStack;
import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil;
import net.xoknox.czernicesweeper.czerniceaudio.mem.Struct;
import net.xoknox.czernicesweeper.czerniceaudio.mem.StructBuffer;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.*;

/**
 *  CzerniceEqBandModificationSubmitInfo Java Struct
 *
 *  <pre><code>
 *  struct CzerniceEqBandModificationSubmitInfo {
 *     usize band,
 *     f64 frequency,
 *     f64 q
 *  }</code></pre>
 */
public class CzerniceEqBandModificationSubmitInfo extends Struct<CzerniceEqBandModificationSubmitInfo> {

    public static final int SIZEOF, ALIGNOF;

    protected static final int
                OF_BAND,
                OF_FREQUENCY,
                OF_Q;

    static {
        Layout layout = __struct(
                __member(POINTER_SIZE),
                __member(8),
                __member(8)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        OF_BAND = layout.offsetof(0);
        OF_FREQUENCY = layout.offsetof(1);
        OF_Q = layout.offsetof(2);
    }

    protected CzerniceEqBandModificationSubmitInfo(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected CzerniceEqBandModificationSubmitInfo create(long address, @Nullable ByteBuffer container) {
        return new CzerniceEqBandModificationSubmitInfo(address, container);
    }

    public CzerniceEqBandModificationSubmitInfo(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    public long band() { return nband(address()); }

    public double frequency() { return nfrequency(address()); }

    public double q() { return nq(address()); }


    public CzerniceEqBandModificationSubmitInfo band(long value) { nband(address(), value); return this; }

    public CzerniceEqBandModificationSubmitInfo frequency(double value) { nfrequency(address(), value); return this; }

    public CzerniceEqBandModificationSubmitInfo q(double value) { nq(address(), value); return this; }


    public CzerniceEqBandModificationSubmitInfo set(
        long band,
        double frequency,
        double q
    ) {
        band(band);
        frequency(frequency);
        q(q);

        return this;
    }

    public CzerniceEqBandModificationSubmitInfo set(CzerniceEqBandModificationSubmitInfo src) {
        mem_cpy(address(), src.address(), SIZEOF);
        return this;
    }


    public static CzerniceEqBandModificationSubmitInfo malloc() {
        return new CzerniceEqBandModificationSubmitInfo(MemoryUtil.malloc(SIZEOF), null);
    }

    public static CzerniceEqBandModificationSubmitInfo calloc() {
        return new CzerniceEqBandModificationSubmitInfo(MemoryUtil.calloc(SIZEOF), null);
    }

    public static CzerniceEqBandModificationSubmitInfo create(long address) {
        return new CzerniceEqBandModificationSubmitInfo(address, null);
    }

    public static CzerniceEqBandModificationSubmitInfo malloc(MemoryStack stack) {
        return new CzerniceEqBandModificationSubmitInfo(stack.malloc(SIZEOF), null);
    }

    public static CzerniceEqBandModificationSubmitInfo calloc(MemoryStack stack) {
        return new CzerniceEqBandModificationSubmitInfo(stack.calloc(SIZEOF), null);
    }

    public static CzerniceEqBandModificationSubmitInfo.Buffer malloc(int capacity) {
        return new CzerniceEqBandModificationSubmitInfo.Buffer(MemoryUtil.malloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static CzerniceEqBandModificationSubmitInfo.Buffer calloc(int capacity) {
        return new CzerniceEqBandModificationSubmitInfo.Buffer(MemoryUtil.calloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static CzerniceEqBandModificationSubmitInfo.Buffer create(int capacity, long address) {
        return new CzerniceEqBandModificationSubmitInfo.Buffer(address, capacity);
    }

    public static CzerniceEqBandModificationSubmitInfo.Buffer malloc(int capacity, MemoryStack stack) {
        return new CzerniceEqBandModificationSubmitInfo.Buffer(stack.malloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static CzerniceEqBandModificationSubmitInfo.Buffer calloc(int capacity, MemoryStack stack) {
        return new CzerniceEqBandModificationSubmitInfo.Buffer(stack.calloc(ALIGNOF, SIZEOF * capacity), capacity);
    }


    public static long nband(long struct) { return getAddress(struct + CzerniceEqBandModificationSubmitInfo.OF_BAND); }

    public static double nfrequency(long struct) { return getDouble(struct + CzerniceEqBandModificationSubmitInfo.OF_FREQUENCY); }

    public static double nq(long struct) { return getDouble(struct + CzerniceEqBandModificationSubmitInfo.OF_Q); }


    public static void nband(long struct, long value) { putAddress(struct + CzerniceEqBandModificationSubmitInfo.OF_BAND, value); }

    public static void nfrequency(long struct, double value) { putDouble(struct + CzerniceEqBandModificationSubmitInfo.OF_FREQUENCY, value); }

    public static void nq(long struct, double value) { putDouble(struct + CzerniceEqBandModificationSubmitInfo.OF_Q, value); }


    public static class Buffer extends StructBuffer<CzerniceEqBandModificationSubmitInfo, Buffer> {
        private static final CzerniceEqBandModificationSubmitInfo ELEMENT_FACTORY = CzerniceEqBandModificationSubmitInfo.create(-1L);

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
        public CzerniceEqBandModificationSubmitInfo getElementFactory() {
            return ELEMENT_FACTORY;
        }
    }
}