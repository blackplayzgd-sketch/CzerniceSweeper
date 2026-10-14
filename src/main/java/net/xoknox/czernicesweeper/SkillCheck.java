package net.xoknox.czernicesweeper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SkillCheck extends Group {
    private final Rectangle marker = new Rectangle(2, 15);
    private final double speed;
    private final double completionMargin;
    private final double width;
    private final DtAnimationTimer timer;
    private final double minCompletionX;
    private final double maxCompletionX;
    private final BooleanProperty win = new SimpleBooleanProperty(false);
    private final BooleanProperty lose = new SimpleBooleanProperty(false);

    private int moveDir = 1;

    public SkillCheck(double width, double speed, double completionMargin) {
        this.width = width;
        this.speed = speed;
        this.completionMargin = completionMargin;
        this.minCompletionX = Math.random() * (width - completionMargin);
        this.maxCompletionX = minCompletionX + completionMargin;

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

        Button clickButton = new Button("Checc");
        clickButton.setLayoutX(width / 2);
        clickButton.setLayoutY(15);

        clickButton.setOnMousePressed(event -> {
            System.out.println("clicked.");
            if (event.isPrimaryButtonDown()) click();
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
        win.set(true);
    }

    private void lose() {
        lose.set(true);
    }

    public BooleanProperty winProperty() {
        return win;
    }

    public BooleanProperty loseProperty() {
        return lose;
    }
}
