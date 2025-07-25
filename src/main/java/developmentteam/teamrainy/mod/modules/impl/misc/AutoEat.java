package developmentteam.teamrainy.mod.modules.impl.misc;

import developmentteam.teamrainy.api.utils.entity.EntityUtil;
import developmentteam.teamrainy.api.utils.entity.InventoryUtil;
import developmentteam.teamrainy.mod.modules.Module;
import developmentteam.teamrainy.mod.modules.settings.impl.SliderSetting;
import net.minecraft.item.Items;


public class AutoEat extends Module {


    public static AutoEat INSTANCE;
    private final SliderSetting hunger =
            add(new SliderSetting("Hunger", 10, 0, 19, 1));
    private final SliderSetting health =
            add(new SliderSetting("Health", 20, 0, 35.9, .1));


    public AutoEat() {
        super("AutoEat", Category.Misc);
        setChinese("自动进食");
        INSTANCE = this;
    }

    boolean eat = false;
    @Override
    public void onUpdate() {
        if (EntityUtil.getHealth(mc.player) <= health.getValueFloat() || mc.player.getHungerManager().getFoodLevel() <= hunger.getValueFloat()) {
            if (InventoryUtil.findItem(Items.ENCHANTED_GOLDEN_APPLE) != -1) {
                InventoryUtil.switchToSlot(InventoryUtil.findItem(Items.ENCHANTED_GOLDEN_APPLE));
                mc.options.useKey.setPressed(true);
                eat = true;
            } else if (InventoryUtil.findItem(Items.GOLDEN_APPLE) != -1) {
                InventoryUtil.switchToSlot(InventoryUtil.findItem(Items.GOLDEN_APPLE));
                mc.options.useKey.setPressed(true);
                eat = true;
            }
        } else if (eat) {
            mc.options.useKey.setPressed(false);
            eat = false;
        }
    }
}
