package net.xoknox.czernicesweeper;

import javafx.beans.property.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import net.xoknox.czernicesweeper.czerniceaudio.BetterBackgroundMusicPlayer;

public class GameSettings {
    private final Property<Units> unit = new SimpleObjectProperty<>(Units.SECOND);
    private final IntegerProperty width = new SimpleIntegerProperty(30);
    private final IntegerProperty height = new SimpleIntegerProperty(20);
    private final IntegerProperty mineCount = new SimpleIntegerProperty(110);
    private final DoubleProperty musicVolume = new SimpleDoubleProperty(1);
    private final DoubleProperty musicRate = new SimpleDoubleProperty(1);
    private final DoubleProperty musicBalance = new SimpleDoubleProperty(0);
    private final StringProperty currSong = new SimpleStringProperty("Nothing");
    private final DoubleProperty lowFreqGain = new SimpleDoubleProperty(0);
    private final DoubleProperty midFreqGain = new SimpleDoubleProperty(0);
    private final DoubleProperty highFreqGain = new SimpleDoubleProperty(0);
    private final DoubleProperty bitDepth = new SimpleDoubleProperty(24d);
    private final DoubleProperty wet = new SimpleDoubleProperty(0);
    private final DoubleProperty dry = new SimpleDoubleProperty(1d);
    private final DoubleProperty feedback = new SimpleDoubleProperty(0);
    private final DoubleProperty rateDivisor = new SimpleDoubleProperty(1d);





    @Getter @Setter private Stage stage;
    @Getter @Setter private Scene gameScene;
    @Getter @Setter private Scene settingsScene;
    @Getter @Setter private Settings settings;
    @Getter @Setter private Game game;
    @Getter @Setter private BetterBackgroundMusicPlayer bgMusic;

    public Property<Units> unitProperty() {
        return unit;
    }
    public IntegerProperty widthProperty() {
        return width;
    }
    public IntegerProperty heightProperty() {
        return height;
    }
    public IntegerProperty mineCountProperty() {
        return mineCount;
    }
    public DoubleProperty musicVolumeProperty() {
        return musicVolume;
    }
    public DoubleProperty musicRateProperty() {
        return musicRate;
    }
    public DoubleProperty musicBalanceProperty() {
        return musicBalance;
    }
    public StringProperty currSongProperty() {
        return currSong;
    }
    public DoubleProperty lowFreqGainProperty() {
        return lowFreqGain;
    }
    public DoubleProperty midFreqGainProperty() {
        return midFreqGain;
    }
    public DoubleProperty highFreqGainProperty() {
        return highFreqGain;
    }
    public DoubleProperty bitDepthProperty() {
        return bitDepth;
    }
    public DoubleProperty wetProperty() {
        return wet;
    }
    public DoubleProperty dryProperty() {
        return dry;
    }
    public DoubleProperty feedbackProperty() {
        return feedback;
    }
    public DoubleProperty rateDivisorProperty() {
        return rateDivisor;
    }

    public void setUnit(Units unit) {
        this.unit.setValue(unit);
    }
    public void setWidth(int width) {
        this.width.setValue(width);
    }
    public void setHeight(int height) {
        this.height.setValue(height);
    }
    public void setMineCount(int count) {
        this.mineCount.setValue(count);
    }
    public void setMusicVolume(double volume) {
        this.musicVolume.setValue(volume);
    }
    public void setMusicRate(double volume) {
        this.musicRate.setValue(volume);
    }
    public void setMusicBalance(double balance) {
        this.musicBalance.setValue(balance);
    }
    public void setLowFreqGain(double gain) {
        this.lowFreqGain.setValue(gain);
    }
    public void setMidFreqGain(double gain) {
        this.midFreqGain.setValue(gain);
    }
    public void setHighFreqGain(double gain) {
        this.highFreqGain.setValue(gain);
    }
    public void setBitDepth(double gain) {
        this.bitDepth.setValue(gain);
    }
    public void setWet(double gain) {
        this.wet.setValue(gain);
    }
    public void setDry(double gain) {
        this.dry.setValue(gain);
    }
    public void setFeedback(double gain) {
        this.feedback.setValue(gain);
    }
    public void setRateDivisor(double gain) {
        this.rateDivisor.setValue(gain);
    }






    public Units getUnit() {
        return this.unit.getValue();
    }

    public GameSettings() {}
}
