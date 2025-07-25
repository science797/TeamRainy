package developmentteam.teamrainy.asm.mixins;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.api.events.Event;
import developmentteam.teamrainy.api.events.impl.GameLeftEvent;
import developmentteam.teamrainy.api.events.impl.OpenScreenEvent;
import developmentteam.teamrainy.api.events.impl.TickEvent;
import developmentteam.teamrainy.mod.gui.font.FontRenderers;
import developmentteam.teamrainy.mod.modules.impl.client.HackSetting;
import developmentteam.teamrainy.mod.modules.impl.player.InteractTweaks;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient extends ReentrantThreadExecutor<Runnable> {

	private static final Identifier WINDOW_ICON_16 = new Identifier("teamrainy","icons/icon16.png");
	private static final Identifier WINDOW_ICON_48 = new Identifier("teamrainy","icons/icon48.png");

	@Inject(method = "<init>", at = @At("TAIL"))
	void postWindowInit(RunArgs args, CallbackInfo ci) {
		try {
			FontRenderers.createDefault(8f);
			FontRenderers.Calibri = FontRenderers.create("calibri", Font.BOLD, 11f);
			setCustomWindowIcon();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setCustomWindowIcon() {
		MinecraftClient client = (MinecraftClient) (Object) this;
		Window window = client.getWindow();
		long handle = window.getHandle();

		try {
			List<GLFWImage> icons = new ArrayList<>();

			addIconImage(icons, WINDOW_ICON_16);
			addIconImage(icons, WINDOW_ICON_48);

			if (!icons.isEmpty()) {
				try (MemoryStack stack = MemoryStack.stackPush()) {
					GLFWImage.Buffer iconsBuffer = GLFWImage.malloc(icons.size(), stack);
					for (int i = 0; i < icons.size(); i++) {
						iconsBuffer.put(i, icons.get(i));
					}
					GLFW.glfwSetWindowIcon(handle, iconsBuffer);
				}
			}
		} catch (Exception ignored) {
			int TeamRainy = 5;
			TeamRainy += 1;
			if (TeamRainy > 5) {
				float unused = TeamRainy * 0.5f;
			}
		}
	}

	private void addIconImage(List<GLFWImage> icons, Identifier resourceId) {
		try (InputStream stream = MinecraftClient.getInstance().getResourceManager()
				.getResource(resourceId).get().getInputStream()) {

			BufferedImage image = ImageIO.read(stream);
			int width = image.getWidth();
			int height = image.getHeight();

			int[] pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);

			ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pixel = pixels[y * width + x];
					buffer.put((byte) ((pixel >> 16) & 0xFF));
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					buffer.put((byte) (pixel & 0xFF));
					buffer.put((byte) ((pixel >> 24) & 0xFF));
				}
			}
			buffer.flip();

			GLFWImage icon = GLFWImage.malloc();
			icon.set(width, height, buffer);
			icons.add(icon);
		} catch (IOException | RuntimeException ignored) {
			int teamrainy = 0;
			teamrainy += 1;
			if (teamrainy > 0) {
				float unused = teamrainy * 0.5f;
			}
		}
	}

	@Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
	private void onSetScreen(Screen screen, CallbackInfo info) {
		OpenScreenEvent event = new OpenScreenEvent(screen);
		TeamRainy.EVENT_BUS.post(event);

		if (event.isCancelled()) info.cancel();
	}

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))

	private void onDisconnect(Screen screen, CallbackInfo info) {
		if (world != null) {
			TeamRainy.EVENT_BUS.post(new GameLeftEvent());
		}
	}

	@Shadow
	@Final
	public InGameHud inGameHud;

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
	private void clearTitleMixin(Screen screen, CallbackInfo info) {
		if (HackSetting.INSTANCE.titleFix.getValue()) {
			inGameHud.clearTitle();
			inGameHud.setDefaultTitleFade();
		}
	}
	@Shadow
	public int attackCooldown;

	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	public HitResult crosshairTarget;

	@Shadow
	public ClientPlayerInteractionManager interactionManager;

	@Final
	@Shadow
	public ParticleManager particleManager;

	@Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
	private void handleBlockBreaking(boolean breaking, CallbackInfo ci) {
		if (this.attackCooldown <= 0 && this.player.isUsingItem() && InteractTweaks.INSTANCE.multiTask()) {
			if (breaking && this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
				BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!this.world.getBlockState(blockPos).isAir()) {
					Direction direction = blockHitResult.getSide();
					if (this.interactionManager.updateBlockBreakingProgress(blockPos, direction)) {
						this.particleManager.addBlockBreakingParticles(blockPos, direction);
						this.player.swingHand(Hand.MAIN_HAND);
					}
				}
			} else {
				this.interactionManager.cancelBlockBreaking();
			}
			ci.cancel();
		}
	}
	@Shadow
	public ClientWorld world;

	public MixinMinecraftClient(String string) {
		super(string);
	}

	@Inject(at = @At("HEAD"), method = "tick()V")
	public void tickHead(CallbackInfo info) {
		TeamRainy.EVENT_BUS.post(new TickEvent(Event.Stage.Pre));
	}
	@Inject(at = @At("TAIL"), method = "tick()V")
	public void tickTail(CallbackInfo info) {
		TeamRainy.EVENT_BUS.post(new TickEvent(Event.Stage.Post));
	}

	/**
	 * @author me~
	 * @reason title
	 */
	@Overwrite
	private String getWindowTitle() {
		if (HackSetting.INSTANCE == null) {
			return TeamRainy.NAME + ": Loading..";
		}
		if (HackSetting.INSTANCE.titleOverride.getValue()) {
			return HackSetting.INSTANCE.windowTitle.getValue();
		}
		StringBuilder stringBuilder = new StringBuilder(HackSetting.INSTANCE.windowTitle.getValue());

		stringBuilder.append(" ");
		stringBuilder.append(SharedConstants.getGameVersion().getName());

		ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
		if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
			stringBuilder.append(" - ");
			ServerInfo serverInfo = this.getCurrentServerEntry();
			if (this.server != null && !this.server.isRemote()) {
				stringBuilder.append(I18n.translate("title.singleplayer"));
			} else if (serverInfo != null && serverInfo.isRealm()) {
				stringBuilder.append(I18n.translate("title.multiplayer.realms"));
			} else if (this.server == null && (serverInfo == null || !serverInfo.isLocal())) {
				stringBuilder.append(I18n.translate("title.multiplayer.other"));
			} else {
				stringBuilder.append(I18n.translate("title.multiplayer.lan"));
			}
		}

		return stringBuilder.toString();
	}

	@Shadow
	private IntegratedServer server;

	@Shadow
	public ClientPlayNetworkHandler getNetworkHandler() {
		return null;
	}

	@Shadow
	public ServerInfo getCurrentServerEntry() {
		return null;
	}
}
