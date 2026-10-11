package net.xoknox.czernicesweeper;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import net.xoknox.czernicesweeper.applicationinstallation.AppData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BackgroundMusicPlayer implements IGameSettingable {
    private final ArrayList<String[]> songs = new ArrayList<>();

    private MediaPlayer player;

    private GameSettings gameSettings;

    public BackgroundMusicPlayer(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
        getMusic();
        this.gameSettings.musicVolumeProperty().addListener(((observable, oldValue, newValue) ->
                player.setVolume((Double) newValue)));
    }

    public void start() {
        String[] song = getRandomSong();
        Media media = new Media(song[0]);

        player = new MediaPlayer(media);
        this.gameSettings.currSongProperty().setValue(song[1]);
        System.out.println(song[1]);
        updateMusicPlayer(player);
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
        player.setAutoPlay(true);
        player.setOnEndOfMedia(() -> {
            String[] newSong = getRandomSong();
            Media newSongMedia = new Media(newSong[0]);
            MediaPlayer newPlayer = new MediaPlayer(newSongMedia);
            newPlayer.setAutoPlay(true);
            this.player.dispose();
            this.player = newPlayer;
            gameSettings.currSongProperty().setValue(newSong[1]);
            updateMusicPlayer(player);
        });

    }

    private String[] getRandomSong() {

        return songs.get(Utils.randIntRange(0, songs.size() - 1));
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
