package net.xoknox.czernicesweeper;

import static net.xoknox.czernicesweeper.util.Utils.dArr;

public enum SweeperMode {
    NORMAL(dArr(0)),
    HARD(dArr(100, 300), dArr( 100, 40)),
    FORMIDABLE(dArr(90, 55), dArr(50, 100), dArr(2.5, 2)),
    PREPOSTEROUS(dArr(50, 30), dArr(4, 3)),
    CZERNICE(dArr(0) ,dArr(350, 30), dArr(40, 140, 1.8), dArr(20, 2));

    private double[][] params;

    SweeperMode(double[]... params) {
        this.params = params;
    }


    public boolean doesSkillCheck() {
        return this.ordinal() > 0;
    }

    public boolean doesOsu() {
        return this.ordinal() > 1;
    }

    public double[][] get() {
        return params;
    }
}
