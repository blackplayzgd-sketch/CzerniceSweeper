package net.xoknox.czernicesweeper;

import net.xoknox.czernicesweeper.czerniceaudio.CzerniceAudio;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        int version = CzerniceAudio.nversion();
        System.out.println(version);

        //Application.launch(CzerniceSweeper.class, args);
    }
}
