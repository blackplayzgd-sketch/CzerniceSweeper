package net.xoknox.czernicesweeper;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class GameSettings {
    private final Property<Units> unit = new SimpleObjectProperty<>(Units.SECOND);


    @Getter @Setter private Stage stage;
    @Getter @Setter private Scene gameScene;
    @Getter @Setter private Scene settingsScene;
    @Getter @Setter private Settings settings;
    @Getter @Setter private Game game;

    public Property<Units> unitProperty() {
        return unit;
    }

    public void setUnit(Units unit) {
        this.unit.setValue(unit);
    }

    public Units getUnit() {
        return this.unit.getValue();
    }

    public GameSettings() {}
}
