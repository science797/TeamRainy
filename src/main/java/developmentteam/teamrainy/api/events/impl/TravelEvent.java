package developmentteam.teamrainy.api.events.impl;

import developmentteam.teamrainy.api.events.Event;
import net.minecraft.entity.player.PlayerEntity;

public class TravelEvent extends Event {

    private final PlayerEntity entity;


    public TravelEvent(Stage stage, PlayerEntity entity) {
        super(stage);
        this.entity = entity;
    }

    public PlayerEntity getEntity() {
        return entity;
    }
}