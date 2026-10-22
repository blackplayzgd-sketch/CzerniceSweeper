package net.xoknox.czernicesweeper;

import javafx.application.Application;
import net.xoknox.czernicesweeper.applicationinstallation.AppData;
import net.xoknox.czernicesweeper.czerniceaudio.CzerniceAudio;
import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil;
import net.xoknox.czernicesweeper.czerniceaudio.mem.Pointer;
import net.xoknox.czernicesweeper.czerniceaudio.structs.CzerniceEqBandInfo;
import net.xoknox.czernicesweeper.czerniceaudio.structs.CzerniceSoundCreateInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(CzerniceAudio.version());

        Application.launch(CzerniceSweeper.class, args);

    }
}
