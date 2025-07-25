package developmentteam.teamrainy.mod.modules.impl.player;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.api.utils.entity.InventoryUtil;
import developmentteam.teamrainy.mod.modules.Module;
import developmentteam.teamrainy.mod.modules.settings.impl.BooleanSetting;
import developmentteam.teamrainy.mod.modules.settings.impl.SliderSetting;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

public class AutoTrade extends Module {
    public static AutoTrade INSTANCE;
    public AutoTrade() {
        super("AutoTrade", Category.Player);
        setChinese("自动村民交易");
        INSTANCE = this;
    }

    public final SliderSetting repeat = add(new SliderSetting("Repeat", 2, 1, 15, 1));
    public final BooleanSetting autoClose = add(new BooleanSetting("AutoClose", true));
    @Override
    public void onUpdate() {
        if (mc.player.currentScreenHandler instanceof MerchantScreenHandler handler) {
            int i = 0;
            boolean flag = true;

                TradeOfferList list = handler.getRecipes();
                for (int size = 0; size < list.size(); ++size) {
                    if (i >= repeat.getValue()) return;
                    TradeOffer tradeOffer = list.get(size);
                    if (!tradeOffer.isDisabled()) {
                        if (TeamRainy.TRADE.inWhitelist(tradeOffer.getSellItem().getItem().getTranslationKey())) {
                            while (i < repeat.getValue() && flag) {
                                flag = false;
                                if (!tradeOffer.getAdjustedFirstBuyItem().isEmpty()) {
                                    int count = InventoryUtil.getItemCount(tradeOffer.getAdjustedFirstBuyItem().getItem());
                                    if (handler.getSlot(0).getStack().getItem() == tradeOffer.getAdjustedFirstBuyItem().getItem()) {
                                        count += handler.getSlot(0).getStack().getCount();
                                    }
                                    if (count < tradeOffer.getAdjustedFirstBuyItem().getCount()) {
                                        continue;
                                    }
                                }
                                if (!tradeOffer.getSecondBuyItem().isEmpty()) {
                                    int count = InventoryUtil.getItemCount(tradeOffer.getSecondBuyItem().getItem());
                                    if (handler.getSlot(1).getStack().getItem() == tradeOffer.getSecondBuyItem().getItem()) {
                                        count += handler.getSlot(1).getStack().getCount();
                                    }
                                    if (count < tradeOffer.getSecondBuyItem().getCount()) {
                                        continue;
                                    }
                                }
                                mc.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(size));
                                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 2, 1, SlotActionType.QUICK_MOVE, mc.player);
                                flag = true;
                                i++;
                            }
                        }
                    }
                }
            if (autoClose.getValue() && i < repeat.getValue()) {
                mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
                mc.currentScreen.close();
            }
        }
    }
}
