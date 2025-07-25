package developmentteam.teamrainy.api.events.impl;

import developmentteam.teamrainy.api.events.Event;

public class EntityVelocityUpdateEvent extends Event {
    public EntityVelocityUpdateEvent() {
        super(Stage.Pre);
    }
}
