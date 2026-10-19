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
 *     f32 reverb_delay,
 *     usize num_filters,
 *     *const CzerniceEqBandInfo p_eq_info,
 *     u32 eq_info_count
 *  }</code></pre>
 */
public class CzerniceSoundCreateInfo extends Struct<CzerniceSoundCreateInfo> {

    public static final int SIZEOF, ALIGNOF;

    protected static final int
                OF_P_DATA,
                OF_DATA_COUNT,
                OF_SAMPLE_RATE,
                OF_CHANNELS,
                OF_REVERB_DELAY,
                OF_NUM_FILTERS,
                OF_P_EQ_INFO,
                OF_EQ_INFO_COUNT;

    static {
        Layout layout = __struct(
                __member(POINTER_SIZE),
                __member(4),
                __member(4),
                __member(4),
                __member(4),
                __member(8),
                __member(POINTER_SIZE),
                __member(4)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        OF_P_DATA = layout.offsetof(0);
        OF_DATA_COUNT = layout.offsetof(1);
        OF_SAMPLE_RATE = layout.offsetof(2);
        OF_CHANNELS = layout.offsetof(3);
        OF_REVERB_DELAY = layout.offsetof(4);
        OF_NUM_FILTERS = layout.offsetof(5);
        OF_P_EQ_INFO = layout.offsetof(6);
        OF_EQ_INFO_COUNT = layout.offsetof(7);
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

    public float reverbDelay() { return nreverbDelay(address()); }

    public long numFilters() { return nnumFilters(address()); }

    public CzerniceEqBandInfo.Buffer pEqInfo() { return npEqInfo(address()); }

    public int eqInfoCount() { return neqInfoCount(address()); }


    public CzerniceSoundCreateInfo pData(ByteBuffer value) { npData(address(), value); return this; }

    public CzerniceSoundCreateInfo sampleRate(int value) { nsampleRate(address(), value); return this; }

    public CzerniceSoundCreateInfo channels(int value) { nchannels(address(), value); return this; }

    public CzerniceSoundCreateInfo reverbDelay(float value) { nreverbDelay(address(), value); return this; }

    public CzerniceSoundCreateInfo numFilters(long value) { nnumFilters(address(), value); return this; }

    public CzerniceSoundCreateInfo pEqInfo(CzerniceEqBandInfo.Buffer value) { npEqInfo(address(), value); return this; }


    public CzerniceSoundCreateInfo set(

        ByteBuffer pData,
        int sampleRate,
        int channels,
        float reverbDelay,
        long numFilters,
        CzerniceEqBandInfo.Buffer pEqInfo
    ) {
        pData(pData);
        sampleRate(sampleRate);
        channels(channels);
        reverbDelay(reverbDelay);
        numFilters(numFilters);
        pEqInfo(pEqInfo);

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

    public static CzerniceSoundCreateInfo.Buffer malloc(int capacity) {
        return new CzerniceSoundCreateInfo.Buffer(MemoryUtil.malloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static CzerniceSoundCreateInfo.Buffer calloc(int capacity) {
        return new CzerniceSoundCreateInfo.Buffer(MemoryUtil.calloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static CzerniceSoundCreateInfo.Buffer create(int capacity, long address) {
        return new CzerniceSoundCreateInfo.Buffer(address, capacity);
    }

    public static CzerniceSoundCreateInfo.Buffer malloc(int capacity, MemoryStack stack) {
        return new CzerniceSoundCreateInfo.Buffer(stack.malloc(ALIGNOF, SIZEOF * capacity), capacity);
    }

    public static CzerniceSoundCreateInfo.Buffer calloc(int capacity, MemoryStack stack) {
        return new CzerniceSoundCreateInfo.Buffer(stack.calloc(ALIGNOF, SIZEOF * capacity), capacity);
    }


    public static ByteBuffer npData(long struct) { return mem_byte_buffer(struct + CzerniceSoundCreateInfo.OF_P_DATA, ndataCount(struct)); }

    public static int ndataCount(long struct) { return getInt(struct + CzerniceSoundCreateInfo.OF_DATA_COUNT); }

    public static int nsampleRate(long struct) { return getInt(struct + CzerniceSoundCreateInfo.OF_SAMPLE_RATE); }

    public static int nchannels(long struct) { return getInt(struct + CzerniceSoundCreateInfo.OF_CHANNELS); }

    public static float nreverbDelay(long struct) { return getFloat(struct + CzerniceSoundCreateInfo.OF_REVERB_DELAY); }

    public static long nnumFilters(long struct) { return getAddress(struct + CzerniceSoundCreateInfo.OF_NUM_FILTERS); }

    public static CzerniceEqBandInfo.Buffer npEqInfo(long struct) { return new CzerniceEqBandInfo.Buffer(struct + CzerniceSoundCreateInfo.OF_P_EQ_INFO, neqInfoCount(struct)); }

    public static int neqInfoCount(long struct) { return getInt(struct + CzerniceSoundCreateInfo.OF_EQ_INFO_COUNT); }


    public static void npData(long struct, ByteBuffer value) { putAddress(struct + CzerniceSoundCreateInfo.OF_P_DATA, memAddress(value)); }

    public static void ndataCount(long struct, int value) { putInt(struct + CzerniceSoundCreateInfo.OF_DATA_COUNT, value); }

    public static void nsampleRate(long struct, int value) { putInt(struct + CzerniceSoundCreateInfo.OF_SAMPLE_RATE, value); }

    public static void nchannels(long struct, int value) { putInt(struct + CzerniceSoundCreateInfo.OF_CHANNELS, value); }

    public static void nreverbDelay(long struct, float value) { putFloat(struct + CzerniceSoundCreateInfo.OF_REVERB_DELAY, value); }

    public static void nnumFilters(long struct, long value) { putAddress(struct + CzerniceSoundCreateInfo.OF_NUM_FILTERS, value); }

    public static void npEqInfo(long struct, CzerniceEqBandInfo.Buffer value) { putAddress(struct + CzerniceSoundCreateInfo.OF_P_EQ_INFO, value.address()); }

    public static void neqInfoCount(long struct, int value) { putInt(struct + CzerniceSoundCreateInfo.OF_EQ_INFO_COUNT, value); }


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