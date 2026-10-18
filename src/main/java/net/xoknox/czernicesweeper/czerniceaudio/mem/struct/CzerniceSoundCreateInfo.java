package net.xoknox.czernicesweeper.czerniceaudio.mem.struct;

import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryStack;
import net.xoknox.czernicesweeper.czerniceaudio.mem.Struct;

import java.nio.ByteBuffer;

import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.*;

/**
 *  Layout:
 *  <pre><code>
 *  struct CzerniceSoundCreateInfo {
 *      usize data_ptr,
 *      usize data_len,
 *      SampleRate sample_rate,
 *      u32 channels,
 *      usize num_filters,
 *      usize eq_info_ptr,
 *      usize eq_info_len
 *  }</code></pre>
 */
public class CzerniceSoundCreateInfo extends Struct<CzerniceSoundCreateInfo> {

    private static final int SIZEOF;

    public static final int
        OF_DATA_PTR,
        OF_DATA_LEN,
        OF_SAMPLE_RATE,
        OF_CHANNELS,
        OF_NUM_FILTERS,
        OF_EQ_INFO_PTR,
        OF_EQ_INFO_LEN;

    static {
        Layout layout = __struct(
                __usize(),
                __usize(),
                __u32(),
                __u32(),
                __usize(),
                __usize(),
                __usize()
        );

        OF_DATA_PTR = layout.offsetof(0);
        OF_DATA_LEN = layout.offsetof(1);
        OF_SAMPLE_RATE = layout.offsetof(2);
        OF_CHANNELS = layout.offsetof(3);
        OF_NUM_FILTERS = layout.offsetof(4);
        OF_EQ_INFO_PTR = layout.offsetof(5);
        OF_EQ_INFO_LEN = layout.offsetof(6);

        SIZEOF = layout.getSize();
    }

    public CzerniceSoundCreateInfo(long address, ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected CzerniceSoundCreateInfo create(long address, ByteBuffer container) {
        return new CzerniceSoundCreateInfo(address, container);
    }

    public CzerniceSoundCreateInfo nDataPtr(long ptr) {
        putAddress(address() + OF_DATA_PTR, ptr);
        return this;
    }

    public CzerniceSoundCreateInfo nDataLen(long len) {
        putAddress(address() + OF_DATA_LEN, len);
        return this;
    }

    public CzerniceSoundCreateInfo nSampleRate(int sampleRate) {
        putInt(address() + OF_SAMPLE_RATE, sampleRate);
        return this;
    }

    public CzerniceSoundCreateInfo nChannels(int channels) {
        putInt(address() + OF_CHANNELS, channels);
        return this;
    }

    public CzerniceSoundCreateInfo nNumFilters(long num) {
        putAddress(address() + OF_NUM_FILTERS, num);
        return this;
    }

    public CzerniceSoundCreateInfo nEqInfoPtr(long ptr) {
        putAddress(address() + OF_EQ_INFO_PTR, ptr);
        return this;
    }

    public CzerniceSoundCreateInfo nEqInfoLen(long len) {
        putAddress(address() + OF_EQ_INFO_LEN, len);
        return this;
    }

    public CzerniceSoundCreateInfo data(ByteBuffer bytes) {
        if (bytes == null) throw new RuntimeException("Data may not be null");
        nDataPtr(memAddress(bytes));
        nDataLen(bytes.remaining());
        return this;
    }

    public CzerniceSoundCreateInfo sampleRate(int sampleRate) {
        return nSampleRate(sampleRate);
    }

    public CzerniceSoundCreateInfo channels(int channels) {
        return nChannels(channels);
    }

    public CzerniceSoundCreateInfo numFilters(long num) {
        return nNumFilters(num);
    }

    public CzerniceSoundCreateInfo eqInfo(CzerniceEqBandInfo.Buffer buffer) {
        if (buffer == null) throw new RuntimeException("EqBandInfo Buffer may not be null");
        nEqInfoPtr(buffer.address());
        nEqInfoLen(buffer.remaining());
        return this;
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public static CzerniceSoundCreateInfo malloc() {
        return new CzerniceSoundCreateInfo(memAlloc(SIZEOF), null);
    }

    public static CzerniceSoundCreateInfo calloc() {
        return new CzerniceSoundCreateInfo(memCalloc(SIZEOF), null);
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

}
