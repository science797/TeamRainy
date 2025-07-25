package developmentteam.teamrainy.asm.mixins;

import developmentteam.teamrainy.core.impl.GuiManager;
import developmentteam.teamrainy.mod.modules.impl.client.HackSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen {
    @Shadow public int width;
    @Shadow public int height;

    @Inject(method = "renderInGameBackground", at = @At("HEAD"), cancellable = true)
    public void renderInGameBackgroundHook(DrawContext context, CallbackInfo ci) {
        ci.cancel();
        if (HackSetting.INSTANCE.guiBackground.getValue()) {
            context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        }
        if (HackSetting.INSTANCE.customBackground.booleanValue) {
            context.fillGradient(0, 0, this.width, this.height, HackSetting.INSTANCE.customBackground.getValue().getRGB(), HackSetting.INSTANCE.endColor.getValue().getRGB());
        }
        if (HackSetting.INSTANCE.snow.booleanValue) {
            GuiManager.snows.forEach(snow -> snow.drawSnow(context, HackSetting.INSTANCE.snow.getValue()));
        }
    }
}
