package net.xoknox.czernicesweeper;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import lombok.Getter;
import lombok.Setter;
import net.xoknox.czernicesweeper.event.*;
import org.intellij.lang.annotations.JdkConstants;

import java.util.ArrayList;

public class SweeperBoard extends Group {
    public static final int CELL_SIZE = 30;

    private final Button clickButton = new Button();

    private ArrayGrid<Boolean> mines;
    private ArrayGrid<Boolean> flags;
    private ArrayGrid<Boolean> revealed;

    @Getter
    private int width;
    @Getter
    private int height;
    private int mineCount;

    private Group lastNode;
    private boolean hasClicked = false;
    private boolean dead = false;
    @Setter
    private boolean playing = true;


    public SweeperBoard(int width, int height, int mineCount) {
        this.width = width;
        this.height = height;
        this.mineCount = mineCount;

        mines = new ArrayGrid<>(width, height, false);
        flags = new ArrayGrid<>(width, height, false);
        revealed = new ArrayGrid<>(width, height, false);

        setupClickButton();

        this.getChildren().add(getNode());
        this.getChildren().add(clickButton);
    }

    private void processFirstClick(int x, int y) {
        hasClicked = true;

        if (isMine(x, y)) {
                reset();
                leftClick(x, y, true);
        } else {
            leftClick(x, y, true);
            if (getRevealedCount() < width * height / 20) {
                reset();
                hasClicked = false;
                leftClick(x, y, true);
            }
        }
    }



    public void leftClick(int x, int y, boolean clickedBgPlayer) {
        if (dead) return;
        if (isFlag(x, y)) return;

        if (!hasClicked) processFirstClick(x, y);
        if (isRevealed(x, y) && clickedBgPlayer) chord(x, y);
        boolean revealedTiles = false;

        if (isMine(x, y)) {
            if (!hasClicked) {
                reset();
                leftClick(x, y, true);
            } else {
                die();
                return;
            }
        } else {
            revealTile(x, y);
            revealedTiles = true;
        }

        fireEvent(new BoardInteractedEvent(false, clickedBgPlayer, revealedTiles, new double[]{x * CELL_SIZE, y * CELL_SIZE}));

        hasClicked = true;
    }

    public void rightClick(int x, int y) {
        flagTile(x, y);
        fireEvent(new BoardInteractedEvent(true, true, false, new double[]{x * CELL_SIZE, y * CELL_SIZE}));
    }

    public void reset() {
        mines = new ArrayGrid<>(width, height, false);
        flags = new ArrayGrid<>(width, height, false);
        revealed = new ArrayGrid<>(width, height, false);

        hasClicked = false;
        dead = false;
        playing = true;

        placeMines();
        updateRemainingMineCount();
        updateBoardVisuals();

        fireEvent(new BoardResetEvent());

    }

    public void die() {
        dead = true;

        updateBoardVisuals();
        System.out.println("DIED");
        fireEvent(new DieEvent());
    }

    public void win() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isMine(x, y) && !isRevealed(x, y)) revealTile(x, y);
            }
        }


        fireEvent(new WinEvent());
        updateBoardVisuals();
    }

    public void placeMines() {
        int remaining = Math.min(mineCount, width * height);

        while (remaining > 0) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);

            if (!isMine(x, y)) {
                mines.set(x, y, true);
                remaining--;
            }
        }
    }

    public void chord(int x, int y) {
        System.out.println("chording!");
        if (getFlagsAroundCount(x, y) == getMinesAroundCount(x, y)) {
            revealNeighbours(x, y);
        }
    }

    public void revealTile(int x, int y) {
        setRevealed(x, y, true);

        if (isFlag(x, y)) flagTile(x, y);

        if (!hasMineAround(x, y)) {
            System.out.println("doesnt have mine");
            revealNeighbours(x, y);
        }


    }

    public void flagTile(int x, int y) {
        if (!isRevealed(x, y)) setFlag(x, y, !flags.get(x, y));
        updateRemainingMineCount();
    }

    private void revealNeighbours(int x, int y) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {
                    System.out.println("checking" + (x + i) + ", " + (y + j));
                    if (x + i < 0 || x + i > mines.getWidth() - 1) continue;
                    System.out.println("passsed if 1");
                    if (y + j < 0 || y + j > mines.getHeight() - 1) continue;
                    System.out.println("passed if 2");
                    if (isRevealed(x + i, y + j)) continue;

                    leftClick(x + i, y + j, false);

                }
            }
        }
    }

    private boolean hasMineAround(int x, int y) {

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {

                    if (x + i < 0 || x + i > width - 1) continue;
                    if (y + j < 0 || y + j > height - 1) continue;

                    if (isMine(x + i, y + j)) return true;

                }
            }
        }

        return false;
    }

    private boolean hasFlagAround(int x, int y) {

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {

                    if (x + i < 0 || x + i > width - 1) continue;
                    if (y + j < 0 || y + j > height - 1) continue;

                    if (isFlag(x + i, y + j)) return true;

                }
            }
        }

        return false;
    }

    private int getMinesAroundCount(int x, int y) {
        if (isMine(x, y)) return 0;
        if (!hasMineAround(x, y)) return 0;

        int sum = 0;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {

                    if (x + i < 0 || x + i > width - 1) continue;
                    if (y + j < 0 || y + j > height - 1) continue;

                    if (isMine(x + i, y + j)) sum++;

                }
            }
        }

        return sum;
    }

    private int getFlagsAroundCount(int x, int y) {
        if (!hasFlagAround(x, y)) return 0;

        int sum = 0;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {

                    if (x + i < 0 || x + i > width - 1) continue;
                    if (y + j < 0 || y + j > height - 1) continue;

                    if (isFlag(x + i, y + j)) sum++;

                }
            }
        }

        return sum;
    }

    private int getOpenAroundCount(int x, int y) {
        int sum = 0;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {

                    if (x + i < 0 || x + i > width - 1) continue;
                    if (y + j < 0 || y + j > height - 1) continue;

                    if (isRevealed(x + i, y + j)) sum++;

                }
            }
        }

        return sum;
    }

    private void updateRemainingMineCount() {
        clickButton.fireEvent(new RemainingMinesChangedEvent(getRemainingMines()));
    }

    public Group getNode() {
        Group group = new Group();


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Image stateImage = isRevealed(x, y) ? Textures.REVEALED : Textures.HIDDEN;
                Image typeImage = getTypeImageForTile(x, y);

                double[] scaleState = Utils.getScaleForImage(stateImage);
                double[] scaleType = Utils.getScaleForImage(typeImage);

                ImageView stateView = new ImageView(stateImage);
                ImageView typeView = new ImageView(typeImage);

                stateView.setScaleX(scaleState[0]);
                stateView.setScaleY(scaleState[1]);
                stateView.setX(CELL_SIZE * x);
                stateView.setY(CELL_SIZE * y);

                typeView.setScaleX(scaleType[0]);
                typeView.setScaleY(scaleType[1]);
                typeView.setX(CELL_SIZE * x);
                typeView.setY(CELL_SIZE * y);

                if (!isRevealed(x, y)) {
                    group.getChildren().add(stateView);
                    if (isFlag(x, y)) {
                        group.getChildren().add(typeView);
                    } else if (isMine(x, y) && dead) {
                        group.getChildren().add(typeView);
                    }
                } else {
                    group.getChildren().add(stateView);
                    group.getChildren().add(typeView);
                }
            }
        }

        lastNode = group;
        return group;
    }

    private Image getTypeImageForTile(int x, int y) {
        int mineAmount = getMinesAroundCount(x, y);

        if (isFlag(x, y)) return Textures.FLAG;
        if (isMine(x, y)) return Textures.MINE;

        switch (mineAmount) {
            case 1: return Textures.ONE;
            case 2: return Textures.TWO;
            case 3: return Textures.THREE;
            case 4: return Textures.FOUR;
            case 5: return Textures.FIVE;
            case 6: return Textures.SIX;
            case 7: return Textures.SEVEN;
            case 8: return Textures.EIGHT;
            default: return Textures.EMPTY;
        }

    }

    private void setupClickButton() {
        Utils.NO_PAINT.add(clickButton);
        clickButton.setPrefSize(CELL_SIZE * width, CELL_SIZE * height);
        clickButton.setBackground(Background.EMPTY);
        clickButton.setOnMousePressed(e -> {
            System.out.println("CLLLLICK");
            if (!dead && playing) {
                int clickedX = (int) ((e.getX()) / CELL_SIZE);
                int clickedY = (int) ((e.getY()) / CELL_SIZE);
                if (e.getButton() == MouseButton.PRIMARY) {
                    System.out.println("clickes!");
                    leftClick(clickedX, clickedY, true);
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    rightClick(clickedX, clickedY);
                }
                System.out.println("CLLLICK (2)");
                //if (!dead) timer.start();
            }

            checkWinCondition();
            updateBoardVisuals();

        });

    }

    public double getRevealedFractionNoMines() {
        int sum = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isRevealed(x, y)) sum++;
            }
        }
        return (double) sum / (width * height - mineCount);
    }

    public int getRevealedCount() {
        int sum = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isRevealed(x, y)) sum++;
            }
        }
        return sum;
    }

    private void checkWinCondition() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isMine(x, y)) if (!isFlag(x, y)) return;
                if (isFlag(x, y)) if (!isMine(x, y)) return;
            }
        }
        win();
    }

    private void updateBoardVisuals() {
        this.getChildren().remove(lastNode);
        this.getChildren().remove(clickButton);

        this.getChildren().add(getNode());
        this.getChildren().add(clickButton);
    }

    public boolean isRevealed(int x, int y) {
        return revealed.get(x, y);
    }

    public boolean isMine(int x, int y) {
        return mines.get(x, y);
    }

    public boolean isFlag(int x, int y) {
        return flags.get(x, y);
    }

    public int getRemainingMines() {
        int sum = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isFlag(x, y)) sum++;
            }
        }

        return mineCount - sum;
    }

    public void setWidth(int width) {
        this.width = width;
        this.reset();

    }
    public void setHeight(int height) {
        this.height = height;
        this.reset();
    }
    public void setMineCount(int mineCount) {

        this.mineCount = Math.min(mineCount, width * height - 1);
        this.reset();
    }

    public void setRevealed(int x, int y, boolean value) {
        revealed.set(x, y, value);

    }

    public void setFlag(int x, int y, boolean value) {
        flags.set(x, y, value);

    }
}
