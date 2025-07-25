package developmentteam.teamrainy.mod.modules.impl.misc;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.api.events.eventbus.EventHandler;
import developmentteam.teamrainy.api.events.impl.DeathEvent;
import developmentteam.teamrainy.mod.modules.Module;
import developmentteam.teamrainy.mod.modules.settings.impl.EnumSetting;
import developmentteam.teamrainy.mod.modules.settings.impl.SliderSetting;
import developmentteam.teamrainy.mod.modules.settings.impl.StringSetting;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Random;

public class AutoEZ extends Module {
    public enum Type {
        Bot,
        Custom,
        AutoSex
    }
    private final EnumSetting<Type> type = add(new EnumSetting<>("Type", Type.Bot));
    private final SliderSetting range =
            add(new SliderSetting("Range", 10, 0, 20,.1));
    private final StringSetting message = add(new StringSetting("Message", "EZ %player% I'm Using TeamRainyHack", () -> type.getValue() == Type.Custom));
    private final SliderSetting randoms =
            add(new SliderSetting("Random", 3, 0, 20,1));
    public AutoEZ() {
        super("AutoEZ", Category.Misc);
        setChinese("自动嘲讽");
    }
    public List<String> sex = List.of("哥哥~你的78有点小哦~",
            "%player%哥哥~你不会以为用这么点大的棒棒就能欺负我了吧~不会吧♡不会吧♡",
            "哥哥的棒棒真是小啊♡嘻嘻~",
            "哎♡~杂鱼就是无趣唉~这就虚脱了",
            "哎呀呀~废柴哥哥会想这种事情啊~唔呃",
            "把你龌蹉的目光拿开啦~很恶心哦♡",
            "咱想看的就是你这样的虚哥哥战败呢~");
                                                                                                                                                                                                         //TeamRainy©
    public List<String> bot = List.of("鼠标明天到，触摸板打的",
            "转人工",
            "收徒",
            "不收徒",
            "有真人吗",
            "墨镜上车",
            "素材局",
            "不接单",
            "接单",
            "征婚",
            "4399?",
            "暂时不考虑打职业",
            "bot?",
            "叫你家大人来打",
            "假肢上门安装",
            "浪费我的网费",
            "不收残疾人",
            "下课",
            "自己找差距",
            "不接代",
            "代+",
            "这样的治好了也流口水",
            "人机",
            "人机怎么调难度啊",
            "只收0 Pop图腾的",
            "Bot吗这是",
            "领养",
            "纳亲",
            "正视差距",
            "近亲繁殖?",
            "我玩的是新手教程",
            "来调灵敏度的",
            "来调参数的",
            "不是本人别加",
            "下次记得晚点玩",
            "扣114514送MoonGod");

    Random random = new Random();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    @EventHandler
    public void onDeath(DeathEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player != mc.player && !TeamRainy.FRIEND.isFriend(player)) {
            if (range.getValue() > 0 && mc.player.distanceTo(player) > range.getValue()) {
                return;
            }
            String randomString = generateRandomString(randoms.getValueInt());
            if (!randomString.isEmpty()) {
                randomString = " " + randomString;
            }
            switch (type.getValue())  {
                case Bot -> mc.getNetworkHandler().sendChatMessage(bot.get(random.nextInt(bot.size() - 1)) + " " + player.getName().getString() + randomString);
                case Custom -> mc.getNetworkHandler().sendChatMessage(message.getValue().replaceAll("%player%", player.getName().getString()) + randomString);
                case AutoSex -> mc.getNetworkHandler().sendChatMessage(sex.get(random.nextInt(sex.size() - 1)) + " " + player.getName().getString() + randomString);
            }
        }
    }

    private String generateRandomString(int LENGTH) {
        StringBuilder sb = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }
}