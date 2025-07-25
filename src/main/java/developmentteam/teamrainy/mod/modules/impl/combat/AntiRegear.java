package developmentteam.teamrainy.mod.modules.impl.combat;

import developmentteam.teamrainy.mod.modules.impl.player.PacketMine;
import developmentteam.teamrainy.mod.modules.settings.impl.SliderSetting;
import developmentteam.teamrainy.api.utils.world.BlockUtil;
import developmentteam.teamrainy.mod.modules.Module;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.math.MathHelper;

public class AntiRegear extends Module {
    private final SliderSetting safeRange =
            add(new SliderSetting("SafeRange", 2, 0, 8, .1));
    private final SliderSetting range =
            add(new SliderSetting("Range", 5, 0, 8,.1));
    public AntiRegear() {
        super("AntiRegear", Category.Combat);
        setChinese("反补给");
    }

    @Override
    public void onUpdate() {
        if (PacketMine.getBreakPos() != null && mc.world.getBlockState(PacketMine.getBreakPos()).getBlock() instanceof ShulkerBoxBlock) {
            return;
        }
        if (getBlock() != null) {
           PacketMine.INSTANCE.mine(getBlock().getPos());
        }
    }

    private ShulkerBoxBlockEntity getBlock() {
        for (BlockEntity entity : BlockUtil.getTileEntities()) {
            if (entity instanceof ShulkerBoxBlockEntity shulker) {
                if (MathHelper.sqrt((float) mc.player.squaredDistanceTo(shulker.getPos().toCenterPos())) <= safeRange.getValue()) {
                    continue;
                }
                if (MathHelper.sqrt((float) mc.player.squaredDistanceTo(shulker.getPos().toCenterPos())) <= range.getValue()) {
                    return shulker;
                }
            }
        }
        return null;
    }
}