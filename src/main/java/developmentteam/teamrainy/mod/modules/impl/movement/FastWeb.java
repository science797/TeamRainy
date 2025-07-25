package developmentteam.teamrainy.mod.modules.impl.movement;

import developmentteam.teamrainy.api.events.eventbus.EventHandler;
import developmentteam.teamrainy.api.events.eventbus.EventPriority;
import developmentteam.teamrainy.api.events.impl.TimerEvent;
import developmentteam.teamrainy.api.utils.entity.MovementUtil;
import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.mod.modules.Module;
import developmentteam.teamrainy.mod.modules.settings.impl.BooleanSetting;
import developmentteam.teamrainy.mod.modules.settings.impl.EnumSetting;
import developmentteam.teamrainy.mod.modules.settings.impl.SliderSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;


public class FastWeb extends Module {
    public static FastWeb INSTANCE;
    public final EnumSetting<Mode> mode =
            add(new EnumSetting<>("Mode", Mode.Vanilla));
    private final SliderSetting fastSpeed =
            add(new SliderSetting("Speed", 3.0, 0.0, 8.0, () -> mode.getValue() == Mode.Vanilla || mode.getValue() == Mode.Strict));
    public final SliderSetting xZSlow =
            add(new SliderSetting("XZSpeed", 25, 0.0, 100, 0.1, () -> mode.getValue() == Mode.Custom).setSuffix("%"));
    public final SliderSetting ySlow =
            add(new SliderSetting("YSpeed", 100, 0.0, 100, 0.1, () -> mode.getValue() == Mode.Custom).setSuffix("%"));
    public final BooleanSetting onlySneak = add(new BooleanSetting("OnlySneak", true));
    public final BooleanSetting grim = add(new BooleanSetting("Grim", false));
    public FastWeb() {
        super("AntiWeb", "So you don't need to keep timer on keybind", Category.Movement);
        setChinese("防蜘蛛网");
        INSTANCE = this;
    }

    @Override
    public String getInfo() {
        return mode.getValue().name();
    }

    public boolean isWorking() {
        return work;
    }
    private boolean work = false;
    @Override
    public void onUpdate() {
        work = (!mc.player.isOnGround()) && (mc.options.sneakKey.isPressed() || !onlySneak.getValue()) && TeamRainy.PLAYER.isInWeb(mc.player);
        if (work) {
            MovementUtil.setMotionY(MovementUtil.getMotionY() - fastSpeed.getValue());
        }
        if (grim.getValue() && (mc.options.sneakKey.isPressed() || !onlySneak.getValue())) {
            for (BlockPos pos : getIntersectingWebs()) {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, pos, Direction.DOWN));
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.DOWN));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onTimer(TimerEvent event) {
        if (work && mode.getValue() == Mode.Strict) {
            event.set(fastSpeed.getValueFloat());
        }
    }

    public List<BlockPos> getIntersectingWebs() {
        int radius = 2;
        final List<BlockPos> blocks = new ArrayList<>();
        for (int x = radius; x > -radius; --x) {
            for (int y = radius; y > -radius; --y) {
                for (int z = radius; z > -radius; --z) {
                    BlockPos blockPos = BlockPos.ofFloored(mc.player.getX() + x,
                            mc.player.getY() + y, mc.player.getZ() + z);
                    BlockState state = mc.world.getBlockState(blockPos);
                    if (state.getBlock() instanceof CobwebBlock) {
                        blocks.add(blockPos);
                    }
                }
            }
        }
        return blocks;
    }

    public enum Mode {
        Vanilla,
        Strict,
        Custom,
        Ignore
    }
}
