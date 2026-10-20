package net.xoknox.czernicesweeper.czerniceaudio.mem;

import net.xoknox.czernicesweeper.czerniceaudio.CzerniceAudio;

import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.NULL;

public interface Pointer {

    int POINTER_SIZE = MemoryUtil.getPointerSize();

    int POINTER_SHIFT = POINTER_SIZE == 8 ? 3 : 2;

    boolean _32_BIT = POINTER_SIZE * 8 == 32;

    boolean _64_BIT = POINTER_SIZE * 8 == 64;

    long address();

    abstract class Default implements Pointer {

        protected long address;

        protected Default(long address) {
            if (address == NULL) {
                throw new NullPointerException();
            }
            this.address = address;
        }

        @Override
        public long address() {
            return address;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Pointer that)) {
                return false;
            }

            return address == that.address();
        }

        public int hashCode() {
            return Long.hashCode(address);
        }

        @Override
        public String toString() {
            return String.format("%s pointer [0x%X]", getClass().getSimpleName(), address);
        }

    }

}
