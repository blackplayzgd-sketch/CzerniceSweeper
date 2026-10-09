package net.xoknox.czernicesweeper.event;

import javafx.event.Event;
import javafx.event.EventType;

public class WinEvent extends Event {
    public static final EventType<WinEvent> WIN =
            new EventType<>(Event.ANY, "WIN");

    public WinEvent() {
        super(WIN);
    }
}
