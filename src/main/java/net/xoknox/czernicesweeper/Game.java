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

public class Game extends Group {

    private final Engine engine;
    private final Sweeper sweeper;
    private final Text title = new Text(100, 70, "Czernicesweeper v0.0.1-θ");
    private final Button settingsButton = new Button();

    @Setter
    private GameSettings gameSettings;
    private Stage stage;
    private Scene settingsScene;

    public Game(GameSettings settings) {
        this.gameSettings = settings;
        updateSettings();

        this.engine = new Engine(1000, 300, 40, 120, 10, 50, 60);
        engine.timer.start();

        this.sweeper = new Sweeper(50, 100, 30, 20, MINE_COUNT);
        this.gameSettings.unitProperty().addListener((obs,oldUnit, newUnit) -> {
            sweeper.timer.setUnit(newUnit);
        });

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
        settingsButton.setOnMouseClicked(e -> stage.setScene(settingsScene));

    }

    private void updateSettings() {
        this.stage = this.gameSettings.getStage();
        this.settingsScene = this.gameSettings.getSettingsScene();
    }

    public void setGameSettings(GameSettings settings) {
        this.gameSettings = settings;
        updateSettings();
    }

}
