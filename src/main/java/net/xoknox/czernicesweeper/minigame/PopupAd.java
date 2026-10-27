package net.xoknox.czernicesweeper.minigame;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import net.xoknox.czernicesweeper.Textures;
import net.xoknox.czernicesweeper.event.MinigameEvent;
import net.xoknox.czernicesweeper.ui.CzerniceControl;
import net.xoknox.czernicesweeper.util.Utils;

public class PopupAd extends Group implements IMinigameable {
    private final Image image;
    private final CzerniceControl<Button> xButton;
    private final double xSize;

    public PopupAd(double xSize) {
        this.image = getRandomAd();
        this.xSize = xSize;
        ImageView mainImg = new ImageView(image);

        xButton = new CzerniceControl<>(new Button("X"));

        setButtonInRandomCorner();
        xButton.setPrefSize(xSize, xSize);

        xButton.main.setOnMouseClicked(e -> {
            System.out.println("i clicka de buton");
            fireEvent(new MinigameEvent(PopupAd.this, MinigameEvent.MINIGAME_ENDED));
        });

        getChildren().addAll(mainImg, xButton);
    }


    private Image getRandomAd() {
        return Textures.AD_TEXTURES.get(Utils.randIntRange(0, Textures.AD_TEXTURES.size()));
    }

    private void setButtonInRandomCorner() {
        int x = Utils.randIntRange(0, 2);
        int y = Utils.randIntRange(0, 2);

        double bx = x * (image.getWidth() - xSize);
        double by = y * (image.getHeight() - xSize);

        xButton.setLayoutX(bx);
        xButton.setLayoutY(by);
    }

    public double[] getSize() {
        return new double[]{image.getWidth(), image.getHeight()};
    }
}
