package developmentteam.teamrainy.core.impl;

import developmentteam.teamrainy.api.utils.world.BlockUtil;
import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.api.events.eventbus.EventHandler;
import developmentteam.teamrainy.api.events.eventbus.EventPriority;
import developmentteam.teamrainy.api.events.impl.TickEvent;
import developmentteam.teamrainy.mod.modules.impl.render.PlaceRender;

public class ThreadManager {
    public static ClientService clientService;

    public ThreadManager() {
        TeamRainy.EVENT_BUS.subscribe(this);
        clientService = new ClientService();
        clientService.setName("TeamRainyHackService");
        clientService.setDaemon(true);
        clientService.start();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEvent(TickEvent event) {
        if (event.isPre()) {
            if (!clientService.isAlive()) {
                clientService = new ClientService();
                clientService.setName("TeamRainyHackService");
                clientService.setDaemon(true);
                clientService.start();
            }
            BlockUtil.placedPos.forEach(pos -> PlaceRender.renderMap.put(pos, PlaceRender.INSTANCE.create(pos)));
            BlockUtil.placedPos.clear();
            TeamRainy.SERVER.onUpdate();
            TeamRainy.PLAYER.onUpdate();
            TeamRainy.MODULE.onUpdate();
            TeamRainy.GUI.onUpdate();
            TeamRainy.POP.onUpdate();
        }
    }

    public static class ClientService extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (TeamRainy.MODULE != null) {
                        TeamRainy.MODULE.onThread();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
