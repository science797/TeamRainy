package developmentteam.teamrainy.asm.mixins;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.api.events.impl.DurabilityEvent;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow
    public abstract int getDamage();

    @Shadow
    public abstract NbtCompound getOrCreateNbt();


    @Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;I)V", at = @At(
            value = "RETURN"))
    private void hookInitItem(ItemConvertible item, int count, CallbackInfo ci) {
        DurabilityEvent durabilityEvent = new DurabilityEvent(getDamage());
        TeamRainy.EVENT_BUS.post(durabilityEvent);
        if (durabilityEvent.isCancelled()) {
            getOrCreateNbt().putInt("Damage", durabilityEvent.getDamage());
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V", at = @At(
            value = "RETURN"))
    private void hookInitNbt(NbtCompound nbt, CallbackInfo ci) {
        DurabilityEvent durabilityEvent = new DurabilityEvent(nbt.getInt("Damage"));
        TeamRainy.EVENT_BUS.post(durabilityEvent);
        if (durabilityEvent.isCancelled()) {
            getOrCreateNbt().putInt("Damage", durabilityEvent.getDamage());
        }
    }
}
