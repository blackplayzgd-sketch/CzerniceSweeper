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
    private final ImageView bg = new ImageView();

    public Settings(GameSettings settings) {
        this.setBackground(new Background(new BackgroundImage(
                Textures.SETTINGS_BG,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT
        )));

        bg.setImage(Textures.SETTINGS_BG);
        bg.setFitWidth(WINDOW_SIZE[0]);
        bg.setFitHeight(WINDOW_SIZE[1]);
        System.out.println(Textures.SETTINGS_BG.getWidth());



        DtAnimationTimer timer = new DtAnimationTimer() {
            double t = 0;
            @Override
            public void step(double dt) {
                t += 2 * dt;

                double sx = (Math.sin(t) + 1) / 2;
                double sy = (Math.sin(t) + 1) / 2;



                bg.setScaleX(fracOfRange(1, 1.4, sx));
                bg.setScaleY(fracOfRange(1.4, 1, sy));

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
            Settings.this.bg.setEffect(Utils.randomColorAdjust());
            gameSettings.getGame().getBg().setEffect(Utils.randomColorAdjust());
        });

        CzerniceControl<Label> musicLabel = new CzerniceControl<>(new Label("Musik"));
        musicLabel.setLayoutX(450);
        musicLabel.setLayoutY(100);
        musicLabel.main.setAlignment(Pos.BASELINE_CENTER);
        musicLabel.setPrefSize(100, 40);

        ArrayList<Double> sliderYs = new ArrayList<>(List.of(150d, 170d, 190d, 210d, 230d, 250d, 270d, 290d, 310d, 330d, 350d, 370d));

        //sliderYs = Utils.shuffle(sliderYs);

        CzerniceControl<Slider> volumeSlider = new CzerniceControl<>(new Slider(0, 1, 0.5));
        volumeSlider.setLayoutX(450);
        volumeSlider.setLayoutY(sliderYs.get(0));
        volumeSlider.setPrefSize(150, 20);
        volumeSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setMusicVolume(newVal.doubleValue());
        });

        CzerniceControl<Slider> rateSlider = new CzerniceControl<>(new Slider(0, 2, 1));
        rateSlider.setLayoutX(450);
        rateSlider.setLayoutY(sliderYs.get(1));
        rateSlider.setPrefSize(150, 20);
        rateSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setMusicRate(newVal.doubleValue());
        });

        CzerniceControl<Slider> balanceSlider = new CzerniceControl<>(new Slider(-1, 1, 0));
        balanceSlider.setLayoutX(450);
        balanceSlider.setLayoutY(sliderYs.get(2));
        balanceSlider.setPrefSize(150, 20);
        balanceSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setMusicBalance(newVal.doubleValue());
        });

        CzerniceControl<Slider> lowFreqSlider = new CzerniceControl<>(new Slider(-50, 50, 0));
        lowFreqSlider.setLayoutX(450);
        lowFreqSlider.setLayoutY(sliderYs.get(3));
        lowFreqSlider.setPrefSize(150, 20);
        lowFreqSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setLowFreqGain(newVal.doubleValue());
        });

        CzerniceControl<Slider> midFreqSlider = new CzerniceControl<>(new Slider(-50, 50, 0));
        midFreqSlider.setLayoutX(450);
        midFreqSlider.setLayoutY(sliderYs.get(4));
        midFreqSlider.setPrefSize(150, 20);
        midFreqSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setMidFreqGain(newVal.doubleValue());
        });

        CzerniceControl<Slider> highFreqSlider = new CzerniceControl<>(new Slider(-50, 50, 0));
        highFreqSlider.setLayoutX(450);
        highFreqSlider.setLayoutY(sliderYs.get(5));
        highFreqSlider.setPrefSize(150, 20);
        highFreqSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setHighFreqGain(newVal.doubleValue());
        });

        CzerniceControl<Slider> bitDepthSlider = new CzerniceControl<>(new Slider(0, 48, 24));
        bitDepthSlider.setLayoutX(450);
        bitDepthSlider.setLayoutY(sliderYs.get(6));
        bitDepthSlider.setPrefSize(150, 20);
        bitDepthSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setBitDepth(newVal.doubleValue());
        });

        CzerniceControl<Slider> wetSlider = new CzerniceControl<>(new Slider(-1, 1, 0));
        wetSlider.setLayoutX(450);
        wetSlider.setLayoutY(sliderYs.get(7));
        wetSlider.setPrefSize(150, 20);
        wetSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setWet(newVal.doubleValue());
        });

        CzerniceControl<Slider> drySlider = new CzerniceControl<>(new Slider(-1, 1, 0));
        drySlider.setLayoutX(450);
        drySlider.setLayoutY(sliderYs.get(8));
        drySlider.setPrefSize(150, 20);
        drySlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setDry(newVal.doubleValue());
        });

        CzerniceControl<Slider> feedbackSlider = new CzerniceControl<>(new Slider(-1, 1, 0));
        feedbackSlider.setLayoutX(450);
        feedbackSlider.setLayoutY(sliderYs.get(9));
        feedbackSlider.setPrefSize(150, 20);
        feedbackSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setFeedback(newVal.doubleValue());
        });

        CzerniceControl<Slider> rateDivisorSlider = new CzerniceControl<>(new Slider(-1, 1, 1));
        rateDivisorSlider.setLayoutX(450);
        rateDivisorSlider.setLayoutY(sliderYs.get(10));
        rateDivisorSlider.setPrefSize(150, 20);
        rateDivisorSlider.main.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setRateDivisor(newVal.doubleValue());
        });

        CzerniceControl<Button> supernovaButton = new CzerniceControl<>(new Button("SUPERNOVA MOED"));
        supernovaButton.setLayoutX(600);
        supernovaButton.setLayoutY(100);
        supernovaButton.setPrefSize(120, 30);
        supernovaButton.main.setOnMouseClicked(e -> {
            CzerniceControl.toggleSupernovaMode();
            ImageView mainBg = gameSettings.getGame().getBg();
            if (mainBg.getImage() == Textures.MAIN_BG) {
                mainBg.setImage(Textures.MAIN_BG_SUPERNOVA);
            } else {
                mainBg.setImage(Textures.MAIN_BG);
            }

            if (bg.getImage() == Textures.SETTINGS_BG) {
                bg.setImage(Textures.SETTINGS_BG_SUPERNOVA);
            } else {
                bg.setImage(Textures.SETTINGS_BG);
            }

        });




        this.getChildren().addAll(bg,
                timeMenuBar,
                backButton,
                widthInput, heightInput, mineCountInput, updateButton,
                colorButton,
                volumeSlider, rateSlider, balanceSlider, lowFreqSlider, midFreqSlider, highFreqSlider, bitDepthSlider, wetSlider, drySlider, feedbackSlider, rateDivisorSlider, musicLabel,
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
