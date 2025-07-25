package developmentteam.teamrainy.asm.accessors;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerMoveC2SPacket.class)
public interface IPlayerMoveC2SPacket {
    @Mutable
    @Accessor("onGround")
    void setOnGround(boolean onGround);

    @Mutable
    @Accessor("pitch")
    void setPitch(float pitch);

    @Mutable
    @Accessor("yaw")
    void setYaw(float yaw);
}
