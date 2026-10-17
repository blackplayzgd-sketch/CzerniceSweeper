package net.xoknox.czernicesweeper;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import net.xoknox.czernicesweeper.ui.CzerniceControl;

import java.util.ArrayList;
import java.util.List;

import static net.xoknox.czernicesweeper.Utils.*;

public class Settings extends Pane implements IGameSettingable {
    private GameSettings gameSettings;

    private final CzerniceControl<TextField> widthInput;
    private final CzerniceControl<TextField> heightInput;
    private final CzerniceControl<TextField> mineCountInput;
    private final Menu timeMenu = new Menu("Time unit");


    public Settings(GameSettings settings) {
        this.setBackground(new Background(new BackgroundImage(
                Textures.SETTINGS_BG_2,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT
        )));
        ImageView bg = new ImageView(Textures.SETTINGS_BG);
        bg.setFitWidth(WINDOW_SIZE[0]);
        bg.setFitHeight(WINDOW_SIZE[1]);



        DtAnimationTimer timer = new DtAnimationTimer() {
            double t = 0;
            @Override
            public void step(double dt) {
                t += 2 * dt;

                double sx = (Math.cos(t) + 1) / 2;
                double sy = (Math.sin(t) + 1) / 2;

                bg.setScaleX(fracOfRange(1, 1.4, sx));
                bg.setScaleY(fracOfRange(1.4, 1, sx));

                t %= 2 * Math.PI;
            }
        };
        timer.start();

        this.gameSettings = settings;

        addTimeMenuItems();
        CzerniceControl<MenuBar> timeMenuBar = new CzerniceControl<>(new MenuBar(timeMenu));
        timeMenuBar.setLayoutX(50);
        timeMenuBar.setLayoutY(100);
        timeMenuBar.setPrefSize(100, 40);

        CzerniceControl<Button> backButton = new CzerniceControl<>(new Button("Bacc"));
        backButton.setLayoutX(Utils.WINDOW_CENTER[0]);
        backButton.setLayoutY(700);
        backButton.setPrefSize(70, 20);
        backButton.main.setOnMouseClicked(e -> gameSettings.getStage().setScene(gameSettings.getGameScene()));

        widthInput = new CzerniceControl<>(new TextField());
        widthInput.main.setPromptText("Board width");
        widthInput.main.setAlignment(Pos.BASELINE_CENTER);
        widthInput.setLayoutX(200);
        widthInput.setLayoutY(150);
        widthInput.setPrefSize(100, 20);
        
        heightInput = new CzerniceControl<>(new TextField());
        heightInput.main.setPromptText("Board height");
        heightInput.main.setAlignment(Pos.BASELINE_CENTER);
        heightInput.setLayoutX(200);
        heightInput.setLayoutY(200);
        heightInput.setPrefSize(100, 20);

        mineCountInput = new CzerniceControl<>(new TextField());;
        mineCountInput.main.setPromptText("Board mine count");
        mineCountInput.main.setAlignment(Pos.BASELINE_CENTER);
        mineCountInput.setLayoutX(200);
        mineCountInput.setLayoutY(250);
        mineCountInput.setPrefSize(100, 20);

        CzerniceControl<Button> updateButton = new CzerniceControl<>(new Button("Update"));
        updateButton.setLayoutX(200);
        updateButton.setLayoutY(100);
        updateButton.main.setAlignment(Pos.BASELINE_CENTER);
        updateButton.setPrefSize(100, 30);
        updateButton.main.setOnMouseClicked(e -> {
            updateButtonClick();
        });

        CzerniceControl<Button> colorButton = new CzerniceControl<>(new Button("Randomeiz colers!"));
        colorButton.setLayoutX(300);
        colorButton.setLayoutY(100);
        colorButton.setPrefSize(120, 30);

        colorButton.main.setOnMouseClicked(e -> {
            gameSettings.getGameScene().setFill(randomColor());
            gameSettings.getSettingsScene().setFill(randomColor());
            Utils.randomizeColorsInParent(gameSettings.getGameScene().getRoot());
            Utils.randomizeColorsInParent(gameSettings.getSettingsScene().getRoot());
        });

        CzerniceControl<Label> musicLabel = new CzerniceControl<>(new Label("Musik"));
        musicLabel.setLayoutX(450);
        musicLabel.setLayoutY(100);
        musicLabel.main.setAlignment(Pos.BASELINE_CENTER);
        musicLabel.setPrefSize(100, 40);

        ArrayList<Double> sliderYs = new ArrayList<>(List.of(150d, 170d, 190d, 210d, 230d, 250d));

        sliderYs = Utils.shuffle(sliderYs);

        CzerniceControl<Slider> volumeSlider = new CzerniceControl<>(new Slider(0, 1, 0.5));
        volumeSlider.setLayoutX(450);
        volumeSlider.setLayoutY(sliderYs.get(0));
        volumeSlider.setPrefSize(150, 20);
        volumeSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setMusicVolume((Double) newVal);
        });

        CzerniceControl<Slider> rateSlider = new CzerniceControl<>(new Slider(0, 2, 1));
        rateSlider.setLayoutX(450);
        rateSlider.setLayoutY(sliderYs.get(1));
        rateSlider.setPrefSize(150, 20);
        rateSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setMusicRate((Double) newVal);
        });

        CzerniceControl<Slider> balanceSlider = new CzerniceControl<>(new Slider(-1, 1, 0));
        balanceSlider.setLayoutX(450);
        balanceSlider.setLayoutY(sliderYs.get(2));
        balanceSlider.setPrefSize(150, 20);
        balanceSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setMusicBalance((Double) newVal);
        });

        CzerniceControl<Slider> lowFreqSlider = new CzerniceControl<>(new Slider(-12, 12, 0));
        lowFreqSlider.setLayoutX(450);
        lowFreqSlider.setLayoutY(sliderYs.get(3));
        lowFreqSlider.setPrefSize(150, 20);
        lowFreqSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setLowFreqGain((Double) newVal);
        });

        CzerniceControl<Slider> midFreqSlider = new CzerniceControl<>(new Slider(-12, 12, 0));
        midFreqSlider.setLayoutX(450);
        midFreqSlider.setLayoutY(sliderYs.get(4));
        midFreqSlider.setPrefSize(150, 20);
        midFreqSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setMidFreqGain((Double) newVal);
        });

        CzerniceControl<Slider> highFreqSlider = new CzerniceControl<>(new Slider(-12, 12, 0));
        highFreqSlider.setLayoutX(450);
        highFreqSlider.setLayoutY(sliderYs.get(5));
        highFreqSlider.setPrefSize(150, 20);
        highFreqSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setHighFreqGain((Double) newVal);
        });

        CzerniceControl<Button> supernovaButton = new CzerniceControl<>(new Button("SUPERNOVA MOED"));
        supernovaButton.setLayoutX(600);
        supernovaButton.setLayoutY(100);
        supernovaButton.setPrefSize(120, 30);
        supernovaButton.main.setOnMouseClicked(e -> {
            CzerniceControl.toggleSupernovaMode();
        });




        this.getChildren().addAll(bg,
                timeMenuBar,
                backButton,
                widthInput, heightInput, mineCountInput, updateButton,
                colorButton,
                volumeSlider, rateSlider, balanceSlider, lowFreqSlider, midFreqSlider, highFreqSlider, musicLabel,
                supernovaButton);
    }

    private void updateButtonClick() {
        try {
            int newWidth = Integer.parseInt(widthInput.main.getText());
            int newHeight = Integer.parseInt(heightInput.main.getText());
            int newMineCount = Integer.parseInt(mineCountInput.main.getText());

            Settings.this.gameSettings.setWidth(newWidth);
            Settings.this.gameSettings.setHeight(newHeight);
            Settings.this.gameSettings.setMineCount(newMineCount);
        } catch (NumberFormatException e) {

        }



        System.out.println("Submittied text!");
    }

    private void addTimeMenuItems() {
        for (Units unit : Units.values()) {
            if (unit == Units.SECOND) continue;
            MenuItem item = new MenuItem(String.format("%s - %s", unit.displayName(), unit.symbol()));
            item.setOnAction(e -> {
                Settings.this.gameSettings.setUnit(unit);
                System.out.println("CHANGED UTNI");
            });
            timeMenu.getItems().add(item);
        }
    }

    @Override
    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }
}
