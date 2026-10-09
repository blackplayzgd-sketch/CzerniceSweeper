package net.xoknox.czernicesweeper;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.xoknox.czernicesweeper.event.RemainingMinesChangedEvent;
import net.xoknox.czernicesweeper.event.WinEvent;

import static net.xoknox.czernicesweeper.Utils.MINE_COUNT;
import static net.xoknox.czernicesweeper.Utils.WINDOW_SIZE;

public class Czernicesweeper extends Application {
    private double t = 0;


    @Override
    public void start(Stage primaryStage) throws Exception {
        Utils.init();

        Group root = new Group();
        Scene scene = new Scene(root, WINDOW_SIZE[0], WINDOW_SIZE[1], Color.color(0.2, 0.3,0.3));
        Image icon = new Image("mine.png");

        spawnTitle(root);
        Engine engine = spawnEngine(root);

        Text mineText = new Text("Mines left: " + MINE_COUNT);
        mineText.setX(800);
        mineText.setY(50);
        mineText.setFont(Font.font("impact", 40));
        mineText.setFill(Color.BLANCHEDALMOND);

        ImageView winView = new ImageView(Textures.WIN.get());
        winView.setX(860);
        winView.setY(400);
        winView.setScaleX(0.5);
        winView.setScaleY(0.5);

        winView.setVisible(false);

        Game sweeper = new Game(root, 50, 100, 30, 20, MINE_COUNT);
        sweeper.clickButton.addEventHandler(
                RemainingMinesChangedEvent.REMAINING_MINES_CHANGED,
                e -> {
                    mineText.setText("Mines left: " + e.getRemainingMines());
                    System.out.println(sweeper.getRemainingMines());
                }
        );
        sweeper.clickButton.addEventHandler(
                WinEvent.WIN,
                e -> {
                    winView.setVisible(true);
                });

        doNothing();

        Button winButton = new Button("Win");
        winButton.setOnMouseClicked(e -> sweeper.win());

        Button resetButton = new Button("Reset Le Boord Boii");
        resetButton.setLayoutX(1000);
        resetButton.setLayoutY(100);
        resetButton.setFont(Font.font("impact", 30));
        resetButton.setBackground(Background.fill(Color.FUCHSIA));
        resetButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                System.out.println("clllick!");
                winView.setVisible(false);
                sweeper.reset();
            };
        });

        Text timerText = new Text("Time elapsed: 0.0F♭⁻¹");
        timerText.setFont(Font.font("impact", 40));
        timerText.setFill(Color.MEDIUMORCHID);
        timerText.setX(400);
        timerText.setY(800);

        final long[] previousTime = {0};
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (previousTime[0] == 0) {
                    previousTime[0] = now;
                    return;
                }

                double dt = (now - previousTime[0]) / 1_000_000_000.0;
                previousTime[0] = now;

                engine.step(dt);
                t += dt;

                timerText.setText("Time elapsed: " + (Math.floor(t * 329.63d * 10) / 10) + " F♭⁻¹");
            }
        };



        root.getChildren().addAll(mineText, resetButton, winButton, winView, timerText);

        primaryStage.setScene(scene);
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Czernicesweeper v0.0.1-θ");

        primaryStage.show();
        timer.start();

    }

    private Engine spawnEngine(Group root) {
        Engine engine = new Engine(root,  1000, 300, 40, 120, 10, 50, 60);

        return engine;

    }

    private void spawnTitle(Group root) {
        Text text = new Text(100, 70, "Czernicesweeper v0.0.1-θ");
        text.setFont(Font.font("impact", 50));
        text.setFill(Color.WHEAT);

        root.getChildren().add(text);
    }

    private void doNothing() {

    }
}
