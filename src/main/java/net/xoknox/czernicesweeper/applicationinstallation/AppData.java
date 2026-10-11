package net.xoknox.czernicesweeper.applicationinstallation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AppData {
    private static final Path DIRECTORY = Path.of(System.getenv("APPDATA"), "CzerniceSweeper");

    private AppData() {}

    public static Path getDirectory() {
        try {
            return Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException("Could not create AppData directory", e);
        }
    }
}
