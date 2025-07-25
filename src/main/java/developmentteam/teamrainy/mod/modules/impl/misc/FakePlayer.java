package developmentteam.teamrainy.mod.modules.impl.misc;

import com.mojang.authlib.GameProfile;
import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.api.events.eventbus.EventHandler;
import developmentteam.teamrainy.api.events.impl.PacketEvent;
import developmentteam.teamrainy.api.utils.world.BlockPosX;
import developmentteam.teamrainy.api.utils.world.BlockUtil;
import developmentteam.teamrainy.mod.modules.Module;
import developmentteam.teamrainy.mod.modules.impl.combat.RainyAnchor;
import developmentteam.teamrainy.mod.modules.impl.combat.RainyCrystal;
import developmentteam.teamrainy.mod.modules.settings.impl.BooleanSetting;
import developmentteam.teamrainy.mod.modules.settings.impl.StringSetting;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class FakePlayer extends Module {
	public static FakePlayer INSTANCE;

	public FakePlayer() {
		super("FakePlayer", Category.Misc);
		setChinese("假人");
		INSTANCE = this;
	}

	private final StringSetting name = add(new StringSetting("Name", "TeamRainy"));
	private final BooleanSetting damage = add(new BooleanSetting("Damage", true));
	private final BooleanSetting autoTotem = add(new BooleanSetting("AutoTotem", true));
	public static OtherClientPlayerEntity fakePlayer;
	@Override
	public String getInfo() {
		return name.getValue();
	}

	@Override
	public void onEnable() {
		if (nullCheck()) {
			disable();
			return;
		}
		fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.fromString("11451400-6666-6666-0001-145141919810"), name.getValue())){
			@Override
			public boolean isOnGround() {
				return true;
			}
		};
		fakePlayer.getInventory().clone(mc.player.getInventory());
		mc.world.addEntity(fakePlayer);
		fakePlayer.copyPositionAndRotation(mc.player);
		fakePlayer.bodyYaw = mc.player.bodyYaw;
		fakePlayer.headYaw = mc.player.headYaw;
		fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 9999, 2));
		fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 9999, 3));
		fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 9999, 1));
	}

	@Override
	public void onUpdate() {
		if (!(fakePlayer != null && !fakePlayer.isDead() && fakePlayer.clientWorld == mc.world)) {
			disable();
			return;
		}
		fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 9999, 2));
		if (autoTotem.getValue() && fakePlayer.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
			TeamRainy.POP.onTotemPop(fakePlayer);
			fakePlayer.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));
		}
		if (fakePlayer.isDead()) {
			if (fakePlayer.tryUseTotem(mc.world.getDamageSources().generic())) {
				fakePlayer.setHealth(10f);
				new EntityStatusS2CPacket(fakePlayer, EntityStatuses.USE_TOTEM_OF_UNDYING).apply(mc.getNetworkHandler());
			}
		}
	}

	@Override
	public void onDisable() {
		if (fakePlayer == null) return;
		fakePlayer.kill();
		fakePlayer.setRemoved(Entity.RemovalReason.KILLED);
		fakePlayer.onRemoved();
		fakePlayer = null;
	}

	@EventHandler
	public void onPacketReceive(PacketEvent.Receive event) {
		if (damage.getValue() && fakePlayer != null && fakePlayer.hurtTime == 0) {
			if (autoTotem.getValue() && fakePlayer.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
				fakePlayer.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));
			}
			if (event.getPacket() instanceof ExplosionS2CPacket explosion) {
				if (MathHelper.sqrt((float) new Vec3d(explosion.getX(), explosion.getY(), explosion.getZ()).squaredDistanceTo(fakePlayer.getPos())) > 10) return;
				float damage;
				if (BlockUtil.getBlock(new BlockPosX(explosion.getX(), explosion.getY(), explosion.getZ())) == Blocks.RESPAWN_ANCHOR) {
					damage = (float) RainyAnchor.INSTANCE.getAnchorDamage(new BlockPosX(explosion.getX(), explosion.getY(), explosion.getZ()), fakePlayer, fakePlayer);
				} else {
					damage = RainyCrystal.INSTANCE.calculateDamage(new Vec3d(explosion.getX(), explosion.getY(), explosion.getZ()), fakePlayer, fakePlayer);
				}
				fakePlayer.onDamaged(mc.world.getDamageSources().generic());
				if (fakePlayer.getAbsorptionAmount() >= damage) {
					fakePlayer.setAbsorptionAmount(fakePlayer.getAbsorptionAmount() - damage);
				} else {
					float damage2 = damage - fakePlayer.getAbsorptionAmount();
					fakePlayer.setAbsorptionAmount(0);
					fakePlayer.setHealth(fakePlayer.getHealth() - damage2);
				}
			}
			if (fakePlayer.isDead()) {
				if (fakePlayer.tryUseTotem(mc.world.getDamageSources().generic())) {
					fakePlayer.setHealth(10f);
					new EntityStatusS2CPacket(fakePlayer, EntityStatuses.USE_TOTEM_OF_UNDYING).apply(mc.getNetworkHandler());
				}
			}
		}
	}
}