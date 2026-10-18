package net.xoknox.czernicesweeper.czerniceaudio.mem;

import lombok.Getter;

import java.nio.ByteBuffer;

import static net.xoknox.czernicesweeper.czerniceaudio.CzerniceAudio.*;

public abstract class Struct<Self extends Struct<Self>> extends Pointer.Default {

    private ByteBuffer container;

    public Struct(long address, ByteBuffer container) {
        super(address);
        this.container = container;
    }

    public void free() {
        nmemFree(address());
    }

    protected abstract Self create(long address, ByteBuffer container);

    public abstract int sizeof();

    public static class Member {
        @Getter
        protected int size, offset;

        Member(int size) {
            this.size = size;
        }
    }

    public static class Layout extends Member {
        public Member[] members;

        Layout(int size, Member[] members) {
            super(size);
            this.members = members;
        }

        public int offsetof(int i) { return members[i].offset; }
    }

    public static Member __usize() { return new Member(Pointer.POINTER_SIZE); }
    public static Member __u32() { return new Member(4); }
    public static Member __f32() { return new Member(4); }
    public static Member __f64() { return new Member(8); }

    public static Layout __struct(Member... members) {
        int size = 0;
        for (Member member : members) {
            member.offset = size;
            size += member.size;
        }

        return new Layout(size, members);
    }

}
