package net.xoknox.czernicesweeper.czerniceaudio.structs;

import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryStack;
import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil;
import net.xoknox.czernicesweeper.czerniceaudio.mem.Struct;
import net.xoknox.czernicesweeper.czerniceaudio.mem.StructBuffer;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.*;

/**
 *  CzerniceSoundCreateInfo Java Struct
 *
 *  <pre><code>
 *  struct CzerniceSoundCreateInfo {
 *     *const u8 p_data,
 *     u32 data_count,
 *     u32 sample_rate,
 *     u32 channels,
 *     usize num_filters,
 *     *const CzerniceEqBandInfo p_eq_info,
 *     u32 eq_info_count,
 *     f32 reverb_delay,
 *     f32 wet,
 *     f32 dry,
 *     f32 feedback,
 *     u32 rate_divisor,
 *     u32 bit_depth
 *  }</code></pre>
 */
public class CzerniceSoundCreateInfo extends Struct<CzerniceSoundCreateInfo> {

    public static final int SIZEOF, ALIGNOF;

    protected static final int
                OF_P_DATA,
                OF_DATA_COUNT,
                OF_SAMPLE_RATE,
                OF_CHANNELS,
                OF_NUM_FILTERS,
                OF_P_EQ_INFO,
                OF_EQ_INFO_COUNT,
                OF_REVERB_DELAY,
                OF_WET,
                OF_DRY,
                OF_FEEDBACK,
                OF_RATE_DIVISOR,
                OF_BIT_DEPTH;

    static {
        Layout layout = __struct(
                __member(POINTER_SIZE),
                __member(4),
                __member(4),
                __member(4),
                __member(8),
                __member(POINTER_SIZE),
                __member(4),
                __member(4),
                __member(4),
                __member(4),
                __member(4),
                __member(4),
                __member(4)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        OF_P_DATA = layout.offsetof(0);
        OF_DATA_COUNT = layout.offsetof(1);
        OF_SAMPLE_RATE = layout.offsetof(2);
        OF_CHANNELS = layout.offsetof(3);
        OF_NUM_FILTERS = layout.offsetof(4);
        OF_P_EQ_INFO = layout.offsetof(5);
        OF_EQ_INFO_COUNT = layout.offsetof(6);
        OF_REVERB_DELAY = layout.offsetof(7);
        OF_WET = layout.offsetof(8);
        OF_DRY = layout.offsetof(9);
        OF_FEEDBACK = layout.offsetof(10);
        OF_RATE_DIVISOR = layout.offsetof(11);
        OF_BIT_DEPTH = layout.offsetof(12);
    }

    protected CzerniceSoundCreateInfo(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected CzerniceSoundCreateInfo create(long address, @Nullable ByteBuffer container) {
        return new CzerniceSoundCreateInfo(address, container);
    }

    public CzerniceSoundCreateInfo(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    public ByteBuffer pData() { return npData(address()); }

    public int dataCount() { return ndataCount(address()); }

    public int sampleRate() { return nsampleRate(address()); }

    public int channels() { return nchannels(address()); }

    public long numFilters() { return nnumFilters(address()); }

    public CzerniceEqBandInfo.Buffer pEqInfo() { return npEqInfo(address()); }

    public int eqInfoCount() { return neqInfoCount(address()); }

    public float reverbDelay() { return nreverbDelay(address()); }

    public float wet() { return nwet(address()); }

    public float dry() { return ndry(address()); }

    public float feedback() { return nfeedback(address()); }

    public int rateDivisor() { return nrateDivisor(address()); }

    public int bitDepth() { return nbitDepth(address()); }


    public CzerniceSoundCreateInfo pData(ByteBuffer value) { npData(address(), value); return this; }

    public CzerniceSoundCreateInfo sampleRate(int value) { nsampleRate(address(), value); return this; }

    public CzerniceSoundCreateInfo channels(int value) { nchannels(address(), value); return this; }

    public CzerniceSoundCreateInfo numFilters(long value) { nnumFilters(address(), value); return this; }

    public CzerniceSoundCreateInfo pEqInfo(CzerniceEqBandInfo.Buffer value) { npEqInfo(address(), value); return this; }

    public CzerniceSoundCreateInfo reverbDelay(float value) { nreverbDelay(address(), value); return this; }

    public CzerniceSoundCreateInfo wet(float value) { nwet(address(), value); return this; }

    public CzerniceSoundCreateInfo dry(float value) { ndry(address(), value); return this; }

    public CzerniceSoundCreateInfo feedback(float value) { nfeedback(address(), value); return this; }

    public CzerniceSoundCreateInfo rateDivisor(int value) { nrateDivisor(address(), value); return this; }

    public CzerniceSoundCreateInfo bitDepth(int value) { nbitDepth(address(), value); return this; }


    public CzerniceSoundCreateInfo set(

        ByteBuffer pData,
        int sampleRate,
        int channels,
        long numFilters,
        CzerniceEqBandInfo.Buffer pEqInfo,
        float reverbDelay,
        float wet,
        float dry,
        float feedback,
        int rateDivisor,
        int bitDepth
    ) {
        pData(pData);
        sampleRate(sampleRate);
        channels(channels);
        numFilters(numFilters);
        pEqInfo(pEqInfo);
        reverbDelay(reverbDelay);
        wet(wet);
        dry(dry);
        feedback(feedback);
        rateDivisor(rateDivisor);
        bitDepth(bitDepth);

        return this;
    }

    public CzerniceSoundCreateInfo set(CzerniceSoundCreateInfo src) {
        mem_cpy(address(), src.address(), SIZEOF);
        return this;
    }


    public static CzerniceSoundCreateInfo malloc() {
        return new CzerniceSoundCreateInfo(MemoryUtil.malloc(SIZEOF), null);
    }

    public static CzerniceSoundCreateInfo calloc() {
        return new CzerniceSoundCreateInfo(MemoryUtil.calloc(SIZEOF), null);
    }

    public static CzerniceSoundCreateInfo create(long address) {
        return new CzerniceSoundCreateInfo(address, null);
    }

    public static CzerniceSoundCreateInfo malloc(MemoryStack stack) {
        return new CzerniceSoundCreateInfo(stack.malloc(SIZEOF), null);
    }

    public static CzerniceSoundCreateInfo calloc(MemoryStack stack) {
        return new CzerniceSoundCreateInfo(stack.calloc(SIZEOF), null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.malloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.calloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static Buffer create(int capacity, long address) {
        return new Buffer(address, capacity);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.malloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.calloc(ALIGNOF, SIZEOF * capacity), capacity);
    }


    public static ByteBuffer npData(long struct) { return mem_byte_buffer(struct + CzerniceSoundCreateInfo.OF_P_DATA, ndataCount(struct)); }

    public static int ndataCount(long struct) { return getInt(struct + CzerniceSoundCreateInfo.OF_DATA_COUNT); }

    public static int nsampleRate(long struct) { return getInt(struct + CzerniceSoundCreateInfo.OF_SAMPLE_RATE); }

    public static int nchannels(long struct) { return getInt(struct + CzerniceSoundCreateInfo.OF_CHANNELS); }

    public static long nnumFilters(long struct) { return getAddress(struct + CzerniceSoundCreateInfo.OF_NUM_FILTERS); }

    public static CzerniceEqBandInfo.Buffer npEqInfo(long struct) { return new CzerniceEqBandInfo.Buffer(struct + CzerniceSoundCreateInfo.OF_P_EQ_INFO, neqInfoCount(struct)); }

    public static int neqInfoCount(long struct) { return getInt(struct + CzerniceSoundCreateInfo.OF_EQ_INFO_COUNT); }

    public static float nreverbDelay(long struct) { return getFloat(struct + CzerniceSoundCreateInfo.OF_REVERB_DELAY); }

    public static float nwet(long struct) { return getFloat(struct + CzerniceSoundCreateInfo.OF_WET); }

    public static float ndry(long struct) { return getFloat(struct + CzerniceSoundCreateInfo.OF_DRY); }

    public static float nfeedback(long struct) { return getFloat(struct + CzerniceSoundCreateInfo.OF_FEEDBACK); }

    public static int nrateDivisor(long struct) { return getInt(struct + CzerniceSoundCreateInfo.OF_RATE_DIVISOR); }

    public static int nbitDepth(long struct) { return getInt(struct + CzerniceSoundCreateInfo.OF_BIT_DEPTH); }


    public static void npData(long struct, ByteBuffer value) { putAddress(struct + CzerniceSoundCreateInfo.OF_P_DATA, memAddress(value)); ndataCount(struct, value == null ? 0 : value.remaining()); }

    public static void ndataCount(long struct, int value) { putInt(struct + CzerniceSoundCreateInfo.OF_DATA_COUNT, value); }

    public static void nsampleRate(long struct, int value) { putInt(struct + CzerniceSoundCreateInfo.OF_SAMPLE_RATE, value); }

    public static void nchannels(long struct, int value) { putInt(struct + CzerniceSoundCreateInfo.OF_CHANNELS, value); }

    public static void nnumFilters(long struct, long value) { putAddress(struct + CzerniceSoundCreateInfo.OF_NUM_FILTERS, value); }

    public static void npEqInfo(long struct, CzerniceEqBandInfo.Buffer value) { putAddress(struct + CzerniceSoundCreateInfo.OF_P_EQ_INFO, value.address()); neqInfoCount(struct, value == null ? 0 : value.remaining()); }

    public static void neqInfoCount(long struct, int value) { putInt(struct + CzerniceSoundCreateInfo.OF_EQ_INFO_COUNT, value); }

    public static void nreverbDelay(long struct, float value) { putFloat(struct + CzerniceSoundCreateInfo.OF_REVERB_DELAY, value); }

    public static void nwet(long struct, float value) { putFloat(struct + CzerniceSoundCreateInfo.OF_WET, value); }

    public static void ndry(long struct, float value) { putFloat(struct + CzerniceSoundCreateInfo.OF_DRY, value); }

    public static void nfeedback(long struct, float value) { putFloat(struct + CzerniceSoundCreateInfo.OF_FEEDBACK, value); }

    public static void nrateDivisor(long struct, int value) { putInt(struct + CzerniceSoundCreateInfo.OF_RATE_DIVISOR, value); }

    public static void nbitDepth(long struct, int value) { putInt(struct + CzerniceSoundCreateInfo.OF_BIT_DEPTH, value); }


    public static class Buffer extends StructBuffer<CzerniceSoundCreateInfo, Buffer> {
        private static final CzerniceSoundCreateInfo ELEMENT_FACTORY = CzerniceSoundCreateInfo.create(-1L);

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
        public CzerniceSoundCreateInfo getElementFactory() {
            return ELEMENT_FACTORY;
        }
    }
}