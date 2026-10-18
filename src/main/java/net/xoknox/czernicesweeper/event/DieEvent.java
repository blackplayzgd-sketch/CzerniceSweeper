package net.xoknox.czernicesweeper.event;

import javafx.event.Event;
import javafx.event.EventType;

public class DieEvent  extends Event {
    public static final EventType<DieEvent> DIE =
            new EventType<>(Event.ANY, "DIE");

    public DieEvent() {
        super(DIE);
    }
}
