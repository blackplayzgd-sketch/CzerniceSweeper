package net.xoknox.czernicesweeper.czerniceaudio.mem;

import java.nio.ByteBuffer;

import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.memAddress;

public abstract class StructBuffer<T extends Struct<T>, SELF extends StructBuffer<T, SELF>> extends Pointer.Default {

    protected final int capacity;

    public StructBuffer(ByteBuffer container, int capacity) {
        this(memAddress(container), capacity);
    }

    public StructBuffer(long address, int capacity) {
        super(address);
        this.capacity = capacity;
    }

    public int capacity() { return this.capacity; }

    public int remaining() {
        return this.capacity() * getElementFactory().sizeof();
    }

    public T get(int index) {
        T factory = getElementFactory();
        return factory.create(address() + (long) factory.sizeof() * index, null);
    }

    public abstract T getElementFactory();
}
