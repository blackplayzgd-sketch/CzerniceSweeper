package net.xoknox.czernicesweeper.czerniceaudio.mem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static net.xoknox.czernicesweeper.czerniceaudio.CzerniceAudio.*;
import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.*;

public final class MemoryStack implements AutoCloseable {
    private static final int DEFAULT_SIZE = 1024 * 1024;

    private final long baseAddress;
    private final int capacity;
    private int offset;
    private boolean closed;

    private MemoryStack(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Invalid capacity");
        }

        this.capacity = capacity;
        this.baseAddress = nmalloc(capacity);

        if (baseAddress == 0) {
            throw new OutOfMemoryError("Native allocation failed");
        }
    }

    public static MemoryStack stackPush() {
        return new MemoryStack(DEFAULT_SIZE);
    }

    public long malloc(int size) {
        return malloc(size, 8);
    }

    public long malloc(int alignment, int size) {
        ensureOpen();

        if (size < 0) {
            throw new IllegalArgumentException("Negative size");
        }

        if (alignment <= 0 || (alignment & (alignment - 1)) != 0) {
            throw new IllegalArgumentException("Alignment must be a power of two");
        }

        int alignedOffset = Math.addExact(
                offset, alignment - 1
        ) & -alignment;

        int end = Math.addExact(alignedOffset, size);

        if (end > capacity) {
            throw new OutOfMemoryError("MemoryStack capacity exceeded");
        }

        long address = baseAddress + alignedOffset;
        offset = end;

        return address;
    }

    public long calloc(int size) {
        long addr = malloc(size);

        for (int i = 0; i < size; i++) {
            MemoryUtil.putByte(addr + i, (byte) 0);
        }

        return addr;
    }

    public long calloc(int alignment, int size) {
        return NULL;
    }

    public ByteBuffer memAlloc(int size) {
        return memAlloc(size, 8);
    }

    public ByteBuffer memAlloc(int size, int alignment) {
        return nmem_byte_buffer(malloc(size, alignment), size)
                .order(ByteOrder.nativeOrder());
    }

    public ByteBuffer memCalloc(int size) {
        return nmem_byte_buffer(calloc(size), size)
                .order(ByteOrder.nativeOrder());
    }

    public IntBuffer mallocInt(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Negative count");
        }

        return memAlloc(
                Math.multiplyExact(count, Integer.BYTES),
                Integer.BYTES
        ).asIntBuffer();
    }

    public long address() {
        ensureOpen();
        return baseAddress;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("MemoryStack is closed");
        }
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            nfree(baseAddress);
        }
    }
}
