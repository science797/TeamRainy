package developmentteam.teamrainy.core.impl;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.api.events.eventbus.EventHandler;
import developmentteam.teamrainy.api.events.impl.PacketEvent;
import developmentteam.teamrainy.api.utils.Wrapper;
import developmentteam.teamrainy.api.utils.math.MathUtil;
import developmentteam.teamrainy.api.utils.render.JelloUtil;
import developmentteam.teamrainy.mod.modules.impl.client.AntiCheat;
import developmentteam.teamrainy.mod.modules.impl.client.FontSetting;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;

public class ServerManager implements Wrapper {

    public ServerManager() {
        TeamRainy.EVENT_BUS.subscribe(this);
    }
    public int serverSideSlot;

    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket packet) {
            int packetSlot = packet.getSelectedSlot();
            if (AntiCheat.INSTANCE.noBadSlot.getValue()) {
                if (packetSlot == serverSideSlot) {
                    event.cancel();
                    return;
                }
            }
            serverSideSlot = packetSlot;
        }
    }
    private final ArrayDeque<Float> tpsResult = new ArrayDeque<>(20);
    private long time;
    private long tickTime;
    private float tps;

    public float getTPS() {
        return round2(tps);
    }

    public float getCurrentTPS() {
        return round2(20.0f * ((float) tickTime / 1000f));
    }

    public float getTPSFactor() {
        return getTPS() / 20f;
    }

    public static float round2(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            if (time != 0L) {
                tickTime = System.currentTimeMillis() - time;

                if (tpsResult.size() > 20)
                    tpsResult.poll();

                tpsResult.add(20.0f * (1000.0f / (float) (tickTime)));

                float average = 0.0f;

                for (Float value : tpsResult) average += MathUtil.clamp(value, 0f, 20f);

                tps = average / (float) tpsResult.size();
            }
            time = System.currentTimeMillis();
        }
        if (event.getPacket() instanceof UpdateSelectedSlotS2CPacket packet) {
            serverSideSlot = packet.getSlot();
        }
    }

    boolean worldNull = true;

    public void onUpdate() {
        JelloUtil.updateJello();
        if (worldNull && mc.world != null) {
            FontSetting.INSTANCE.enable();
            TeamRainy.MODULE.onLogin();
            worldNull = false;
        } else if (!worldNull && mc.world == null) {
            TeamRainy.save();
            TeamRainy.MODULE.onLogout();
            worldNull = true;
        }
    }
}
