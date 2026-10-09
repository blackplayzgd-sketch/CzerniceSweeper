package net.xoknox.czernicesweeper.event;

import javafx.event.Event;
import javafx.event.EventType;

public class RemainingMinesChangedEvent extends Event {
    public static final EventType<RemainingMinesChangedEvent> REMAINING_MINES_CHANGED =
            new EventType<>(Event.ANY, "REMAINING_MINES_CHANGED");

    private final int remainingMines;

    public RemainingMinesChangedEvent(int remainingMines) {
        super(REMAINING_MINES_CHANGED);
        this.remainingMines = remainingMines;
    }

    public int getRemainingMines() {
        return remainingMines;
    }
}
