package net.xoknox.czernicesweeper;

import javafx.scene.Group;

public class SweeperBoard extends Group {

    private ArrayGrid<Boolean> mines;
    private ArrayGrid<Boolean> flags;
    private ArrayGrid<Boolean> revealed;

    public SweeperBoard(int width, int height, int mineCount) {

    }
}
