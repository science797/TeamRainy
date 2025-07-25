package developmentteam.teamrainy.api.events.impl;

import developmentteam.teamrainy.api.events.Event;

public class GameLeftEvent extends Event {
    public GameLeftEvent() {
        super(Stage.Post);
    }
}
