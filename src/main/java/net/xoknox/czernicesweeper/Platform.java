package net.xoknox.czernicesweeper;

import lombok.Getter;

public enum Platform {

    LINUX("Linux"),
    WINDOWS("Windows");

    private static final Platform current;

    static {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            current = WINDOWS;
        } else if (osName.startsWith("Linux") || osName.startsWith("SunOS") || osName.startsWith("Unix")) {
            current = LINUX;
        } else {
            throw new IllegalStateException("Unknown platform, if youre not on linux or windows your out of luck");
        }
    }

    @Getter
    private final String name;

    Platform(String name) {
        this.name = name;
    }

    public static Platform get() {
        return current;
    }

}
