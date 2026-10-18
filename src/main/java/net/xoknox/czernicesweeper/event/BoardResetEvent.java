package net.xoknox.czernicesweeper.event;

import javafx.event.Event;
import javafx.event.EventType;

public class BoardResetEvent extends Event {
    public static final EventType<BoardResetEvent> BOARD_RESET =
            new EventType<>(Event.ANY, "BOARD_RESET");

    public BoardResetEvent() {
        super(BOARD_RESET);
    }
}
