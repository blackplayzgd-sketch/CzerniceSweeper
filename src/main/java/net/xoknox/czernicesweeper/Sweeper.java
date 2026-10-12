package net.xoknox.czernicesweeper;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import net.xoknox.czernicesweeper.event.RemainingMinesChangedEvent;
import net.xoknox.czernicesweeper.event.TimeUnitChangedEvent;
import net.xoknox.czernicesweeper.event.WinEvent;

import static net.xoknox.czernicesweeper.Utils.MINE_COUNT;
import static net.xoknox.czernicesweeper.Utils.getScaleForImage;

public class Sweeper extends Group {
    public static final int CELL_SIZE = 30;

    private final Button clickButton = new Button();
    private final Button resetButton = new Button("Reset Le Boord Boii");
    private final Text mineText = new Text("Mines left: " + MINE_COUNT);
    private final ImageView winView = new ImageView(Textures.WIN);
    public final TextTimer timer = new TextTimer("Time elapsed: %e %s", Units.HOUR);

    private final int posX;
    private final int posY;

    private int width;
    private int height;
    private int mineCount;

    private ArrayGrid<Boolean> mines;
    private ArrayGrid<Boolean> flags;
    private ArrayGrid<Boolean> revealed;

    private Group lastNode;
    private boolean hasClicked = false;
    private boolean dead = false;
    private boolean playing = false;

    public int t = 0;


    public Sweeper(int posX, int posY, int width, int height, int mineCount) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.mineCount = mineCount;

        mines = new ArrayGrid<>(width, height, false);
        flags = new ArrayGrid<>(width, height, false);
        revealed = new ArrayGrid<>(width, height, false);

        this.getChildren().add(getNode());
        this.getChildren().add(clickButton);
        this.getChildren().addAll(mineText, resetButton, winView, timer);

        setupClickButton();
        setupUI();
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
        playing = false;
        t = 0;
        timer.reset();
        timer.stop();
        placeMines();
        updateRemainingMineCount();
        updateBoardVisuals();
    }

    public void die() {
        dead = true;
        timer.stop();
        System.out.println("DIED");
    }

    public void win() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isMine(x, y) && !isRevealed(x, y)) revealTile(x, y);
            }
        }
        winView.setVisible(true);
        timer.stop();
        clickButton.fireEvent(new WinEvent());
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
                Image stateImage = isRevealed(x, y) ? Textures.REVEALED : Textures.HIDDEN;
                Image typeImage = getTypeImageForTile(x, y);

                double[] scaleState = Utils.getScaleForImage(stateImage);
                double[] scaleType = Utils.getScaleForImage(typeImage);

                ImageView stateView = new ImageView(stateImage);
                ImageView typeView = new ImageView(typeImage);

                stateView.setScaleX(scaleState[0]);
                stateView.setScaleY(scaleState[1]);
                stateView.setX(CELL_SIZE * x + posX);
                stateView.setY(CELL_SIZE * y + posY);

                typeView.setScaleX(scaleType[0]);
                typeView.setScaleY(scaleType[1]);
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

    private void setupUI() {
        mineText.setX(800);
        mineText.setY(50);
        mineText.setFont(Font.font("impact", 40));
        mineText.setFill(Utils.randomColor());

        winView.setX(860);
        winView.setY(400);
        winView.setScaleX(0.5);
        winView.setScaleY(0.5);
        winView.setVisible(false);

        resetButton.setLayoutX(1000);
        resetButton.setLayoutY(100);
        resetButton.setFont(Font.font("impact", 30));
        resetButton.setBackground(Background.fill(Utils.randomColor()));
        resetButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                System.out.println("clllick!");
                winView.setVisible(false);
                this.reset();
            };
        });

        timer.setFont(Font.font("impact", 40));
        timer.setFill(Utils.randomColor());
        timer.setX(200);
        timer.setY(800);
        timer.addEventHandler(
                TimeUnitChangedEvent.TIME_UNIT_CHANGED,
                e -> {
                    timer.setUnit(e.getUnit());
                    System.out.println("EVENT GOTTEN!!!");
                }
        );
    }

    private void setupClickButton() {
        Utils.NO_PAINT.add(clickButton);
        clickButton.setLayoutX(posX);
        clickButton.setLayoutY(posY);
        clickButton.setPrefSize(CELL_SIZE * width, CELL_SIZE * height);
        clickButton.setBackground(Background.EMPTY);
        clickButton.setOnMousePressed(e -> {
            System.out.println("CLLLLICK");
            if (!dead) {
                int clickedX = (int) ((e.getSceneX() - this.posX) / CELL_SIZE);
                int clickedY = (int) ((e.getSceneY() - this.posY) / CELL_SIZE);
                if (e.getButton() == MouseButton.PRIMARY) {
                    System.out.println("clickes!");
                    leftClick(clickedX, clickedY);
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    rightClick(clickedX, clickedY);
                }
                playing = true;
                System.out.println("CLLLICK (2)");
                if (!dead) timer.start();
            }
            checkWinCondition();
            updateBoardVisuals();
        });
        clickButton.addEventHandler(
                RemainingMinesChangedEvent.REMAINING_MINES_CHANGED,
                e -> {
                    mineText.setText("Mines left: " + e.getRemainingMines());
                    System.out.println(getRemainingMines());
                }
        );
        clickButton.addEventHandler(
                WinEvent.WIN,
                e -> {

                });
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

}
