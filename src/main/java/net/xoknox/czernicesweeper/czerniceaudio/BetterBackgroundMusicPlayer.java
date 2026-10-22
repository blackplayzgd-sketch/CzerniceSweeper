package net.xoknox.czernicesweeper.czerniceaudio;

import net.xoknox.czernicesweeper.GameSettings;
import net.xoknox.czernicesweeper.IGameSettingable;
import net.xoknox.czernicesweeper.applicationinstallation.AppData;
import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil;
import net.xoknox.czernicesweeper.czerniceaudio.structs.CzerniceEqBandInfo;
import net.xoknox.czernicesweeper.czerniceaudio.structs.CzerniceEqGainModificationSubmitInfo;
import net.xoknox.czernicesweeper.czerniceaudio.structs.CzerniceSoundCreateInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class BetterBackgroundMusicPlayer implements IGameSettingable {
    private final long engine;
    private long sound;
    private GameSettings gameSettings;
    private double lowGain = 0d;
    private double midGain = 0d;
    private double highGain = 0d;
    private double rate = 1;
    private double volume = 1;
    private double balance = 0;
    private double bitDepth = 24d;
    private double wet = 0d;
    private double dry = 1d;
    private double feedback = 0d;
    private double rateDivisor = 1d;


    public BetterBackgroundMusicPlayer(GameSettings gameSettings) throws IOException {
        this.gameSettings = gameSettings;

        this.gameSettings.musicVolumeProperty().addListener(((observable, oldValue, newValue) -> {
            volume = newValue.doubleValue();
            CzerniceAudio.setVolume(sound, (float) volume);
        }));

        this.gameSettings.musicRateProperty().addListener(((observable, oldValue, newValue) ->{
            rate = newValue.doubleValue();
            CzerniceAudio.setSpeed(sound, (float) rate);
        }));
        this.gameSettings.musicBalanceProperty().addListener(((observable, oldValue, newValue) ->{
            balance = newValue.doubleValue();
            CzerniceAudio.setBalance(sound, (float) balance);
        }));

        this.gameSettings.lowFreqGainProperty().addListener((observable, oldValue, newValue) -> {
            double actualGain = newValue.doubleValue();
            lowGain = actualGain;
            setBandsGain(lowGain, midGain, highGain);
        });

        this.gameSettings.midFreqGainProperty().addListener((observable, oldValue, newValue) -> {
            double actualGain = newValue.doubleValue();
            midGain = actualGain;
            setBandsGain(lowGain, midGain, highGain);
        });

        this.gameSettings.highFreqGainProperty().addListener((observable, oldValue, newValue) -> {
            double actualGain = newValue.doubleValue();
            highGain = actualGain;
            setBandsGain(lowGain, midGain, highGain);
        });

        this.gameSettings.bitDepthProperty().addListener(((observable, oldValue, newValue) ->{
            balance = newValue.doubleValue();
            CzerniceAudio.setBitDepth(sound, (int) balance);
        }));

        this.gameSettings.wetProperty().addListener(((observable, oldValue, newValue) ->{
            balance = newValue.doubleValue();
            CzerniceAudio.setWet(sound, (float) balance);
        }));

        this.gameSettings.dryProperty().addListener(((observable, oldValue, newValue) ->{
            balance = newValue.doubleValue();
            CzerniceAudio.setDry(sound, (float) balance);
        }));

        this.gameSettings.feedbackProperty().addListener(((observable, oldValue, newValue) ->{
            balance = newValue.doubleValue();
            CzerniceAudio.setFeedback(sound, (float) balance);
        }));

        this.gameSettings.rateDivisorProperty().addListener(((observable, oldValue, newValue) ->{
            balance = newValue.doubleValue();
            CzerniceAudio.setSampleRateReduction(sound, (int) balance);
        }));

        this.engine = CzerniceAudio.createEngine();

        createRandomSound();
    }

    private void setBandsGain(double lowGain, double midGain, double highGain) {
        CzerniceEqGainModificationSubmitInfo eqGainInfo = CzerniceEqGainModificationSubmitInfo.malloc();
        eqGainInfo.band(0).gainDb((float) lowGain).sampleRate(44100);
        CzerniceAudio.setEqGain(sound, eqGainInfo);

        eqGainInfo.band(1).gainDb((float) midGain).sampleRate(44100);
        CzerniceAudio.setEqGain(sound, eqGainInfo);

        eqGainInfo.band(2).gainDb((float) highGain).sampleRate(44100);
        CzerniceAudio.setEqGain(sound, eqGainInfo);

        eqGainInfo.free();
    }

    private void createRandomSound() throws IOException {
        Path song = getSong();
        byte[] songBytes = Files.readAllBytes(song);


        ByteBuffer buffer = MemoryUtil.mallocBytes(songBytes.length);
        buffer.put(songBytes);
        buffer.flip();

        CzerniceEqBandInfo.Buffer buf = CzerniceEqBandInfo.malloc(3);
        buf.get(0).frequency(100).gainDb((float) lowGain).q(0.5f).sampleRate(44100);
        buf.get(1).frequency(1000).gainDb((float) midGain).q(0.5f).sampleRate(44100);
        buf.get(2).frequency(7000).gainDb((float) highGain).q(0.5f).sampleRate(44100);

        CzerniceSoundCreateInfo createInfo = CzerniceSoundCreateInfo.calloc();
        createInfo.pData(buffer);
        createInfo.sampleRate(44100);
        createInfo.channels(2);
        createInfo.pEqInfo(buf);
        createInfo.reverbDelay(0);
        createInfo.wet(0);
        createInfo.dry(1);
        createInfo.feedback(0);
        createInfo.rateDivisor(1);
        createInfo.bitDepth(24);

        this.sound = CzerniceAudio.createSound(engine,createInfo);
        setupSound(sound);

        System.out.println(this.sound);
        createInfo.free();
        buf.free();
        MemoryUtil.free(buffer);


        CzerniceAudio.setOnEndCallback(sound, this::onEndOfSound);
        System.out.println("smth else");
    }

    private void setupSound(long sound) {
        CzerniceAudio.setSpeed(sound, (float) rate);
        CzerniceAudio.setVolume(sound, (float) volume);
        CzerniceAudio.setBalance(sound, (float) balance);
        CzerniceAudio.setBitDepth(sound, (int) bitDepth);
        CzerniceAudio.setDry(sound, (float) dry);
        CzerniceAudio.setWet(sound, (float) wet);
        CzerniceAudio.setFeedback(sound, (float) feedback);
        CzerniceAudio.setSampleRateReduction(sound, (int) rateDivisor);
        setBandsGain(lowGain, midGain, highGain);

    }

    private void onEndOfSound() {
        System.out.println("Called back!");
        long oldSound = BetterBackgroundMusicPlayer.this.sound;

        try {
            createRandomSound();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CzerniceAudio.stopSound(oldSound);

        System.out.println("Finished calling back!");
        CzerniceAudio.retireSound(oldSound);
        System.out.println("ReALLY finjshed coallids");
    }



    public void start() {
        System.out.println(CzerniceAudio.startSound(sound) + " skibidi");
    }
    public void skip() {
        CzerniceAudio.stopSound(sound);
        onEndOfSound();
    }

    public void toggle() {
        System.out.println("togold");
        System.out.println(CzerniceAudio.stopSound(sound));

    }

    private Path getSong() {
        Path musicDir = AppData.getDirectory().resolve("music");

        try (var paths = Files.list(musicDir)) {
            return paths.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".mp3")).toList().get(2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }
}
