package developmentteam.teamrainy.asm.mixins;

import developmentteam.teamrainy.mod.modules.Module;
import developmentteam.teamrainy.mod.modules.impl.render.Ambience;
import developmentteam.teamrainy.mod.modules.impl.render.XRay;
import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class MixinAbstractBlockState {
    @Inject(method = "getLuminance", at = @At("HEAD"), cancellable = true)
    public void getTeamRainyHook(CallbackInfoReturnable<Integer> cir) {
        if (Module.nullCheck()) return;
        if (XRay.INSTANCE.isOn()) {
            cir.setReturnValue(15);
        } else if (Ambience.INSTANCE.customTeamRainy.getValue()) {
            cir.setReturnValue(Ambience.INSTANCE.teamrainy.getValueInt());
        }
    }
}