package developmentteam.teamrainy.mod.modules.impl.misc;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.mod.modules.settings.impl.StringSetting;
import developmentteam.teamrainy.api.events.eventbus.EventHandler;
import developmentteam.teamrainy.api.events.impl.SendMessageEvent;
import developmentteam.teamrainy.mod.modules.Module;

public class ChatSuffix extends Module {
	public static ChatSuffix INSTANCE;
	private final StringSetting message = add(new StringSetting("suffix", TeamRainy.CHAT_SUFFIX));
	public ChatSuffix() {
		super("ChatSuffix", Category.Misc);
		setChinese("消息后缀");
		INSTANCE = this;
	}

	@EventHandler
	public void onSendMessage(SendMessageEvent event) {
		if (nullCheck() || event.isCancelled() || AutoQueue.inQueue) return;
		String message = event.message;

		if (message.startsWith("/") || message.startsWith("!") || message.endsWith(this.message.getValue())) {
			return;
		}
		String suffix = this.message.getValue();
		message = message + " " + suffix;
		event.message = message;
	}
}