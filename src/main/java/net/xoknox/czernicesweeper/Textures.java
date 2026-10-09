package net.xoknox.czernicesweeper;

import javafx.scene.image.Image;

public enum Textures {
    HIDDEN(new Image("hidden.png")),
    REVEALED(new Image("revealed.png")),

    MINE(new Image("mine.png")),
    FLAG(new Image("flag.png")),

    EMPTY(new Image("empty.png")),
    ONE(new Image("1.png")),
    TWO(new Image("2.png")),
    THREE(new Image("3.png")),
    FOUR(new Image("4.png")),
    FIVE(new Image("5.png")),
    SIX(new Image("6.png")),
    SEVEN(new Image("7.png")),
    EIGHT(new Image("8.png")),

    WIN(new Image("win.png"));

    private Image texture;
    Textures(Image texture) {
        this.texture = texture;
    }

    public Image get() {
        return texture;
    }
}
