package net.xoknox.czernicesweeper.event;

import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;

public class BoardInteractedEvent extends Event {
    public static final EventType<BoardInteractedEvent> BOARD_INTERACTED =
            new EventType<>(Event.ANY, "BOARD_INTERACTED");

    @Getter
    private final boolean didRevealTiles;
    @Getter
    private final boolean byPlayer;
    @Getter
    private final boolean isRightClick;
    @Getter
    private final double[] pos;


    public BoardInteractedEvent(boolean isRightClick, boolean byPlayer, boolean didRevealTiles, double[] pos) {
        super(BOARD_INTERACTED);
        this.isRightClick = isRightClick;
        this.byPlayer = byPlayer;
        this.didRevealTiles = didRevealTiles;
        this.pos = pos;
    }
}
