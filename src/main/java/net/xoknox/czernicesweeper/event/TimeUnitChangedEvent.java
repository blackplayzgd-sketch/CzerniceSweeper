package net.xoknox.czernicesweeper.event;

import javafx.event.Event;
import javafx.event.EventType;
import net.xoknox.czernicesweeper.Units;

public class TimeUnitChangedEvent extends Event {
    public static final EventType<TimeUnitChangedEvent> TIME_UNIT_CHANGED =
            new EventType<>(Event.ANY, "TIME_UNIT_CHANGED");

    private final Units unit;

    public TimeUnitChangedEvent(Units unit) {
        super(TIME_UNIT_CHANGED);
        this.unit = unit;
    }

    public Units getUnit() {
        return unit;
    }
}
