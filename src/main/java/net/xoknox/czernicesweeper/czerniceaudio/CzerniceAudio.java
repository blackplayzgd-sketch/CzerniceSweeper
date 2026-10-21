package net.xoknox.czernicesweeper.czerniceaudio;

import net.xoknox.czernicesweeper.czerniceaudio.structs.CzerniceEqBandInfo;
import net.xoknox.czernicesweeper.czerniceaudio.structs.CzerniceEqBandModificationSubmitInfo;
import net.xoknox.czernicesweeper.czerniceaudio.structs.CzerniceEqGainModificationSubmitInfo;
import net.xoknox.czernicesweeper.czerniceaudio.structs.CzerniceSoundCreateInfo;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.*;

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
    public static native void nretireEngine(long engine);

    public static native long ncreateSound(long engine, long create_info);
    public static native void nretireSound(long sound);

    public static native boolean nstartSound(long sound);
    public static native boolean nstopSound(long sound);
    public static native boolean nstopFade(long sound, long fadeMillis);
    public static native boolean nisPlaying(long sound);
    public static native void nsetLooping(long sound, boolean looping);

    public static native void nsetVolume(long sound, float volume);
    public static native void nsetSpeed(long sound, float speed);
    public static native void nsetBalance(long sound, float balance);
    public static native void nsetDoppler(long sound, float doppler);
    public static native void nsetRolloff(long sound, float rolloff);
    public static native void nsetWet(long sound, float wet);
    public static native void nsetDry(long sound, float dry);
    public static native void nsetFeedback(long sound, float decay);

    public static native void nsetSampleRateReduction(long sound, int reduction);
    public static native void nsetBitDepth(long sound, int depth);

    public static native void nsetEqGain(long sound, long gain_submit_info);
    public static native void nsetEqBand(long sound, long band_submit_info);

    public static native void ngetEqBandInfo(long sound, long count_ptr, long band_info_ptr);

    public static native void nsetOnEndCallback(long sound, Runnable callback);

    public static long createEngine() {
        if (ENABLED) {
            return ncreateEngine();
        } else {
            return NULL;
        }
    }
    public static void retireEngine(long engine) {
        if (CHECKS) {
            if (engine == NULL) throw new IllegalArgumentException("Tried to retire NULL engine!");
        }

        nretireEngine(engine);
    }

    public static long createSound(long engine, CzerniceSoundCreateInfo create_info) {
        if (CHECKS) {
            if (engine == NULL) throw new IllegalArgumentException("Tried to retire NULL engine!");
            if (create_info == null) throw new IllegalArgumentException("Sound create info may not be NULL!");
            if (create_info.address() == NULL) throw new IllegalArgumentException("Sound create info may not be NULL!");
        }

        return ncreateSound(engine, create_info.address());
    }
    public static void retireSound(long sound) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Tried to retire NULL sound!");
        }

        nretireSound(sound);
    }

    public static boolean startSound(long sound) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        return nstartSound(sound);
    }
    public static boolean stopSound(long sound) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        return nstopSound(sound);
    }
    public static boolean stopFade(long sound, long fadeMillis) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        return nstopFade(sound, fadeMillis);
    }
    public static boolean isPlaying(long sound) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        return nisPlaying(sound);
    }
    public static void setLooping(long sound, boolean looping) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        nsetLooping(sound, looping);
    }

    public static void setVolume(long sound, float volume) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        nsetVolume(sound, volume);
    }
    public static void setSpeed(long sound, float speed) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        nsetSpeed(sound, speed);
    }
    public static void setBalance(long sound, float balance) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        nsetBalance(sound, balance);
    }
    public static void setDoppler(long sound, float doppler) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        nsetDoppler(sound, doppler);
    }
    public static void setRolloff(long sound, float rolloff) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        nsetRolloff(sound, rolloff);
    }
    public static void setWet(long sound, float wet) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        nsetWet(sound, wet);
    }
    public static void setDry(long sound, float dry) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        nsetDry(sound, dry);
    }
    public static void setFeedback(long sound, float decay) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        nsetFeedback(sound, decay);
    }

    public static void setSampleRateReduction(long sound, int reduction) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        nsetSampleRateReduction(sound, reduction);
    }
    public static void setBitDepth(long sound, int depth) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
        }

        nsetBitDepth(sound, depth);
    }

    public static void setEqGain(long sound, CzerniceEqGainModificationSubmitInfo gain_submit_info) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
            if (gain_submit_info == null) throw new IllegalArgumentException("Submit info may not be NULL!");
            if (gain_submit_info.address() == NULL) throw new IllegalArgumentException("Submit info may not be NULL!");
        }

        nsetEqGain(sound, gain_submit_info.address());
    }
    public static void setEqBand(long sound, CzerniceEqBandModificationSubmitInfo band_submit_info) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
            if (band_submit_info == null) throw new IllegalArgumentException("Submit info may not be NULL!");
            if (band_submit_info.address() == NULL) throw new IllegalArgumentException("Submit info may not be NULL!");
        }

        nsetEqBand(sound, band_submit_info.address());
    }

    public static void getEqBandInfo(long sound, IntBuffer count, @Nullable CzerniceEqBandInfo.Buffer buffer) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
            if (count == null) throw new IllegalArgumentException("Count buffer may not be NULL!");
        }

        ngetEqBandInfo(sound, memAddress(count), buffer == null ? 0L : buffer.address());
    }

    public static void setOnEndCallback(long sound, @Nullable Runnable callback) {
        if (CHECKS) {
            if (sound == NULL) throw new IllegalArgumentException("Invalid sound");
            if (callback == null) throw new IllegalArgumentException("Callback may not be NULL!");
        }

        nsetOnEndCallback(sound, callback);
    }

}
