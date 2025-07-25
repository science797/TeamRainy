package developmentteam.teamrainy.mod.modules.impl.client;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.api.events.eventbus.EventHandler;
import developmentteam.teamrainy.api.events.impl.PacketEvent;
import developmentteam.teamrainy.api.utils.math.Timer;
import developmentteam.teamrainy.mod.modules.Module;
import developmentteam.teamrainy.mod.modules.settings.impl.BooleanSetting;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

public class ServerApply extends Module {
    public static ServerApply INSTANCE;
    public final BooleanSetting rotate = add(new BooleanSetting("Rotate", true));
    public final BooleanSetting applyYaw = add(new BooleanSetting("ApplyYaw", true, () -> !rotate.getValue()));
    public final BooleanSetting slot = add(new BooleanSetting("Slot", true));
    public final BooleanSetting pack = add(new BooleanSetting("ResourcePack", true));
    public ServerApply() {
        super("ServerApply", Category.Client);
        setChinese("服务器应用");
        TeamRainy.EVENT_BUS.subscribe(this);
        INSTANCE = this;
    }

    boolean send = false;

    private final Timer applyTimer = new Timer();
    @Override
    public void onLogin() {
        applyTimer.reset();
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!pack.getValue() && event.getPacket() instanceof ResourcePackSendS2CPacket) {
            send = true;
            event.cancel();
        }
        if (!applyTimer.passed(1000)) return;
        if (nullCheck()) return;
        if (!slot.getValue() && event.getPacket() instanceof UpdateSelectedSlotS2CPacket packet) {
            event.setCancelled(true);
            if (packet.getSlot() != mc.player.getInventory().selectedSlot) {
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
            }
        }
    }

    @Override
    public void onUpdate() {
        if (send && mc.player != null) {
            mc.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(mc.player.getUuid(), ResourcePackStatusC2SPacket.Status.ACCEPTED));
            mc.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(mc.player.getUuid(), ResourcePackStatusC2SPacket.Status.DOWNLOADED));
            mc.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(mc.player.getUuid(), ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
            send = false;
        }
    }

    @Override
    public void enable() {
        this.state = true;
    }

    @Override
    public void disable() {
        this.state = true;
    }

    @Override
    public boolean isOn() {
        return true;
    }
}
