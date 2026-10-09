package net.xoknox.czernicesweeper;

import javafx.scene.image.Image;

public class Utils {
    public static final int[] WINDOW_SIZE = new int[]{1300, 850};
    public static final int[] WINDOW_CENTER = new int[2];
    public static final int MINE_COUNT = 110;
    public static double[] getScaleForImage(Image image) {
        return new double[]{image.getWidth() / Game.CELL_SIZE, image.getHeight() / Game.CELL_SIZE};
    }

    public static void init() {
        WINDOW_CENTER[0] = WINDOW_SIZE[0] / 2;
        WINDOW_CENTER[1] = WINDOW_SIZE[1] / 2;
    }
}
