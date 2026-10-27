package net.xoknox.czernicesweeper;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import net.xoknox.czernicesweeper.applicationinstallation.AppData;
import net.xoknox.czernicesweeper.util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class BackgroundMusicPlayer implements IGameSettingable {
    @Getter
    private static final ArrayList<String[]> songs = new ArrayList<>();
    private double lowGain = 0d;
    private double midGain = 0d;
    private double highGain = 0d;
    private double rate = 1;
    private double volume = 1;
    private double balance = 0;

    private MediaPlayer player;

    private GameSettings gameSettings;

    public BackgroundMusicPlayer(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
        getMusic();

        this.gameSettings.musicVolumeProperty().addListener(((observable, oldValue, newValue) -> {
            volume = newValue.doubleValue();
            player.setVolume(volume);
        }));

        this.gameSettings.musicRateProperty().addListener(((observable, oldValue, newValue) ->{
            rate = newValue.doubleValue();
            player.setRate(rate);
        }));
        this.gameSettings.musicBalanceProperty().addListener(((observable, oldValue, newValue) ->{
            balance = newValue.doubleValue();
            player.setBalance(balance);
        }));

        this.gameSettings.lowFreqGainProperty().addListener((observable, oldValue, newValue) -> {
            double actualGain = newValue.doubleValue() < 0 ? newValue.doubleValue() * 2 : newValue.doubleValue();
            lowGain = actualGain;
            setBandsGain(player, lowGain, midGain, highGain);
        });

        this.gameSettings.midFreqGainProperty().addListener((observable, oldValue, newValue) -> {
            double actualGain = newValue.doubleValue() < 0 ? newValue.doubleValue() * 2 : newValue.doubleValue();
            midGain = actualGain;
            setBandsGain(player, lowGain, midGain, highGain);


        });
        this.gameSettings.highFreqGainProperty().addListener((observable, oldValue, newValue) -> {
            double actualGain = newValue.doubleValue() < 0 ? newValue.doubleValue() * 2 : newValue.doubleValue();
            highGain = actualGain;
            setBandsGain(player, lowGain, midGain, highGain);

        });
    }

    private void setBandsGain(MediaPlayer player, double lowGain, double midGain, double highGain) {
        player.getAudioEqualizer().getBands().get(0).setGain(lowGain);
        player.getAudioEqualizer().getBands().get(1).setGain(lowGain);
        player.getAudioEqualizer().getBands().get(2).setGain(lowGain);
        player.getAudioEqualizer().getBands().get(3).setGain(lowGain);

        player.getAudioEqualizer().getBands().get(4).setGain(midGain);
        player.getAudioEqualizer().getBands().get(5).setGain(midGain);
        player.getAudioEqualizer().getBands().get(6).setGain(midGain);

        player.getAudioEqualizer().getBands().get(7).setGain(highGain);
        player.getAudioEqualizer().getBands().get(8).setGain(highGain);
        player.getAudioEqualizer().getBands().get(9).setGain(highGain);
    }

    public void start() {
//        String[] song = getRandomSong();
//        Media media = new Media(song[0]);
//
//        player = new MediaPlayer(media);
//        setupInitialPlayer(player);
//        this.gameSettings.currSongProperty().setValue(song[1]);
//        System.out.println(song[1]);
//        updateMusicPlayer(player);
    }

    private void setupInitialPlayer(MediaPlayer player) {
        player.setAutoPlay(true);
        player.getAudioEqualizer().setEnabled(true);
        player.setVolume(volume);
        player.setRate(rate);
        player.setBalance(balance);
        setBandsGain(player, lowGain, midGain, highGain);
    }

    public void skip() {
        player.dispose();
        start();
    }

    public void pause() {
        if (player.getStatus() == MediaPlayer.Status.PAUSED) {
            player.play();
        } else {
            player.pause();
        }
        System.out.println("paused");
    }

    private void updateMusicPlayer(MediaPlayer player) {

        player.setOnEndOfMedia(() -> {
            System.out.println("EXECUTEDDDDDD");

            String[] newSong = getRandomSong();
            Media newSongMedia = new Media(newSong[0]);

            MediaPlayer newPlayer = new MediaPlayer(newSongMedia);
            newPlayer.setAutoPlay(true);
            newPlayer.getAudioEqualizer().setEnabled(true);
            newPlayer.setVolume(volume);
            newPlayer.setRate(rate);
            newPlayer.setBalance(balance);
            setBandsGain(newPlayer, lowGain, midGain, highGain);

            this.player.dispose();
            this.player = newPlayer;
            gameSettings.currSongProperty().setValue(newSong[1]);
            updateMusicPlayer(this.player);
        });

    }

    private String[] getRandomSong() {

        return songs.get(Utils.randIntRange(0, songs.size()));
    }

    private void getMusic() {
        Path musicDir = AppData.getDirectory().resolve("music");

        try (var paths = Files.list(musicDir)) {
            paths.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".mp3")).forEach(p -> {
                String[] song = new String[]{p.toUri().toString(), p.getFileName().toString()};
                songs.add(song);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }
}
