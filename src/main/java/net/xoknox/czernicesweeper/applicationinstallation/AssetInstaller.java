package net.xoknox.czernicesweeper.applicationinstallation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AssetInstaller {
    private AssetInstaller() {}

    public static Path installIfMissing(String resPath) throws IOException {
        Path destination = AppData.getDirectory().resolve(resPath);

        Files.createDirectories(destination.getParent());

        if (Files.notExists(destination)) {
            try (InputStream input = AssetInstaller.class.getResourceAsStream("/" + resPath)) {
                if (input == null) {
                    throw new IOException("Missing bundled resource: " + resPath);
                }

                Files.copy(input, destination);
            }
        }

        return destination;
    }

    public static void installDefaultMusic() throws IOException {
        Path musicDir = AppData.getDirectory().resolve("music");
        Path marker = AppData.getDirectory().resolve(".music_installed");

        Files.createDirectories(musicDir);

        if (Files.exists(marker)) return;

        String[] songNames = new String[]{
                "Alex Skrindo, Stahl - Moments [NCS Release].mp3",
                "Cartoon, Coleman Trapp, Jéja - Why We Lose (feat. Coleman Trapp) [NCS Release](1).mp3",
                "Cartoon, Daniel Levi, Jéja - On & On (feat. Daniel Levi) [NCS Release].mp3",
                "Culture Code, KARRA, Tobu - Make Me Move (feat. Karra) (Tobu Remix) [NCS Release].mp3",
                "DEAF KEV - Invincible [NCS Release].mp3",
                "Different Heaven, EH!DE - My Heart [NCS Release].mp3",
                "Dirty Palm, Micah Martin - Oblivion (feat. Micah Martin) [NCS Release].mp3",
                "Disfigure - Blank [NCS Release].mp3",
                "Distrion, Alex Skrindo - Entropy [NCS Release].mp3",
                "Diviners, Philly K. - Savannah (feat. Philly K) [NCS Release].mp3",
                "Electro-Light - Symbolism [NCS Release].mp3",
                "Janji, Johnning - Heroes Tonight (feat. Johnning) [NCS Release].mp3",
                "Jim Yosef - Eclipse [NCS Release].mp3",
                "Jim Yosef - Firefly [NCS Release].mp3",
                "JPB - High [NCS Release].mp3",
                "Kasger - Reflections [NCS Release].mp3",
                "Lensko - Let's Go! [NCS Release].mp3",
                "Robin Hustin, Tobimorrow - Light It Up [NCS Release].mp3",
                "Sub Urban - Cradles [NCS Release].mp3",
                "TULE - Fearless [NCS Release].mp3",
                "Unknown Brain, Bri Tolani - Why Do I_ (feat. Bri Tolani) [NCS Release].mp3",
                "Unknown Brain, Chris Linton - Superhero (feat. Chris Linton) [NCS Release].mp3",
                "Unknown Brain, Marvin Divine - MATAFAKA (feat. Marvin Divine) [NCS Release].mp3"
        };

        for (String song : songNames) {
            installIfMissing("music/" + song);
        }

        Files.createFile(marker);
    }

}
