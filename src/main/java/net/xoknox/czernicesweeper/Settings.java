package net.xoknox.czernicesweeper;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import lombok.Setter;

import static net.xoknox.czernicesweeper.Utils.randomColor;

public class Settings extends Pane implements IGameSettingable {
    private GameSettings gameSettings;

    private final TextField widthInput;
    private final TextField heightInput;
    private final TextField mineCountInput;
    private final Menu timeMenu = new Menu("Time unit");


    public Settings(GameSettings settings) {
        this.setBackground(Background.EMPTY);
        this.gameSettings = settings;

        addTimeMenuItems();
        MenuBar timeMenuBar = new MenuBar(timeMenu);
        timeMenuBar.setBackground(Background.fill(randomColor()));
        timeMenuBar.setLayoutX(50);
        timeMenuBar.setLayoutY(100);
        timeMenuBar.setScaleX(2);


        Button backButton = new Button("Bacc");
        backButton.setLayoutX(Utils.WINDOW_CENTER[0]);
        backButton.setLayoutY(700);
        backButton.setPrefSize(70, 20);
        backButton.setOnMouseClicked(e -> gameSettings.getStage().setScene(gameSettings.getGameScene()));
        backButton.setBackground(Background.fill(randomColor()));

        widthInput = new TextField();
        widthInput.setPromptText("Board width");
        widthInput.setBackground(Background.fill(randomColor()));
        widthInput.setLayoutX(200);
        widthInput.setLayoutY(150);
        
        heightInput = new TextField();
        heightInput.setPromptText("Board height");
        heightInput.setBackground(Background.fill(randomColor()));
        heightInput.setLayoutX(200);
        heightInput.setLayoutY(200);

        mineCountInput = new TextField();
        mineCountInput.setPromptText("Board mine count");
        mineCountInput.setBackground(Background.fill(randomColor()));
        mineCountInput.setLayoutX(200);
        mineCountInput.setLayoutY(250);

        Button updateButton = new Button("Update");
        updateButton.setLayoutX(200);
        updateButton.setLayoutY(100);
        updateButton.setBackground(Background.fill(randomColor()));
        updateButton.setTextFill(Utils.randomColor());
        updateButton.setOnMouseClicked(e -> {
            updateButtonClick();
        });


        Button colorButton = new Button("Randomeiz colers!");
        colorButton.setLayoutX(300);
        colorButton.setLayoutY(100);
        colorButton.setBackground(Background.fill(randomColor()));
        colorButton.setOnMouseClicked(e -> {
            gameSettings.getGameScene().setFill(randomColor());
            gameSettings.getSettingsScene().setFill(randomColor());
            Utils.randomizeColorsInParent(gameSettings.getGameScene().getRoot());
            Utils.randomizeColorsInParent(gameSettings.getSettingsScene().getRoot());
        });



        Slider volumeSlider = new Slider(0, 1, 1);
        volumeSlider.setLayoutX(450);
        volumeSlider.setLayoutY(150);
        volumeSlider.setBackground(Background.fill(randomColor()));
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.this.gameSettings.setMusicVolume((Double) newVal);
        });

        Label volumeLabel = new Label("Laustärke");
        volumeLabel.setLayoutX(450);
        volumeLabel.setLayoutY(100);
        volumeLabel.setBackground(Background.fill(randomColor()));



        this.getChildren().addAll(timeMenuBar, backButton, widthInput, heightInput, mineCountInput, updateButton, colorButton, volumeSlider, volumeLabel);
    }

    private void updateButtonClick() {
        int newWidth = Integer.parseInt(widthInput.getText());
        int newHeight = Integer.parseInt(heightInput.getText());
        int newMineCount = Integer.parseInt(mineCountInput.getText());
        Settings.this.gameSettings.setWidth(newWidth);
        Settings.this.gameSettings.setHeight(newHeight);
        Settings.this.gameSettings.setMineCount(newMineCount);

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
