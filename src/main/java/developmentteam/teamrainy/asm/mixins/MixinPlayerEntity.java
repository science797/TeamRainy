package developmentteam.teamrainy.asm.mixins;

import developmentteam.teamrainy.api.events.Event;
import developmentteam.teamrainy.api.events.impl.JumpEvent;
import developmentteam.teamrainy.api.events.impl.TravelEvent;
import developmentteam.teamrainy.api.utils.Wrapper;
import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.mod.modules.impl.client.HackSetting;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerEntity.class)
public class MixinPlayerEntity implements Wrapper {

    @Inject(method = "canChangeIntoPose", at = @At("RETURN"), cancellable = true)
    private void poseNotCollide(EntityPose pose, CallbackInfoReturnable<Boolean> cir) {
        if ((PlayerEntity) (Object) this == mc.player && !HackSetting.INSTANCE.crawl.getValue() && pose == EntityPose.SWIMMING) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "jump", at = @At("HEAD"))
    private void onJumpPre(CallbackInfo ci) {
        TeamRainy.EVENT_BUS.post(new JumpEvent(Event.Stage.Pre));
    }

    @Inject(method = "jump", at = @At("RETURN"))
    private void onJumpPost(CallbackInfo ci) {
        TeamRainy.EVENT_BUS.post(new JumpEvent(Event.Stage.Post));
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravelPre(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if(player != mc.player)
            return;

        TravelEvent event = new TravelEvent(Event.Stage.Pre, player);
        TeamRainy.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
            event = new TravelEvent(Event.Stage.Post, player);
            TeamRainy.EVENT_BUS.post(event);
        }
    }

    @Inject(method = "travel", at = @At("RETURN"))
    private void onTravelPost(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if(player != mc.player)
            return;

        TravelEvent event = new TravelEvent(Event.Stage.Post, player);
        TeamRainy.EVENT_BUS.post(event);
    }
}
