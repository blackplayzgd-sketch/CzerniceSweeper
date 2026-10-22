package net.xoknox.czernicesweeper;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import net.xoknox.czernicesweeper.ui.CzerniceControl;

import java.net.URL;
import java.util.*;

public class Utils {
    public static final int[] WINDOW_SIZE = new int[]{1300, 850};
    public static final int[] WINDOW_CENTER = new int[2];
    public static final int MINE_COUNT = 110;
    public static final ArrayList<Node> NO_PAINT = new ArrayList<>();
    public static final ArrayList<CzerniceControl<?>> CZERNICE = new ArrayList<>();

    private static final Property<Color> color = new SimpleObjectProperty<>(Color.WHEAT);

    public static double[] getScaleForImage(Image image) {
        //return new double[]{image.getWidth() / Sweeper.CELL_SIZE, image.getHeight() / Sweeper.CELL_SIZE};
        return new double[]{ SweeperBoard.CELL_SIZE / image.getWidth(), SweeperBoard.CELL_SIZE / image.getHeight()};
    }

    public static void init() {
        WINDOW_CENTER[0] = WINDOW_SIZE[0] / 2;
        WINDOW_CENTER[1] = WINDOW_SIZE[1] / 2;
    }

    public static Property<Color> colorProperty() {
        return color;
    }

    public static Color randomColor() {
        return Color.color(Math.random(), Math.random(), Math.random());
    }

    public static void randomizeColorsInParent(Parent input) {
        for (Node node : input.getChildrenUnmodifiable()) {
            if (NO_PAINT.contains(node)) return;

            if (node instanceof Region region) {
                if (region instanceof CzerniceControl<?> cc) {
                    cc.setCoolBG();
                } else
                if (region instanceof CheckBox checkBox) {
                    checkBox.setBackground(Background.fill(randomColor()));
                    continue;
                } else {
                    region.setBackground(Background.fill(randomColor()));
                }

            }

            try {
                if (node instanceof Shape shape) {
                    shape.setFill(randomColor());
                }
            } catch (RuntimeException e) {
                continue;
            }

            if (node instanceof Parent parent && !(node instanceof CzerniceControl<?>)) randomizeColorsInParent(parent);
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

    public static <T> ArrayList<T> shuffle(ArrayList<T> list) {
        ArrayList<T> copy = (ArrayList<T>) list.clone();
        ArrayList<T> output = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < list.size(); i++) {
            int removedIdx = randIntRange(0, size);
            output.add(copy.get(removedIdx));
            copy.remove(removedIdx);
            size--;
        }

        return output;
    }
    public static double fracOfRange(double x, double y, double p) {
        return x + p * (y - x);
    }

    public static ColorAdjust randomColorAdjust() {
        //return new ColorAdjust(Math.random(), Math.random() * 2 - 1, Math.random() * 2 - 1, 0);
        return new ColorAdjust(Math.random() * 2 - 1, 0, 0, Math.random() * 2 - 1);
    }

    public static boolean rollChance(double chance) {
        return Math.random() < chance;
    }

    public static double randDRange(double min, double max) {
        return  Math.random() * (max - min) + min;
    }
}
