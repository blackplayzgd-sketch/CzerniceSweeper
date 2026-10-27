package net.xoknox.czernicesweeper.event;

import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;

public class MinigameEvent extends Event {
    public static final EventType<MinigameEvent> ANY =
            new EventType<>(Event.ANY, "MINIGAME");

    public static final EventType<MinigameEvent> MINIGAME_ENDED =
            new EventType<>(MinigameEvent.ANY, "MINIGAME_ENDED");

    public static final EventType<MinigameEvent> MINIGAME_LOST =
            new EventType<>(MinigameEvent.ANY, "MINIGAME_LOST");

    public static final EventType<MinigameEvent> MINIGAME_WON =
            new EventType<>(MinigameEvent.ANY, "MINIGAME_WON");


    @Getter
    private Object origin;

    public MinigameEvent(Object source, EventType<MinigameEvent> eventType) {
        super(eventType);
        this.origin = source;
    }


}
