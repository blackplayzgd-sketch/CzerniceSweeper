package net.xoknox.czernicesweeper.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import lombok.Getter;
import lombok.Setter;
import net.xoknox.czernicesweeper.Textures;
import net.xoknox.czernicesweeper.Utils;

import java.util.ArrayList;

public class CzerniceControl<T extends Control> extends StackPane implements ICzernicable {
    public static final String DEF_FONT = "impact";
    public T main;
    private ImageView bg = new ImageView();

    private static final ArrayList<CzerniceControl<?>> czernice = new ArrayList<>();
    private static boolean supernovaMode = false;


    public CzerniceControl(T main) {
        this.main = main;

        if (main instanceof Labeled labeled) {
            labeled.setFont(Font.font(DEF_FONT, labeled.getFont().getSize()));
        };
        bg.setPreserveRatio(false);
        bg.setImage(supernovaMode ? Textures.BUTTON_RED_SUPERNOVA : Textures.BUTTON_RED);

        main.setPrefSize(100, 50);
        bg.setFitWidth(100);
        bg.setFitHeight(50);
        setCoolBG();

        main.setBackground(Background.EMPTY);
        getChildren().addAll(bg, main);

        czernice.add(this);
    }

    @Override
    public void setPrefSize(double prefWidth, double prefHeight) {
        super.setPrefSize(prefWidth, prefHeight);
        main.setPrefSize(prefWidth, prefHeight);
        bg.setFitWidth(prefWidth);
        bg.setFitHeight(prefHeight);

    }

    @Override
    public void setCoolBG() {
        bg.setEffect(Utils.randomColorAdjust());
    }

    public static void toggleSupernovaMode() {
        supernovaMode = !supernovaMode;
        for (CzerniceControl<?> cc : czernice) {
            cc.bg.setImage(supernovaMode ? Textures.BUTTON_RED_SUPERNOVA : Textures.BUTTON_RED);
        }
    }
}
