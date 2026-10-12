package net.xoknox.czernicesweeper.applicationinstallation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AppData {
    private static final Path DIRECTORY;

    static {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            DIRECTORY = Path.of(System.getenv("APPDATA"), "CzerniceSweeper");
        } else {
            DIRECTORY = Path.of(System.getProperty("user.home"), ".local", "share", "CzerniceSweeper");
        }
    }

    private AppData() {}

    public static Path getDirectory() {
        try {
            return Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException("Could not create AppData directory", e);
        }
    }
}
