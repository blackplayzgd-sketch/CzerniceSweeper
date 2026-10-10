package net.xoknox.czernicesweeper;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static net.xoknox.czernicesweeper.Utils.randomColor;

public class Settings extends Pane {
    private Stage stage;
    private GameSettings gameSettings;
    private Scene gameScene;

    private final Menu timeMenu = new Menu("Time unit");


    public Settings(GameSettings settings) {
        this.setBackground(Background.EMPTY);
        this.gameSettings = settings;
        updateSettings();

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
        backButton.setOnMouseClicked(e -> stage.setScene(gameScene));
        backButton.setBackground(Background.fill(randomColor()));

        this.getChildren().addAll(timeMenuBar, backButton);
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

    public void updateSettings() {
        this.stage = gameSettings.getStage();
        this.gameScene = gameSettings.getGameScene();
    }

    public void setGameSettings(GameSettings settings) {
        this.gameSettings = settings;
        updateSettings();
    }
}
