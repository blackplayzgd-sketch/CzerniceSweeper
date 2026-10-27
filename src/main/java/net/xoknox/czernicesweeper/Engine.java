package net.xoknox.czernicesweeper;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import net.xoknox.czernicesweeper.util.Utils;

public class Engine extends Group {
    private final Circle circle = new Circle(30);
    private final Circle circlePoint = new Circle(5);
    private final Circle pistonPoint = new Circle(5);
    private final Line shaft = new Line(0, 0, 100, 100);
    private final Rectangle piston = new Rectangle(0, 0, 100, 100);
    public final DtAnimationTimer timer;

    private final double shaftLength;
    private final double pistonWidth;
    private final double pistonHeight;
    private double posX;
    private double posY;

    private double time = 0;
    private float speedMulti = 1;
    private long previousTime = 0;


    public Engine(double posX, double posY, double size, double shaftLength, double shaftGirth, double pistonWidth, double pistonHeight) {

        this.shaftLength = shaftLength;
        this.pistonWidth = pistonWidth;
        this.pistonHeight = pistonHeight;
        this.posX = posX;
        this.posY = posY;

        circle.setRadius(size);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Utils.randomColor());

        shaft.setStrokeWidth(shaftGirth);
        shaft.setFill(Utils.randomColor());

        piston.setWidth(pistonWidth);
        piston.setHeight(pistonHeight);
        piston.setFill(Utils.randomColor());

        setCenters();
        this.getChildren().addAll(circle, shaft, piston);
        Utils.NO_PAINT.add(circle);

        timer = new DtAnimationTimer() {
            @Override
            public void step(double dt) {
                Engine.this.step(dt);
            }
        };
    }

    public void step(double dt) {
        time += dt * 3 * speedMulti;

        double cx = circle.getRadius() * Math.cos(time);
        double cy = circle.getRadius() * Math.sin(time);

        double px = 0;
        double py = Math.sqrt(shaftLength * shaftLength - cx * cx) + cy;

        circlePoint.setLayoutX(cx);
        circlePoint.setLayoutY(cy);

        pistonPoint.setLayoutX(px);
        pistonPoint.setLayoutY(py);

        shaft.setStartX(cx + posX);
        shaft.setStartY(cy + posY);
        shaft.setEndX(px + posX);
        shaft.setEndY(py + posY);

        piston.setX(px + posX + -pistonWidth/2);
        piston.setY(py + posY);


    }

    private void setCenters() {
        circle.setCenterX(posX);
        circle.setCenterY(posY);
        circlePoint.setCenterX(posX);
        circlePoint.setCenterY(posY);
        pistonPoint.setCenterX(posX);
        pistonPoint.setCenterY(posY);

    }

    public void setSpeedMulti(float multi) {
        speedMulti = multi;
    }

    public float getSpeedMulti() {
        return speedMulti;
    }
}
