package net.xoknox.czernicesweeper;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Setter;

import static net.xoknox.czernicesweeper.Utils.MINE_COUNT;
import static net.xoknox.czernicesweeper.Utils.randomColor;

public class Game extends Group implements IGameSettingable {

    private final Engine engine;
    private final Sweeper sweeper;
    private final Text title = new Text(100, 70, "Czernicesweeper v0.0.2-Σ");
    private final Button settingsButton = new Button();


    private GameSettings gameSettings;

    public Game(GameSettings settings) {
        this.gameSettings = settings;

        this.engine = new Engine(1000, 300, 40, 120, 10, 50, 60);
        engine.timer.start();

        this.sweeper = new Sweeper(50, 100, 30, 20, MINE_COUNT);
        this.gameSettings.unitProperty().addListener((obs,oldUnit, newUnit) -> {
            sweeper.timer.setUnit(newUnit);
        });
        this.gameSettings.widthProperty().addListener((obs, oldVal, newVal) -> sweeper.setWidth((Integer) newVal));
        this.gameSettings.heightProperty().addListener((obs, oldVal, newVal) -> sweeper.setHeight((Integer) newVal));
        this.gameSettings.mineCountProperty().addListener((obs, oldVal, newVal) -> sweeper.setMineCount((Integer) newVal));
        setupUI();




        /*
        Button winButton = new Button("Win");
        winButton.setOnMouseClicked(e -> sweeper.win());


        Button timerButton = new Button("stop");
        timerButton.setOnMouseClicked(e -> sweeper.die());
        timerButton.setLayoutX(50);

        Button timerButton2 = new Button("start");
        timerButton2.setOnMouseClicked(e -> sweeper.timer.start());
        timerButton2.setLayoutX(100);

        this.getChildren().addAll(winButton, timerButton, timerButton2);

         */

        this.getChildren().addAll(sweeper, settingsButton, engine, title);



    }

    private void setupUI() {
        title.setFont(Font.font("impact", 50));
        title.setFill(Utils.randomColor());

        settingsButton.setText("Settignes");
        settingsButton.setLayoutX(1000);
        settingsButton.setLayoutY(200);
        settingsButton.setPrefSize(170, 30);
        settingsButton.setFont(Font.font("impact", 20));
        settingsButton.setBackground(Background.fill(Utils.randomColor()));
        settingsButton.setOnMouseClicked(e -> gameSettings.getStage().setScene(gameSettings.getSettingsScene()));

        setupMusicUI();
    }

    private void setupMusicUI() {
        Text currSongText = new Text("Now deploying: ");
        currSongText.setFont(new Font("impact", 20));
        currSongText.setFill(Utils.randomColor());
        currSongText.setX(50);
        currSongText.setY(730);
        gameSettings.currSongProperty().addListener((observable, oldValue, newValue) -> {
                    currSongText.setText("Now playing:\n" + newValue);
                    System.out.println("CHANGED SONGG");
        });

        Button pauseButton = new Button("Pouz");
        pauseButton.setFont(new Font("impact", 15));
        pauseButton.setBackground(Background.fill(randomColor()));
        pauseButton.setLayoutX(50);
        pauseButton.setLayoutY(770);
        pauseButton.setOnMouseClicked(e -> {
            gameSettings.getBackgroundMusicPlayer().pause();
        });

        Button skipButton = new Button("Skip");
        skipButton.setFont(new Font("impact", 15));
        skipButton.setBackground(Background.fill(randomColor()));
        skipButton.setLayoutX(100);
        skipButton.setLayoutY(770);
        skipButton.setOnMouseClicked(e -> {
            gameSettings.getBackgroundMusicPlayer().skip();
        });


        getChildren().addAll(currSongText, pauseButton, skipButton);

    }

    @Override
    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }
}
