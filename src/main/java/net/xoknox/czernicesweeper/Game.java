package net.xoknox.czernicesweeper;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import net.xoknox.czernicesweeper.event.RemainingMinesChangedEvent;
import net.xoknox.czernicesweeper.event.WinEvent;

public class Game {
    public static final int CELL_SIZE = 30;



    private final Group root;
    public final Button clickButton = new Button();

    private final int posX;
    private final int posY;
    private final int width;
    private final int height;
    private final int mineCount;

    private ArrayGrid<Boolean> mines;
    private ArrayGrid<Boolean> flags;
    private ArrayGrid<Boolean> revealed;

    private Group lastNode;
    private boolean hasClicked = false;
    private boolean dead = false;


    public Game(Group root, int posX, int posY, int width, int height, int mineCount) {
        this.root = root;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.mineCount = mineCount;
        setupClickButton();

        mines = new ArrayGrid<>(width, height, false);
        flags = new ArrayGrid<>(width, height, false);
        revealed = new ArrayGrid<>(width, height, false);

        root.getChildren().add(getNode());
        root.getChildren().add(clickButton);

        placeMines();
    }



    public void leftClick(int x, int y) {
        if (dead) return;
        if (isRevealed(x, y)) chord(x, y);
        if (isFlag(x, y)) return;

        if (isMine(x, y)) {
            if (!hasClicked) {
                reset();
                leftClick(x, y);
            } else {
                die();
            }
        } else {
            revealTile(x, y);

        }
        hasClicked = true;
    }

    public void rightClick(int x, int y) {
        flagTile(x, y);
    }

    public void reset() {
        mines = new ArrayGrid<>(width, height, false);
        flags = new ArrayGrid<>(width, height, false);
        revealed = new ArrayGrid<>(width, height, false);



        hasClicked = false;
        dead = false;
        placeMines();
        updateRemainingMineCount();
        updateBoardVisuals();
    }

    public void die() {
        dead = true;

    }

    public void win() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isMine(x, y) && !isRevealed(x, y)) revealTile(x, y);
            }
        }
        clickButton.fireEvent(new WinEvent());
        updateBoardVisuals();
    }

    public void placeMines() {
        int remaining = mineCount;

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
        revealed.set(x, y, true);

        if (isFlag(x, y)) flagTile(x, y);

        if (!hasMineAround(x, y)) {
            System.out.println("doesnt have mine");
            revealNeighbours(x, y);
        }


    }

    public void flagTile(int x, int y) {
        if (!isRevealed(x, y)) flags.set(x, y, !flags.get(x, y));
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

                    leftClick(x + i, y + j);

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

    private void updateRemainingMineCount() {
        clickButton.fireEvent(new RemainingMinesChangedEvent(getRemainingMines()));
    }

    public Group getNode() {
        Group group = new Group();


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Image stateImage = isRevealed(x, y) ? Textures.REVEALED.get() : Textures.HIDDEN.get();
                Image typeImage = getTypeImageForTile(x, y);

                double[] scale = Utils.getScaleForImage(Textures.HIDDEN.get());

                ImageView stateView = new ImageView(stateImage);
                ImageView typeView = new ImageView(typeImage);

                stateView.setScaleX(scale[0]);
                stateView.setScaleY(scale[1]);
                stateView.setX(CELL_SIZE * x + posX);
                stateView.setY(CELL_SIZE * y + posY);

                typeView.setScaleX(scale[0]);
                typeView.setScaleY(scale[1]);
                typeView.setX(CELL_SIZE * x + posX);
                typeView.setY(CELL_SIZE * y + posY);

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

        if (isFlag(x, y)) return Textures.FLAG.get();
        if (isMine(x, y)) return Textures.MINE.get();

        switch (mineAmount) {
            case 1: return Textures.ONE.get();
            case 2: return Textures.TWO.get();
            case 3: return Textures.THREE.get();
            case 4: return Textures.FOUR.get();
            case 5: return Textures.FIVE.get();
            case 6: return Textures.SIX.get();
            case 7: return Textures.SEVEN.get();
            case 8: return Textures.EIGHT.get();
            default: return Textures.EMPTY.get();
        }

    }

    private void setupClickButton() {
        clickButton.setLayoutX(posX);
        clickButton.setLayoutY(posY);
        clickButton.setPrefSize(CELL_SIZE * width, CELL_SIZE * height);
        clickButton.setBackground(Background.EMPTY);
        clickButton.setOnMousePressed(e -> {
            int clickedX = (int) ((e.getSceneX() - this.posX) / CELL_SIZE);
            int clickedY = (int) ((e.getSceneY() - this.posY) / CELL_SIZE);
            if (dead) return;
            if (e.getButton() == MouseButton.PRIMARY) {
                System.out.println("clickes!");
                leftClick(clickedX, clickedY);

            } else if (e.getButton() == MouseButton.SECONDARY) {
                rightClick(clickedX, clickedY);
            }
            checkWinCondition();
            updateBoardVisuals();
        });
    }

    private void checkWinCondition() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isMine(x, y)) if (!isFlag(x, y)) return;
            }
        }
        win();
    }

    private void updateBoardVisuals() {
        this.root.getChildren().remove(lastNode);
        this.root.getChildren().remove(clickButton);
        this.root.getChildren().add(getNode());
        this.root.getChildren().add(clickButton);
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
}
