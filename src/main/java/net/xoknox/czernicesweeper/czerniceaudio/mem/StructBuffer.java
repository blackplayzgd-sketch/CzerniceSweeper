package net.xoknox.czernicesweeper.czerniceaudio.mem;

import java.nio.ByteBuffer;

import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.*;

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
        return this.capacity();
    }

    public void free() {
        nfree(address());
    }

    public T get(int index) {
        T factory = getElementFactory();
        return factory.create(address() + (long) factory.sizeof() * index, null);
    }

    public SELF put(int index, T value) {
        int sizeof = getElementFactory().sizeof();
        mem_cpy(address() + (long) index * sizeof, value.address(), sizeof);
        return self();
    }

    public SELF get(int index, T value) {
        int sizeof = getElementFactory().sizeof();
        mem_cpy(value.address(), address() + (long) index * sizeof, sizeof);
        return self();
    }

    public abstract T getElementFactory();
    protected abstract SELF self();
    protected abstract SELF create(long address, int capacity);
}
