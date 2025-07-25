package developmentteam.teamrainy.mod.modules.impl.misc;

import developmentteam.teamrainy.api.utils.world.BlockUtil;
import developmentteam.teamrainy.mod.modules.Module;
import developmentteam.teamrainy.mod.modules.impl.player.PacketMine;
import developmentteam.teamrainy.mod.modules.settings.impl.BooleanSetting;
import developmentteam.teamrainy.mod.modules.settings.impl.SliderSetting;
import net.minecraft.util.math.BlockPos;

public class Nuker extends Module {
    private final SliderSetting range =
            add(new SliderSetting("Range", 4, 0, 8,.1));
    private final BooleanSetting down =
            add(new BooleanSetting("Down",false));
    public Nuker() {
        super("Nuker", Category.Misc);
        setChinese("范围挖掘");
    }

    @Override
    public void onUpdate() {
        if (PacketMine.getBreakPos() != null && !mc.world.isAir(PacketMine.getBreakPos())) {
            return;
        }
        BlockPos pos = getBlock();
        if (pos != null) {
           PacketMine.INSTANCE.mine(pos);
        }
    }

    private BlockPos getBlock() {
        BlockPos down = null;
        for (BlockPos pos : BlockUtil.getSphere(range.getValueFloat(), mc.player.getPos())) {
            if (mc.world.isAir(pos)) continue;
            if (PacketMine.godBlocks.contains(mc.world.getBlockState(pos).getBlock())) continue;
            if (BlockUtil.getClickSideStrict(pos) == null) continue;
            if (pos.getY() < mc.player.getY()) {
                if (down == null && this.down.getValue()) {
                    down = pos;
                }
                continue;
            }
            return pos;
        }
        return down;
    }
}