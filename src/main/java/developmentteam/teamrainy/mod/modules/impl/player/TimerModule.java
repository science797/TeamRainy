package developmentteam.teamrainy.mod.modules.impl.player;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.api.events.eventbus.EventHandler;
import developmentteam.teamrainy.api.events.impl.PacketEvent;
import developmentteam.teamrainy.api.utils.entity.MovementUtil;
import developmentteam.teamrainy.api.utils.math.Easing;
import developmentteam.teamrainy.api.utils.math.FadeUtils;
import developmentteam.teamrainy.api.utils.math.Timer;
import developmentteam.teamrainy.mod.modules.Module;
import developmentteam.teamrainy.mod.modules.settings.impl.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import java.awt.*;
import java.text.DecimalFormat;

public class TimerModule extends Module {
	public final SliderSetting multiplier = add(new SliderSetting("Speed", 1, 0.1, 5, 0.01));
	public final BindSetting boostKey = add(new BindSetting("BoostKey", -1));
	public final SliderSetting boost = add(new SliderSetting("Boost", 1, 0.1, 10, 0.01));
	private final BooleanSetting tickShift = add(new BooleanSetting("TickShift", true).setParent());
	private final SliderSetting shiftTimer = add(new SliderSetting("ShiftTimer", 2, 1, 10, 0.1, () -> tickShift.isOpen()));
	private final SliderSetting accumulate = add(new SliderSetting("Charge", 2000f, 1f, 10000f, 50f, () -> tickShift.isOpen()).setSuffix("ms"));
	private final SliderSetting minAccumulate = add(new SliderSetting("MinCharge", 500f, 1f, 10000f, 50f, () -> tickShift.isOpen()).setSuffix("ms"));
	private final BooleanSetting smooth = add(new BooleanSetting("Smooth", true, () -> tickShift.isOpen()).setParent());
	private final EnumSetting<Easing> ease = add(new EnumSetting<>("Ease", Easing.CubicInOut, () -> smooth.isOpen() && tickShift.isOpen()));
	private final BooleanSetting reset = add(new BooleanSetting("Reset", true, () -> tickShift.isOpen()));
	private final BooleanSetting indicator = add(new BooleanSetting("Indicator", true, () -> tickShift.isOpen()).setParent());
	private final ColorSetting work = add(new ColorSetting("Completed", new Color(0, 255, 0), () -> indicator.isOpen() && tickShift.isOpen()));
	private final ColorSetting charging = add(new ColorSetting("Charging", new Color(255, 0, 0), () -> indicator.isOpen() && tickShift.isOpen()));
	private final SliderSetting yOffset = add(new SliderSetting("YOffset", 0, -200, 200, 1, () -> indicator.isOpen() && tickShift.isOpen()));
	public static TimerModule INSTANCE;
	public TimerModule() {
		super("Timer", Category.Player);
		setChinese("时间加速");
		INSTANCE = this;
	}
// @future_COCO:你但凡看过整个项目就知道为什么要叫TimerModule而不是Timer了
	@Override
	public void onDisable() {
		TeamRainy.TIMER.reset();
	}

	@Override
	public void onUpdate() {
		TeamRainy.TIMER.tryReset();
	}

	@Override
	public void onEnable() {
		TeamRainy.TIMER.reset();
	}

	private final Timer timer = new Timer();
	private final Timer timer2 = new Timer();
	DecimalFormat df = new DecimalFormat("0.0");
	private final FadeUtils end = new FadeUtils(500);

	long lastMs = 0;
	boolean moving = false;
	@Override
	public void onRender2D(DrawContext drawContext, float tickDelta) {
		if (!tickShift.getValue()) return;
		timer.setMs(Math.min(Math.max(0, timer.getPassedTimeMs()), accumulate.getValueInt()));
		if (MovementUtil.isMoving() && !TeamRainy.PLAYER.insideBlock) {

			if (!moving) {
				if (timer.passedMs(minAccumulate.getValue())) {
					timer2.reset();
					lastMs = timer.getPassedTimeMs();
				} else {
					lastMs = 0;
				}
				moving = true;
			}

			timer.reset();

			if (timer2.passed(lastMs)) {
				TeamRainy.TIMER.reset();
			} else {
				if (smooth.getValue()) {
					double timer = TeamRainy.TIMER.getDefault() + (1 - end.ease(ease.getValue())) * (shiftTimer.getValueFloat() - 1) * (lastMs / accumulate.getValue());
					TeamRainy.TIMER.set((float) Math.max(TeamRainy.TIMER.getDefault(), timer));
				} else {
					TeamRainy.TIMER.set(shiftTimer.getValueFloat());
				}
			}
		} else {
			if (moving) {
				TeamRainy.TIMER.reset();
				if (reset.getValue()) {
					timer.reset();
				} else {
					timer.setMs(Math.max(lastMs - timer2.getPassedTimeMs(), 0));
				}
				moving = false;
			}
			end.setLength(timer.getPassedTimeMs());
			end.reset();
		}

		if (indicator.getValue()) {
			double current = (moving ? (Math.max(lastMs - timer2.getPassedTimeMs(), 0)) : timer.getPassedTimeMs());
			boolean completed = moving && current > 0 || current >= minAccumulate.getValueInt();
			double max = accumulate.getValue();
			String text = df.format(current / max * 100L) + "%";
			drawContext.drawText(mc.textRenderer, text, mc.getWindow().getScaledWidth() / 2 - mc.textRenderer.getWidth(text) / 2, mc.getWindow().getScaledHeight() / 2 + mc.textRenderer.fontHeight - yOffset.getValueInt(), completed ? this.work.getValue().getRGB() : this.charging.getValue().getRGB(), true);
		}
	}

	@Override
	public String getInfo() {
		if (!tickShift.getValue()) return null;
		double current = (moving ? (Math.max(lastMs - timer2.getPassedTimeMs(), 0)) : timer.getPassedTimeMs());
		double max = accumulate.getValue();
		double value = Math.min(current / max * 100, 100);
		return df.format(value) + "%";
	}

	@EventHandler
	public void onReceivePacket(PacketEvent.Receive event) {
		if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
			lastMs = 0;
		}
	}
}