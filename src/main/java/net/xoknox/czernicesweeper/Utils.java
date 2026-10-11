package net.xoknox.czernicesweeper;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.net.URL;
import java.util.ArrayList;

public class Utils {
    public static final int[] WINDOW_SIZE = new int[]{1300, 850};
    public static final int[] WINDOW_CENTER = new int[2];
    public static final int MINE_COUNT = 110;
    public static final ArrayList<Node> NO_PAINT = new ArrayList<>();
    public static double[] getScaleForImage(Image image) {
        return new double[]{image.getWidth() / Sweeper.CELL_SIZE, image.getHeight() / Sweeper.CELL_SIZE};
    }

    public static void init() {
        WINDOW_CENTER[0] = WINDOW_SIZE[0] / 2;
        WINDOW_CENTER[1] = WINDOW_SIZE[1] / 2;
    }

    public static Color randomColor() {
        return Color.color(Math.random(), Math.random(), Math.random());
    }

    public static void randomizeColorsInParent(Parent input) {
        for (Node node : input.getChildrenUnmodifiable()) {
            if (NO_PAINT.contains(node)) return;
            if (node instanceof Region region) region.setBackground(Background.fill(randomColor()));
            try {
                if (node instanceof Shape shape) shape.setFill(randomColor());
            } catch (RuntimeException e) {
                continue;
            }
            if (node instanceof Parent parent) randomizeColorsInParent(parent);
        }
    }

    public static String getPath(String filePath) {
        URL url = CzerniceSweeper.class.getResource(filePath);

        if (url == null) {
            throw new IllegalStateException("Could not find " + filePath);
        }

        return url.toExternalForm();
    }

    public static int randIntRange(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }
}
