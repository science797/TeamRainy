package developmentteam.teamrainy.mod.modules.impl.misc;

import developmentteam.teamrainy.api.events.eventbus.EventHandler;
import developmentteam.teamrainy.api.events.impl.PacketEvent;
import developmentteam.teamrainy.api.utils.entity.InventoryUtil;
import developmentteam.teamrainy.mod.modules.Module;
import developmentteam.teamrainy.mod.modules.settings.impl.BooleanSetting;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

import java.util.HashMap;
//无用功能，后续更新删除 TeamRainy©
public class AutoQueue extends Module {
    public static HashMap<String, String> asks = new HashMap<>(){
        {
            put("红石火把", "我是Hack");
            put("猪被闪电", "我是Hack");
            put("小箱子能", "我是Hack");
            put("开服年份", "我是Hack");
            put("定位末地遗迹", "我是Hack");
            put("爬行者被闪电", "我是Hack");
            put("大箱子能", "我是Hack");
            put("羊驼会主动", "我是Hack");
            put("无限水", "我是Hack");
            put("挖掘速度最快", "我是Hack");
            put("凋灵死后", "我是Hack");
            put("苦力怕的官方", "我是Hack");
            put("南瓜的生长", "我是Hack");
            put("定位末地", "我是Hack");
        }
    };

    public AutoQueue() {
        super("AutoQueue", Category.Misc);
        setChinese("自动答题");
    }
    private final BooleanSetting queueCheck = add(new BooleanSetting("QueueCheck", true));

    public static boolean inQueue = false;
    @Override
    public void onUpdate() {
        if (nullCheck()) {
            inQueue = false;
            return;
        }
        inQueue = InventoryUtil.findItem(Items.COMPASS) != -1;
    }

    @Override
    public void onDisable() {
        inQueue = false;
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive e) {
        if (!inQueue && queueCheck.getValue()) return;
        if (e.getPacket() instanceof GameMessageS2CPacket packet) {
            for (String key : asks.keySet()) {
                if (packet.content().getString().contains(key)) {
                    String[] abc = new String[]{"A", "B", "C"};
                    for (String s : abc) {
                        if (packet.content().getString().contains(s + "." + asks.get(key))) {
                            mc.getNetworkHandler().sendChatMessage(s.toLowerCase());
                            return;
                        }
                    }
                }
            }
        }
    }
}
