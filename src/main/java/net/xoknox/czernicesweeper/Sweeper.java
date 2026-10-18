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

import static net.xoknox.czernicesweeper.Utils.*;

public class Sweeper extends Group {
    private final CzerniceControl<Button> resetButton = new CzerniceControl<>(new Button("Reset Le Boord Boii"));
    private final Text mineText = new Text("Mines left: " + MINE_COUNT);
    private final ImageView winView = new ImageView(Textures.WIN);
    public final TextTimer timer = new TextTimer("Time elapsed: %e %s", Units.HOUR);

    private final ArrayList<ArrayList<Node>> renderLayers = new ArrayList<>(2);

    private SkillCheck skillCheck;

    @Getter
    private final SweeperBoard board;

    @Getter
    @Setter
    private boolean hardMode = false;

    public int t = 0;

    public Sweeper(int width, int height, int mineCount) {
        this.board = new SweeperBoard(width, height, mineCount);
        board.setLayoutX(50);
        board.setLayoutY(100);

        this.getChildren().addAll(mineText, resetButton, winView, timer, board);

        setupUI();
        board.placeMines();

        board.addEventHandler(BoardInteractedEvent.BOARD_INTERACTED, e -> {
            if (hardMode && e.isByPlayer() && e.isDidRevealTiles() && !e.isRightClick()) spawnSkillCheck(e.getPos());
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

    }

    private void spawnSkillCheck(double[] pos) {
        if (Math.random() > 0.5) {
            double width = 180;
            board.setPlaying(false);
            SkillCheck skillCheck = new SkillCheck(width,Utils.fracOfRange(100, 400, board.getRevealedFractionNoMines()), Utils.fracOfRange(100, 10, board.getRevealedFractionNoMines()));
            skillCheck.setLayoutX(pos[0] + board.getLayoutX() - width / 2);
            skillCheck.setLayoutY(pos[1] + board.getLayoutY() + 5);
            skillCheck.winProperty().addListener((observable, oldValue, newValue) -> {
                board.setPlaying(true);
                getChildren().remove(skillCheck);
            });
            skillCheck.loseProperty().addListener((observable, oldValue, newValue) -> {
                board.setPlaying(true);
                board.die();
                getChildren().remove(skillCheck);
            });
            skillCheck.start();
            getChildren().add(skillCheck);
        }

    }

    public void die() {
        timer.stop();
    }

    public void win() {
        winView.setVisible(true);
        timer.stop();
    }

    public void reset() {
        t = 0;
        timer.reset();
        timer.stop();
        winView.setVisible(false);

        ArrayList<Node> toRemove = new ArrayList<>(1);
        for (Node child : getChildren()) {
            if (child instanceof SkillCheck) toRemove.add(child);
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
