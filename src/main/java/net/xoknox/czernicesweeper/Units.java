package net.xoknox.czernicesweeper;

public enum Units {
    SECOND(1, "s", "Second"),
    HOUR(1 / 3600d, "h", "Hour"),
    YOCTOSECOND(1000000000000000000000000d, "ys", "Yoctosecond"),

    ROOT_BEARD_FORTNIGHT_CUBED_ELEPHANT_PER_G(1 / 4713.7229484d, "√(Bfn³*El*G⁻¹)", "Root Beard Fortnight Cubed Elephant per G"),
    R_CUBIC_METRE_OXYGENS_RUTHERFORDIUM_MELTING_POINT_PER_MASS_CUBIC_MICROMETRE_POLYTETRAFLUOROETHYLENE_PER_CENTRIPETAL_ACCELERATION_KEPLER_22B_PER_KILOKNOT(1 / 988555820150000000d, "RMRᵤP⁻¹A⁻¹K⁻¹", "R Cubic Metre Oxygens Rutherfordium Melting Point per Mass Polytetrafluoroethylene Cubic Micrometre per Kepler 22-b Centripetal Acceleration per Kiloknot"),

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
