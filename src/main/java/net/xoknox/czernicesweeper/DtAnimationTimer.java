package net.xoknox.czernicesweeper;

import javafx.animation.AnimationTimer;

public abstract class DtAnimationTimer extends AnimationTimer {
    private long previousTime = 0;


    @Override
    public void handle(long now) {
        if (previousTime == 0) {
            previousTime = now;
            return;
        }

        double dt = (now - previousTime) / 1_000_000_000.0;
        step(dt);

        previousTime = now;
    }

    public abstract void step(double dt);

}
