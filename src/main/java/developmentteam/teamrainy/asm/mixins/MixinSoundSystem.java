package developmentteam.teamrainy.asm.mixins;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.api.events.impl.PlaySoundEvent;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class MixinSoundSystem {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void onPlay(SoundInstance soundInstance, CallbackInfo info) {
        PlaySoundEvent event = new PlaySoundEvent(soundInstance);
        TeamRainy.EVENT_BUS.post(event);
        if (event.isCancelled()) info.cancel();
    }
}
