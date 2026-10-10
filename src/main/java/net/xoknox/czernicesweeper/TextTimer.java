package net.xoknox.czernicesweeper;

import javafx.animation.AnimationTimer;
import javafx.scene.text.Text;

public class TextTimer extends Text {
    private Units unit;
    private String text;
    private long previousTime = 0;
    private double t = 0;
    private boolean paused = true;

    public AnimationTimer timer = new AnimationTimer() {

        @Override
        public void handle(long now) {
            if (paused) {
                previousTime = 0;
                return;
            }

            if (previousTime == 0) {
                previousTime = now;
                return;
            }

            double dt = (now - previousTime) / 1_000_000_000.0;
            previousTime = now;
            t += dt;
            TextTimer.this.updateText();
        }
    };

    public TextTimer(String text, Units unit) {
        this.text = text;
        this.unit = unit;
        updateText();
    }

    private void updateText() {
        this.setText(String.format(text, t * unit.t(), unit.symbol()));
    }

    public void setUnit(Units unit) {
        this.unit = unit;
        updateText();
    }

    public void start() {
        paused = false;
        timer.start();
    }

    public void stop() {
        previousTime = 0;
        timer.stop();
    }

    public void reset() {
        t = 0;
        updateText();
    }
}
