package net.xoknox.czernicesweeper;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import net.xoknox.czernicesweeper.event.*;
import net.xoknox.czernicesweeper.minigame.IMinigameable;
import net.xoknox.czernicesweeper.minigame.OsuCircle;
import net.xoknox.czernicesweeper.minigame.PopupAd;
import net.xoknox.czernicesweeper.minigame.SkillCheck;
import net.xoknox.czernicesweeper.ui.CzerniceControl;
import net.xoknox.czernicesweeper.util.Utils;

import java.util.ArrayList;

import static net.xoknox.czernicesweeper.SweeperMode.*;
import static net.xoknox.czernicesweeper.util.Utils.*;

public class Sweeper extends Group {
    private final CzerniceControl<Button> resetButton = new CzerniceControl<>(new Button("Reset Le Boord Boii"));
    private final Text mineText = new Text("Mines left: " + MINE_COUNT);
    private final ImageView winView = new ImageView(Textures.WIN);
    public final TextTimer timer = new TextTimer("Time elapsed: %e %s", Units.HOUR);
    private final DtAnimationTimer circleTimer = new DtAnimationTimer() {
        double time = 0;
        @Override
        public void step(double dt) {

            if (time > modeStat(FORMIDABLE, 2)) {
                spawnOsuCircle();
                time = 0;
            }
            time += dt;
        }
    };
    private final DtAnimationTimer adTimer = new DtAnimationTimer() {
        double time = 0;
        @Override
        public void step(double dt) {
            if (time > modeStat(PREPOSTEROUS, 1)) {
                spawnAd();
                time = 0;
            }
            time += dt;
        }
    };

    private final Group minigames = new Group();
    private final Group osuCircles = new Group();
    private final Group ads = new Group();
    private final Group everythingElse = new Group();

    @Getter
    private final SweeperBoard board;

    @Getter
    @Setter
    private SweeperMode mode = SweeperMode.NORMAL;

    public int t = 0;

    private double mouseX = 0;
    private double mouseY = 0;

    private double modeStat(SweeperMode gm, int idx) {
        return isCzernice() ? CZERNICE.get()[gm.ordinal()][idx] : mode.ordinal() > gm.ordinal() ? gm.get()[idx][1] : rangeOfBoard(gm.get()[idx]);
    }

    public Sweeper(int width, int height, int mineCount) {
        this.board = new SweeperBoard(width, height, mineCount);
        board.setLayoutX(50);
        board.setLayoutY(100);

        System.out.println(modeStat(PREPOSTEROUS, 1));

        minigames.getChildren().addAll(everythingElse, ads, osuCircles);

        this.getChildren().addAll(mineText, resetButton, winView, timer, board, minigames);

        setupUI();
        board.placeMines();

        board.addEventHandler(BoardInteractedEvent.BOARD_INTERACTED, e -> {
            if (mode.ordinal() > SweeperMode.NORMAL.ordinal() && e.isByPlayer() && e.isDidRevealTiles() && !e.isRightClick()) spawnSkillCheck(e.getPos());
            if (mode.ordinal() > HARD.ordinal()) circleTimer.start();
            if (mode.ordinal() > FORMIDABLE.ordinal()) adTimer.start();
            timer.start();
        });
        board.addEventHandler(WinEvent.WIN, e -> win());
        board.addEventHandler(DieEvent.DIE, e -> {
            System.out.println("recieved die");
            die();
        });

        board.addEventHandler(BoardResetEvent.BOARD_RESET, e -> reset());

        addEventHandler(
                RemainingMinesChangedEvent.REMAINING_MINES_CHANGED,
                e -> {
                    mineText.setText("Mines left: " + e.getRemainingMines());
                    System.out.println(e.getRemainingMines());
                }
        );

        addEventHandler(MinigameEvent.ANY, e -> {
            System.out.println("i geta de event");

            IMinigameable minigame = e.getOrigin() instanceof IMinigameable m ? m : null;
            if (minigame != null) {
                if (minigame instanceof OsuCircle) osuCircles.getChildren().remove(minigame);
                else if (minigame instanceof PopupAd) ads.getChildren().remove(minigame);
                else everythingElse.getChildren().remove(minigame);
            }
            System.out.println("i remove de ad");

            if (getChildren().stream().filter(n -> n instanceof SkillCheck).toList().isEmpty()) {
                board.setPlaying(true);
            }

            if (e.getEventType() == MinigameEvent.MINIGAME_LOST) {
                board.die();
            }
        });

        addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        Button b = new Button("ad");
        b.setOnMouseClicked(e -> getChildren().add(new PopupAd(20)));

        getChildren().add(b);


    }

    private void spawnAd() {
        if (!Utils.rollChance(0.5) && !isCzernice()) return;

        PopupAd ad = new PopupAd(modeStat(PREPOSTEROUS, 0));
        ad.setLayoutX(mouseX - ad.getSize()[0] / 2);
        ad.setLayoutY(mouseY - ad.getSize()[1] / 2);

        ad.setLayoutX(Utils.clamp(ad.getLayoutX(), board.getLayoutX(), board.getLayoutX() + board.getWidth() * SweeperBoard.CELL_SIZE - ad.getSize()[0]));
        ad.setLayoutY(Utils.clamp(ad.getLayoutY(), board.getLayoutY(), board.getLayoutY() + board.getHeight() * SweeperBoard.CELL_SIZE - ad.getSize()[1]));


        ads.getChildren().add(ad);

    }

    private void spawnOsuCircle() {
        double r = modeStat(FORMIDABLE, 0);
        OsuCircle c = new OsuCircle(r, modeStat(FORMIDABLE, 1), 2.4 * r);

        do {
            c.setLayoutX(Utils.randDRange(board.getLayoutX(), board.getLayoutX() + board.getWidth() * SweeperBoard.CELL_SIZE));
            c.setLayoutY(Utils.randDRange(board.getLayoutY(), board.getLayoutY() + board.getHeight() * SweeperBoard.CELL_SIZE));
        } while (!Utils.isFurtherThan(c.getLayoutX(), c.getLayoutY(), mouseX, mouseY, 1.2 * r));

        c.start();
        osuCircles.getChildren().add(c);
    }

    private void spawnSkillCheck(double[] pos) {
        if (Utils.rollChance(0.5) || mode == CZERNICE) {
            double width = 180;
            board.setPlaying(false);
            SkillCheck skillCheck = new SkillCheck(width,modeStat(HARD, 0), modeStat(HARD, 1));
            skillCheck.setLayoutX(pos[0] + board.getLayoutX() - width / 2);
            skillCheck.setLayoutY(pos[1] + board.getLayoutY() + 5);
            skillCheck.start();
            everythingElse.getChildren().add(skillCheck);
        }
    }

    public void die() {
        circleTimer.stop();
        adTimer.stop();
        timer.stop();
        removeMinigames();
    }

    public void win() {
        winView.setVisible(true);
        timer.stop();
        adTimer.stop();
        circleTimer.stop();
        removeMinigames();
    }

    public void reset() {
        t = 0;
        timer.reset();
        timer.stop();
        circleTimer.stop();
        adTimer.stop();
        winView.setVisible(false);
        removeMinigames();

    }

    private void removeMinigames() {
        for (Node child : minigames.getChildren()) {
            if (child instanceof Group g) {
                ArrayList<IMinigameable> toRemove = new ArrayList<>(1);

                for (Node min : g.getChildren()) {
                    if (min instanceof IMinigameable m) toRemove.add(m);
                }
                g.getChildren().removeAll(toRemove);
            }
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
        resetButton.main.setFont(Font.font("impact", 30));
        resetButton.setPrefSize(280, 80);
        resetButton.main.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                System.out.println("clllick!");
                this.reset();
                board.reset();
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

    private double rangeOfBoard(double[] range) {
        return Utils.fracOfRange(range[0], range[1], board.getRevealedFractionNoMines());
    }

    private boolean isCzernice() {
        return mode == CZERNICE;
    }



}
