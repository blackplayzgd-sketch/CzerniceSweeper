package net.xoknox.czernicesweeper.czerniceaudio;

import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil;
import net.xoknox.czernicesweeper.czerniceaudio.structs.CzerniceEqBandInfo;
import net.xoknox.czernicesweeper.czerniceaudio.structs.CzerniceSoundCreateInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class AudioTest {

    public static void main(String[] args) throws IOException {
        long engine = CzerniceAudio.ncreateEngine();
//        MemoryUtil.malloc(128);

//        try (MemoryStack stack = MemoryStack.stackPush()) {
        ByteBuffer data = null;
        int c = 0;
        try (InputStream is = AudioTest.class.getResourceAsStream("/music/test.mp3")) {
            byte[] dat = is.readAllBytes();
            c = dat.length;
            data = MemoryUtil.mallocBytes(dat.length);

            for (byte b : dat) {
                data.put(b);
            }

            data.flip();
        }

        CzerniceEqBandInfo.Buffer buf = CzerniceEqBandInfo.calloc(3);

        buf.get(0)
                .frequency(200)
                .q(1.0)
                .gainDb(0)
                .sampleRate(44100);

        buf.get(1)
                .frequency(1000)
                .q(1.0)
                .gainDb(-0)
                .sampleRate(44100);

        buf.get(2)
                .frequency(4000)
                .q(1.0)
                .gainDb(0)
                .sampleRate(44100);

        CzerniceSoundCreateInfo ci = CzerniceSoundCreateInfo.calloc()
                .pData(data)
                .sampleRate(44100)
                .channels(2)
                .numFilters(3)
                .pEqInfo(buf);

        CzerniceSoundCreateInfo.ndataCount(ci.address(), c);
        CzerniceSoundCreateInfo.neqInfoCount(ci.address(), 3);

        long sound = CzerniceAudio.ncreateSound(engine, ci.address());

        CzerniceAudio.nstartSound(sound);

        while (true) {}


        //}
    }

}
