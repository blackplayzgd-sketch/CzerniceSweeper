package net.xoknox.czernicesweeper.czerniceaudio.mem;

import lombok.Getter;
import net.xoknox.czernicesweeper.Platform;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.*;

public abstract class Struct<Self extends Struct<Self>> extends Pointer.Default {

    protected static final int DEFAULT_PACK_ALIGNMENT = Platform.get() == Platform.WINDOWS ? 8 : 0x4000_0000;
    protected static final int DEFAULT_ALIGN_AS       = 0;

    @Nullable
    protected ByteBuffer container;

    public Struct(long address, @Nullable ByteBuffer container) {
        super(address);
        this.container = container;
    }

    protected abstract Self create(long address, @Nullable  ByteBuffer container);

    public abstract int sizeof();

    public void clear() { mem_set(address(), 0, sizeof()); }

    public void free() {
        nfree(address());
    }

    protected static ByteBuffer __create(int elements, int elementSize) {
        return ByteBuffer.allocateDirect(elements * elementSize).order(ByteOrder.nativeOrder());
    }

    protected static <T extends Struct<T>> @Nullable ByteBuffer __getContainer(T struct) {
        return struct.container;
    }

    public static ByteBuffer __checkContainer(ByteBuffer container, int sizeof) {
        if (CHECKS) {
            if (container.remaining() < sizeof) throw new IllegalArgumentException("Container is too small for struct!");
        }
        return container;
    }

    @Getter
    public static class Member {
        protected final int size, alignment;
        protected final boolean forcedAlignment;

        protected int offset;

        Member(int size, int alignment, boolean forcedAlignment) {
            this.size = size;
            this.alignment = alignment;
            this.forcedAlignment = forcedAlignment;
        }

        public int getAlignment(int packAlignment) {
            return forcedAlignment ? alignment : Math.min(alignment, packAlignment);
        }
    }

    public static class Layout extends Member {
        public Member[] members;

        Layout(int size, int alignment, boolean forcedAlignment, Member[] members) {
            super(size, alignment, forcedAlignment);
            this.members = members;
        }

        public int offsetof(int i) { return members[i].offset; }
    }

    public static Member __padding(int num) { return __padding(num, 1); }
    public static Member __padding(int num, int size) { return __member(num * size, size); }

    public static Member __member(int size) { return __member(size, size); }
    public static Member __member(int size, int alignment) {
        return __member(size, alignment, false);
    }

    public static Member __member(int size, int alignment, boolean forceAlignment) {
        return new Member(size, alignment, forceAlignment);
    }

    public static Layout __struct(Member... members) { return __struct(DEFAULT_PACK_ALIGNMENT, DEFAULT_ALIGN_AS, members); }
    public static Layout __struct(int packAlignment, int alignas, Member... members) {
        List<Member> struct = new ArrayList<>(members.length);

        int size      = 0;
        int alignment = alignas;
        for (Member m : members) {
            int memberAlignment = m.getAlignment(packAlignment);

            m.offset = align(size, memberAlignment);

            size = m.offset + m.size;
            alignment = max(alignment, memberAlignment);

            struct.add(m);
            if (m instanceof Layout) {
                addNestedMembers(m, struct, m.offset);
            }
        }

        // tail padding
        size = align(size, alignment);

        return new Layout(size, alignment, alignas != 0, struct.toArray(new Member[0]));
    }

    private static void addNestedMembers(Member nested, List<Member> members, int offset) {
        Layout layout = (Layout)nested;

        for (Member m : layout.members) {
            m.offset += offset;
            members.add(m);
        }
    }

    private static int align(int offset, int alignment) {
        return ((offset - 1) | (alignment - 1)) + 1;
    }

}
