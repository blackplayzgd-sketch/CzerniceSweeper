package net.xoknox.czernicesweeper;

import javafx.application.Application;
import net.xoknox.czernicesweeper.czerniceaudio.CzerniceAudio;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(CzerniceAudio.version());

        Application.launch(CzerniceSweeper.class, args);

    }
}
