package net.xoknox.czernicesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.xoknox.czernicesweeper.applicationinstallation.AssetInstaller;

import static net.xoknox.czernicesweeper.Utils.*;

public class CzerniceSweeper extends Application {
    private BackgroundMusicPlayer backgroundMusicPlayer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Textures.init();
        AssetInstaller.installDefaultMusic();
        Utils.init();
        GameSettings gameSettings = new GameSettings();

        Image icon = new Image(getPath("/textures/mine.png"));
        System.out.println(Units.YOCTOSECOND.t());

        backgroundMusicPlayer = new BackgroundMusicPlayer(gameSettings);

        gameSettings.setBackgroundMusicPlayer(backgroundMusicPlayer);

        Settings settings = new Settings(gameSettings);
        Scene settingsScene = new Scene(settings, WINDOW_SIZE[0], WINDOW_SIZE[1], randomColor());

        Game game = new Game(gameSettings);
        Scene gameScene = new Scene(game, WINDOW_SIZE[0], WINDOW_SIZE[1], randomColor());



        gameSettings.setStage(primaryStage);
        gameSettings.setGame(game);
        gameSettings.setGameScene(gameScene);
        gameSettings.setSettings(settings);
        gameSettings.setSettingsScene(settingsScene);


        game.setGameSettings(gameSettings);
        settings.setGameSettings(gameSettings);
        backgroundMusicPlayer.setGameSettings(gameSettings);



        primaryStage.setScene(gameScene);
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Czernicesweeper v0.0.1-θ");

        backgroundMusicPlayer.start();
        primaryStage.show();
    }



    private void doNothing() {

    }
}
