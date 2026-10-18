package net.xoknox.czernicesweeper.czerniceaudio;

import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryStack;
import net.xoknox.czernicesweeper.czerniceaudio.mem.struct.CzerniceEqBandInfo;
import net.xoknox.czernicesweeper.czerniceaudio.mem.struct.CzerniceSoundCreateInfo;

public class AudioTest {

    public static void main(String[] args) {
        long engine = CzerniceAudio.ncreateEngine();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            CzerniceEqBandInfo.Buffer buf = CzerniceEqBandInfo.Buffer.calloc(3, stack);

            buf.get(0)
                    .frequency(200)
                    .q(1.0)
                    .gainDb(0)
                    .sampleRate(44100);

            buf.get(1)
                    .frequency(1000)
                    .q(1.0)
                    .gainDb(0)
                    .sampleRate(44100);

            buf.get(2)
                    .frequency(4000)
                    .q(1.0)
                    .gainDb(0)
                    .sampleRate(44100);

            CzerniceSoundCreateInfo ci = CzerniceSoundCreateInfo.calloc(stack)
                    .data(null)
                    .sampleRate(44100)
                    .channels(2)
                    .numFilters(3)
                    .eqInfo(buf);

            long sound = CzerniceAudio.ncreateSound(engine, ci.address());
        }
    }

}
