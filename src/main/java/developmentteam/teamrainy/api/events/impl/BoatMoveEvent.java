package developmentteam.teamrainy.api.events.impl;

import developmentteam.teamrainy.api.events.Event;
import net.minecraft.entity.vehicle.BoatEntity;

public class BoatMoveEvent extends Event {

    private final BoatEntity boat;

    public BoatMoveEvent(BoatEntity boat) {
        super(Stage.Pre);
        this.boat = boat;
    }

    public BoatEntity getBoat() {
        return boat;
    }
}

