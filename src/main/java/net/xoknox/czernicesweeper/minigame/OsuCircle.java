package net.xoknox.czernicesweeper.minigame;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import jdk.jshell.execution.Util;
import net.xoknox.czernicesweeper.DtAnimationTimer;
import net.xoknox.czernicesweeper.event.MinigameEvent;
import net.xoknox.czernicesweeper.util.Utils;

import java.util.ArrayList;

public class OsuCircle extends Group implements IMinigameable {
    private static final double errorTollerance = 10;

    private final double r;
    private final Circle mainCircle;
    private final Circle hitCircle;
    private final DtAnimationTimer timer;
    private final Button clickButton;

    private boolean playing;

    public OsuCircle(double r, double speed, double dist) {
        this.r = r;

        mainCircle = new Circle(r);
        mainCircle.setScaleY(1.3);
        mainCircle.setFill(Color.color(0, 0.5f, 1f, 0.6));
        mainCircle.setOnMousePressed(e -> click());

        clickButton = new Button();
        clickButton.setPrefSize(2*r, 2*r);
        clickButton.setLayoutX(-r);
        clickButton.setLayoutY(-r);
        clickButton.setBackground(Background.EMPTY);
        clickButton.setOnMousePressed(e -> click());
        clickButton.setScaleY(1.3);

        hitCircle = new Circle(r + dist);
        hitCircle.setFill(Color.TRANSPARENT);
        hitCircle.setStroke(Color.RED);
        hitCircle.setScaleY(1.3);
        hitCircle.setMouseTransparent(true);


        timer = new DtAnimationTimer() {
            @Override
            public void step(double dt) {
                if (!playing) return;
                hitCircle.setRadius(hitCircle.getRadius() - speed * dt);
                checkEnd();
            }
        };

        getChildren().addAll(mainCircle, hitCircle, clickButton);
    }

    public void start() {
        playing = true;
        timer.start();
    }

    private void click() {
        playing = false;

        if (hitCircle.getRadius() < r - errorTollerance || hitCircle.getRadius() > r + errorTollerance) fireEvent(new MinigameEvent(this, MinigameEvent.MINIGAME_LOST));
        else fireEvent(new MinigameEvent(this, MinigameEvent.MINIGAME_WON));
        System.out.println("cliiicked");
    }

    private void checkEnd() {
        if (hitCircle.getRadius() < r - errorTollerance) {
            playing = false;
            fireEvent(new MinigameEvent(this, MinigameEvent.MINIGAME_LOST));
            System.out.println("shoulda been faster!!!!!");
        }
    }
}
