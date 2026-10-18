package net.xoknox.czernicesweeper;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;
import net.xoknox.czernicesweeper.ui.CzerniceControl;

import static net.xoknox.czernicesweeper.Utils.*;
import static net.xoknox.czernicesweeper.Utils.fracOfRange;
import static net.xoknox.czernicesweeper.ui.CzerniceControl.DEF_FONT;

public class Game extends Pane implements IGameSettingable {

    private final Engine engine;
    private final Sweeper sweeper;
    private final Text title = new Text(100, 70, "CzerniceSweeper v0.0.3-ζ");
    private final CzerniceControl<Button> settingsButton = new CzerniceControl<>(new Button());
    private final ImageView titleImage = new ImageView(Textures.LOGO);
    @Getter
    private final ImageView bg = new ImageView();


    private GameSettings gameSettings;

    public Game(GameSettings settings) {
        this.gameSettings = settings;

        this.setBackground(new Background(new BackgroundImage(
                Textures.MAIN_BG,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT
        )));
        bg.setImage(Textures.MAIN_BG);
        bg.setFitWidth(WINDOW_SIZE[0]);
        bg.setFitHeight(WINDOW_SIZE[1]);
        getChildren().add(bg);

        DtAnimationTimer timer = new DtAnimationTimer() {
            double t = 0;
            @Override
            public void step(double dt) {
                t += 2 * dt;

                double sx = (Math.cos(t) + 1) / 2;
                double sy = (Math.sin(t) + 1) / 2;

                bg.setScaleX(fracOfRange(1, 1.4, sx));
                bg.setScaleY(fracOfRange(1.4, 1,sx));

                t %= 2 * Math.PI;
            }
        };
        timer.start();

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


        CzerniceControl<CheckBox> hardModeCheckBox = new CzerniceControl<>(new CheckBox("Hard moed"));
        hardModeCheckBox.setLayoutX(1100);
        hardModeCheckBox.setLayoutY(260);
        hardModeCheckBox.main.setFont(Font.font(DEF_FONT, 20));
        hardModeCheckBox.setPrefSize(150, 60);
        hardModeCheckBox.main.selectedProperty().addListener((observable, oldValue, newValue) -> {
            sweeper.setHardMode(newValue);
        });





        //button.setLayoutX(100);
        //button.setLayoutY(20);
        //button.setPrefSize(256, 32);
        //button.setScaleShape(true);
        //button.setScaleX(0.5);
        //button.setScaleY(0.5);

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

        this.getChildren().addAll(sweeper, settingsButton, engine, titleImage, hardModeCheckBox);



    }

    private void setupUI() {
        title.setFont(Font.font(20));
        title.setFill(Utils.randomColor());
        titleImage.setX(100);
        titleImage.setY(5);


        settingsButton.main.setText("Settignes");
        settingsButton.setLayoutX(1000);
        settingsButton.setLayoutY(200);
        settingsButton.setPrefSize(170, 30);
        settingsButton.main.setFont(Font.font(DEF_FONT, 15));
        settingsButton.main.setOnMouseClicked(e -> gameSettings.getStage().setScene(gameSettings.getSettingsScene()));

        setupMusicUI();
    }

    private void setupMusicUI() {
        Text currSongText = new Text("Now deploying: ");
        currSongText.setFont(new Font(20));
        currSongText.setFill(Utils.randomColor());
        currSongText.setX(50);
        currSongText.setY(730);
        gameSettings.currSongProperty().addListener((observable, oldValue, newValue) -> {
                    currSongText.setText("Now playing:\n" + newValue);
                    System.out.println("CHANGED SONGG");
        });

        CzerniceControl<Button> pauseButton = new CzerniceControl<>(new Button("Pouz"));
        pauseButton.main.setFont(Font.font(DEF_FONT, 15));
        pauseButton.setPrefSize(50, 20);
        pauseButton.setLayoutX(50);
        pauseButton.setLayoutY(770);
        pauseButton.main.setOnMouseClicked(e -> {
            gameSettings.getBackgroundMusicPlayer().pause();
        });

        CzerniceControl<Button> skipButton = new CzerniceControl<>(new Button( "Skeep"));
        skipButton.main.setFont(Font.font(DEF_FONT, 15));
        skipButton.setPrefSize(70, 20);
        skipButton.setLayoutX(100);
        skipButton.setLayoutY(770);
        skipButton.main.setOnMouseClicked(e -> {
            gameSettings.getBackgroundMusicPlayer().skip();
        });


        getChildren().addAll(currSongText, pauseButton, skipButton);

    }

    @Override
    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }
}
