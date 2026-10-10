package net.xoknox.czernicesweeper;

public enum Units {
    SECOND(1, "s", "Second"),
    HOUR(1 / 3600d, "h", "Hour"),
    YOCTOSECOND(1000000000000000000000000d, "ys", "Yoctosecond"),

    PER_C( 261.63d, "C⁻¹", "per C"),
    PER_C_FLAT( 246.94d, "C♭⁻¹", "per C♭"),
    PER_C_SHARP( 277.18d, "C♯⁻¹", "per C♯"),

    PER_D( 293.66d, "D⁻¹", "per D"),
    PER_D_FLAT( 277.18d, "D♭⁻¹", "per D♭"),
    PER_D_SHARP( 311.13d, "D♯⁻¹", "per D♯"),

    PER_E( 329.63d, "E⁻¹", "per E"),
    PER_E_FLAT( 311.13d, "E♭⁻¹", "per E♭"),
    PER_E_SHARP( 349.23d, "E♯⁻¹", "per E♯"),

    PER_F( 349.23d, "F⁻¹", "per F"),
    PER_F_FLAT( 329.63d, "F♭⁻¹", "per F♭"),
    PER_F_SHARP( 369.99d, "F♯⁻¹", "per F♯"),

    PER_G( 392.00d, "G⁻¹", "per G"),
    PER_G_FLAT( 369.99d, "G♭⁻¹", "per G♭"),
    PER_G_SHARP( 415.30d, "G♯⁻¹", "per G♯"),

    PER_A( 440.00d, "A⁻¹", "per A"),
    PER_A_FLAT( 415.30d, "A♭⁻¹", "per A♭"),
    PER_A_SHARP( 466.16d, "A♯⁻¹", "per A♯"),

    PER_B( 493.88d, "B⁻¹", "per B"),
    PER_B_FLAT( 466.16d, "B♭⁻¹", "per B♭"),
    PER_B_SHARP( 523.25d, "B♯⁻¹", "per B♯");

    private double t;
    private String symbol;
    private String displayName;

    Units(double t, String symbol, String displayName) {
        this.t = t;
        this.symbol = symbol;
        this.displayName = displayName;
    }

    public double t() {
        return t;
    }

    public String symbol() {
        return symbol;
    }
    public String displayName() {
        return displayName;
    }
}
