package net.xoknox.czernicesweeper.minigame;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import net.xoknox.czernicesweeper.DtAnimationTimer;
import net.xoknox.czernicesweeper.util.Utils;
import net.xoknox.czernicesweeper.event.MinigameEvent;
import net.xoknox.czernicesweeper.ui.CzerniceControl;

import java.util.ArrayList;

public class SkillCheck extends Group implements IMinigameable {
    private final static ArrayList<SkillCheck> REGISTRY = new ArrayList<>();
    private final Rectangle marker = new Rectangle(2, 15);
    private final double speed;
    private final double completionMargin;
    private final double width;
    private final DtAnimationTimer timer;
    private final double minCompletionX;
    private final double maxCompletionX;
    private int moveDir = 1;

    public SkillCheck(double width, double speed, double completionMargin) {
        REGISTRY.add(this);
        this.width = width;
        this.speed = speed;
        this.completionMargin = completionMargin;
        double marginMiddle = Utils.randDRange(0.5 - 0.1, 0.5 + 0.1) * (width);
        this.minCompletionX = marginMiddle - completionMargin / 2;
        this.maxCompletionX = marginMiddle + completionMargin / 2;

        marker.setY(-2.5);
        marker.setFill(Color.WHITE);

        Rectangle mainRect = new Rectangle(width, 10);
        Rectangle marginRect = new Rectangle(completionMargin, 10);
        marginRect.setX(minCompletionX);
        marginRect.setFill(Color.GREEN);

        timer = new DtAnimationTimer() {
            @Override
            public void step(double dt) {
                marker.setX(marker.getX() + SkillCheck.this.speed * moveDir * dt);

                moveDir = marker.getX() > mainRect.getWidth() ? -1 : marker.getX() < 0 ? 1 : moveDir;
            }
        };

        CzerniceControl<Button> clickButton = new CzerniceControl<>(new Button("Checc"));
        clickButton.setPrefSize(60, 10);
        clickButton.setLayoutX(width / 2);
        clickButton.setLayoutY(15);

        clickButton.main.setOnMousePressed(event -> {
            System.out.println("clicked.");
            if (event.isPrimaryButtonDown()) click();
        });

        clickButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.A) click();
        });




        getChildren().addAll(mainRect, marginRect, marker, clickButton);
    }

    public void start() {
        timer.start();
        System.out.println("started!!!");

    }

    private void click() {
        System.out.println("clllick!");
        if (minCompletionX < marker.getX() && marker.getX() < maxCompletionX) {
            win();
        } else {
            lose();
        }
    }

    private void win() {
        System.out.println("won!");
        fireEvent(new MinigameEvent(this, MinigameEvent.MINIGAME_WON));
    }

    private void lose() {
        fireEvent(new MinigameEvent(this, MinigameEvent.MINIGAME_LOST));
    }

    public static void onKeyboardInput(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        REGISTRY.forEach(sc -> {
            if (keyCode == KeyCode.SPACE) sc.click();
            System.out.println("SKILLEDD!!!");
        });
    }

}
