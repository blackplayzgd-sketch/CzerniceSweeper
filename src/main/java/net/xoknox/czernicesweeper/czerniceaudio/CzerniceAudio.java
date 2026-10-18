package net.xoknox.czernicesweeper.czerniceaudio;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class CzerniceAudio {

    public static final int EXPECTED_VERSION = 4194304;
    private static boolean ENABLED = false;

    static {
        load();
        if (!ENABLED) {
            System.err.println("CzerniceAudio failed to load and has been disabled.");
        }
    }

    private static void load() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();
        if (!arch.equals("amd64") && !arch.equals("x86_64"))  {
            return;
        }

        String filename;
        if (os.contains("linux")) {
            filename = "libczernice_audio.so";
        } else if (os.contains("win")) {
            filename = "czernice_audio.dll";
        } else {
            return;
        }

        try (InputStream stream = CzerniceAudio.class.getResourceAsStream("/" + filename)) {
            if (stream == null) return;

            String suffix = filename.substring(filename.lastIndexOf('.'));
            Path temp = Files.createTempFile("czernice_audio", suffix);

            Files.copy(stream, temp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            temp.toFile().deleteOnExit();

            System.load(temp.toAbsolutePath().toString());
            if (verify(false)) {
                ENABLED = true;
            }
        } catch (Throwable ignore) {}
    }

    public static int version() {
        if (ENABLED) {
            return nversion();
        } else {
            return 0;
        }
    }
    public static native int nversion();
    public static native boolean verify(boolean crash);

    public static native long ncreateEngine();
    public static native boolean nretireEngine(long engine);

    public static native long ncreateSound(long engine, byte[] soundBytes);
    public static native boolean nretireSound(long engine, long sound);

    public static native boolean nstartSound(long engine, long sound);
    public static native boolean nstopSound(long engine, long sound);
    public static native boolean nstopFade(long engine, long sound, long fadeMollis);
    public static native boolean nisPlaying(long engine, long sound);
    public static native void nsetLooping(long engine, long sound, boolean looping);

    public static native void nsetVolume(long engine, long sound, float volume);
    public static native void nsetSpeed(long engine, long sound, float speed);
    public static native void nsetBalance(long engine, long sound, float balance);
    public static native void nsetDoppler(long engine, long sound, float doppler);
    public static native void nsetRolloff(long engine, long sound, float rolloff);
    public static native void nsetWet(long engine, long sound, float wet);
    public static native void nsetDry(long engine, long sound, float dry);
    public static native void nsetDecayFrames(long engine, long sound, float decay);

    public static native void nsetSampleRateReduction(long engine, long sound, int reduction);
    public static native void nsetBitDepth(long engine, long sound, int depth);

    public static native void nsetEq(long engine, long sound, int band, float gain);
}
