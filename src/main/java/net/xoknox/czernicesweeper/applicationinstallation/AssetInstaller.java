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
                "test.mp3",
                "Cartoon, Coleman Trapp, Jéja - Why We Lose (feat. Coleman Trapp) [NCS Release](1).mp3",
                "Cartoon, Daniel Levi, Jéja - On & On (feat. Daniel Levi) [NCS Release].mp3"
        };

        for (String song : songNames) {
            installIfMissing("music/" + song);
        }

        Files.createFile(marker);
    }

}
