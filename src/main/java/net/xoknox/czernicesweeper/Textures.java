package net.xoknox.czernicesweeper;

import javafx.scene.image.Image;
import net.xoknox.czernicesweeper.applicationinstallation.AssetInstaller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class Textures {
    private static final HashMap<String, Path> textures = new HashMap<>();
    private static final HashMap<String, String> texturesImage = new HashMap<>();



    public static Image HIDDEN;
    public static Image REVEALED;

    public static Image MINE;
    public static Image FLAG;

    public static Image EMPTY;

    public static Image ONE;
    public static Image TWO;
    public static Image THREE;
    public static Image FOUR;
    public static Image FIVE;
    public static Image SIX;
    public static Image SEVEN;
    public static Image EIGHT;

    public static Image WIN;

    public static Image LOGO;




    public static void init() throws IOException {
        String[] textureFiles = new String[]{
                "hidden", "revealed",
                "flag", "mine",
                "empty",
                "1", "2", "3", "4", "5", "6", "7", "8",
                "win",
                "logo"
        };

        for (String fileName : textureFiles) {
            Path path = AssetInstaller.installIfMissing("textures/" + fileName + ".png");
            textures.put(fileName, path);
            texturesImage.put(fileName, path.toUri().toString());
        }

        HIDDEN = new Image(texturesImage.get("hidden"));
        REVEALED = new Image(texturesImage.get("revealed"));

        MINE = new Image(texturesImage.get("mine"));
        FLAG = new Image(texturesImage.get("flag"));

        EMPTY = new Image(texturesImage.get("empty"));

        ONE = new Image(texturesImage.get("1"));
        TWO = new Image(texturesImage.get("2"));
        THREE = new Image(texturesImage.get("3"));
        FOUR = new Image(texturesImage.get("4"));
        FIVE = new Image(texturesImage.get("5"));
        SIX = new Image(texturesImage.get("6"));
        SEVEN = new Image(texturesImage.get("7"));
        EIGHT = new Image(texturesImage.get("8"));

        WIN = new Image(texturesImage.get("win"));

        LOGO = new Image(texturesImage.get("logo"));
    }


}
