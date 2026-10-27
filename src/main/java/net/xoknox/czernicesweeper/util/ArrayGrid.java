package net.xoknox.czernicesweeper.util;

import java.util.ArrayList;

public class ArrayGrid<T> {
    private ArrayList<ArrayList<T>> grid = new ArrayList<>();
    private int width;
    private int height;
    private T def;

    public ArrayGrid(int width, int height, T defaultValue) {
        this.width = width;
        this.height = height;
        this.def = defaultValue;

        populateGrid(defaultValue);
    }

    public void set(int x, int y, T value) throws ArrayIndexOutOfBoundsException{
        if (x > width - 1 || y > height - 1 || x < 0 || y < 0)
            throw new ArrayIndexOutOfBoundsException(String.format("Indices (%d, %d) out of bounds for lengths (%d, %d)", x, y, width, height));
        grid.get(y).set(x, value);
    }

    public T get(int x, int y) throws ArrayIndexOutOfBoundsException{
        if (x > width - 1 || y > height - 1 || x < 0 || y < 0)
            throw new ArrayIndexOutOfBoundsException(String.format("Indices  %d, %d) out of bounds for lengths (%d, %d)", x, y, width, height));
        return grid.get(y).get(x);
    }

    private void populateGrid(T value) {
        ArrayList<T> row = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            row.add(i, value);
        }
        for (int i = 0; i < height; i++) {
            grid.add(i, (ArrayList<T>) row.clone());
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayGrid<T> copy() {
        ArrayGrid<T> newGrid = new ArrayGrid<>(width, height, def);
        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y++) {
                newGrid.set(x, y, get(x, y));
            }
        }

        return newGrid;
    }
}
