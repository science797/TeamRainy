package developmentteam.teamrainy.mod.modules.impl.misc;

import developmentteam.teamrainy.api.events.eventbus.EventHandler;
import developmentteam.teamrainy.api.events.impl.DurabilityEvent;
import developmentteam.teamrainy.mod.modules.Module;

public class TrueDurability extends Module {

    public TrueDurability() {
        super("DurabilityFix", Category.Misc);
        setChinese("耐久度修正");
    }

    @EventHandler
    public void onDurability(DurabilityEvent event) {
        int dura = event.getItemDamage();
        if (event.getDamage() < 0) {
            dura = event.getDamage();
        }
        event.cancel();
        event.setDamage(dura);
    }
}
