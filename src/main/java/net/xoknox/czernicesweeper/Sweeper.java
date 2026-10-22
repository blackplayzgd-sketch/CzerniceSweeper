package net.xoknox.czernicesweeper;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import net.xoknox.czernicesweeper.event.*;
import net.xoknox.czernicesweeper.ui.CzerniceControl;

import java.util.ArrayList;
import java.util.Optional;

import static net.xoknox.czernicesweeper.Utils.*;

public class Sweeper extends Group {
    private final CzerniceControl<Button> resetButton = new CzerniceControl<>(new Button("Reset Le Boord Boii"));
    private final Text mineText = new Text("Mines left: " + MINE_COUNT);
    private final ImageView winView = new ImageView(Textures.WIN);
    public final TextTimer timer = new TextTimer("Time elapsed: %e %s", Units.HOUR);
    private final DtAnimationTimer circleTimer = new DtAnimationTimer() {
        double time = 0;
        @Override
        public void step(double dt) {

            if (time > 2.5) {
                spawnOsuCircle();
                time = 0;
            }
            time += dt;
        }
    };

    @Getter
    private final SweeperBoard board;

    @Getter
    @Setter
    private boolean hardMode = false;
    @Getter
    @Setter
    private boolean formidableMode = false;

    public int t = 0;

    public Sweeper(int width, int height, int mineCount) {
        this.board = new SweeperBoard(width, height, mineCount);
        board.setLayoutX(50);
        board.setLayoutY(100);

        Button b = new Button("osu!!");
        b.setOnMouseClicked(e -> {
            formidableMode = true;
        });

        this.getChildren().addAll(mineText, resetButton, winView, timer, board, b);

        setupUI();
        board.placeMines();

        board.addEventHandler(BoardInteractedEvent.BOARD_INTERACTED, e -> {
            if (hardMode && e.isByPlayer() && e.isDidRevealTiles() && !e.isRightClick()) spawnSkillCheck(e.getPos());
            if (formidableMode) circleTimer.start();
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
            Optional<IMinigameable> minigame = e.getOrigin() instanceof IMinigameable m ? Optional.of(m) : Optional.empty();
            minigame.ifPresent(iMinigameable -> getChildren().remove(iMinigameable));

            if (getChildren().stream().filter(n -> n instanceof SkillCheck).toList().isEmpty()) {
                board.setPlaying(true);
            }

            if (e.getEventType() == MinigameEvent.MINIGAME_LOST) {
                board.die();
            }
        });


    }

    private void spawnOsuCircle() {
        double r = Utils.fracOfRange(60, 10, board.getRevealedFractionNoMines());
        OsuCircle c = new OsuCircle(r, Utils.fracOfRange(20, 120, board.getRevealedFractionNoMines()), 1.5 * r);
        c.setLayoutX(Utils.randDRange(board.getLayoutX(), board.getLayoutX() + board.getWidth() * SweeperBoard.CELL_SIZE));
        c.setLayoutY(Utils.randDRange(board.getLayoutY(), board.getLayoutY() + board.getHeight() * SweeperBoard.CELL_SIZE));
        c.start();
        this.getChildren().add(c);
    }

    private void spawnSkillCheck(double[] pos) {
        if (Utils.rollChance(0.5)) {
            double width = 180;
            board.setPlaying(false);
            SkillCheck skillCheck = new SkillCheck(width,Utils.fracOfRange(100, 400, board.getRevealedFractionNoMines()), Utils.fracOfRange(100, 10, board.getRevealedFractionNoMines()));
            skillCheck.setLayoutX(pos[0] + board.getLayoutX() - width / 2);
            skillCheck.setLayoutY(pos[1] + board.getLayoutY() + 5);
            skillCheck.start();
            getChildren().add(skillCheck);
        }

    }

    public void die() {
        circleTimer.stop();
        timer.stop();
        removeMinigames();
    }

    public void win() {
        winView.setVisible(true);
        timer.stop();
        circleTimer.stop();
        removeMinigames();
    }

    public void reset() {
        t = 0;
        timer.reset();
        timer.stop();
        circleTimer.stop();
        winView.setVisible(false);
        removeMinigames();

    }

    private void removeMinigames() {
        ArrayList<IMinigameable> toRemove = new ArrayList<>(1);
        for (Node child : getChildren()) {
            if (child instanceof IMinigameable minigame) toRemove.add(minigame);
        }
        getChildren().removeAll(toRemove);
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



}
