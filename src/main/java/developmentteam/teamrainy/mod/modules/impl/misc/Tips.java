package developmentteam.teamrainy.mod.modules.impl.misc;

import developmentteam.teamrainy.mod.modules.settings.impl.BooleanSetting;
import developmentteam.teamrainy.mod.modules.settings.impl.SliderSetting;
import developmentteam.teamrainy.api.events.eventbus.EventHandler;
import developmentteam.teamrainy.api.events.impl.DeathEvent;
import developmentteam.teamrainy.api.events.impl.PacketEvent;
import developmentteam.teamrainy.core.impl.CommandManager;
import developmentteam.teamrainy.api.utils.entity.InventoryUtil;
import developmentteam.teamrainy.api.utils.math.Timer;
import developmentteam.teamrainy.mod.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import java.awt.*;
import java.text.DecimalFormat;

public class Tips extends Module {
    public static Tips INSTANCE;
    public Tips() {
        super("Tips", Category.Misc);
        setChinese("提示");
        INSTANCE = this;
    }
    public final BooleanSetting deathCoords =
            add(new BooleanSetting("DeathCoords", true));
    public final BooleanSetting serverLag =
            add(new BooleanSetting("ServerLag", true));
    public final BooleanSetting lagBack =
            add(new BooleanSetting("LagBack", true));
    public final BooleanSetting turtle =
            add(new BooleanSetting("Turtle", true).setParent());
    private final SliderSetting yOffset = add(new SliderSetting("YOffset", 0, -200, 200, () -> turtle.isOpen()));
    public final BooleanSetting shulkerViewer =
            add(new BooleanSetting("ShulkerViewer", true));

    int turtles = 0;

    @Override
    public void onUpdate() {
        if (turtle.getValue()) {
            turtles = InventoryUtil.getPotionCount(StatusEffects.RESISTANCE);
        }
    }
    private final Timer lagTimer = new Timer();
    private final Timer lagBackTimer = new Timer();
    @EventHandler
    public void onPacketEvent(PacketEvent.Receive event) {
        lagTimer.reset();
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            lagBackTimer.reset();
        }
    }
    DecimalFormat df = new DecimalFormat("0.0");
    int color = new Color(190, 0, 0).getRGB();
    @Override
    public void onRender2D(DrawContext drawContext, float tickDelta) {
        if (serverLag.getValue() && lagTimer.passedS(1.4)) {
            String text = "Server not responding (" + df.format(lagTimer.getPassedTimeMs() / 1000d) + "s)";
            drawContext.drawText(mc.textRenderer, text, mc.getWindow().getScaledWidth() / 2 - mc.textRenderer.getWidth(text) / 2, 10 + mc.textRenderer.fontHeight, color, true);
        }
        if (lagBack.getValue() && !lagBackTimer.passedS(1.5)) {
            String text = "Lagback (" + df.format((1500 - lagBackTimer.getPassedTimeMs()) / 1000d) + "s)";
            drawContext.drawText(mc.textRenderer, text, mc.getWindow().getScaledWidth() / 2 - mc.textRenderer.getWidth(text) / 2, 10 + mc.textRenderer.fontHeight * 2, color, true);
        }
        if (turtle.getValue()) {
            String text = "§e" + turtles;
            if (mc.player.hasStatusEffect(StatusEffects.RESISTANCE) && mc.player.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() >= 2) {
                text += " §f" + (mc.player.getStatusEffect(StatusEffects.RESISTANCE).getDuration() / 20 + 1);
            }
            drawContext.drawText(mc.textRenderer, text, mc.getWindow().getScaledWidth() / 2 - mc.textRenderer.getWidth(text) / 2, mc.getWindow().getScaledHeight() / 2 + mc.textRenderer.fontHeight - yOffset.getValueInt(), -1, true);
        }
    }

    @EventHandler
    public void onPlayerDeath(DeathEvent event) {
        PlayerEntity player = event.getPlayer();
        if (deathCoords.getValue() && player == mc.player) {
            CommandManager.sendChatMessage("§4You died at " + (int) player.getX() + ", " + (int) player.getY() + ", " + (int) player.getZ());
        }
    }


}
