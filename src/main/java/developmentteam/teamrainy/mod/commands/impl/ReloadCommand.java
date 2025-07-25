package developmentteam.teamrainy.mod.commands.impl;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.core.impl.CommandManager;
import developmentteam.teamrainy.core.impl.ConfigManager;
import developmentteam.teamrainy.mod.commands.Command;

import java.util.List;

public class ReloadCommand extends Command {

	public ReloadCommand() {
		super("reload", "");
	}

	@Override
	public void runCommand(String[] parameters) {
		CommandManager.sendChatMessage("§fReloading..");
		TeamRainy.CONFIG = new ConfigManager();
		TeamRainy.PREFIX = TeamRainy.CONFIG.getString("prefix", TeamRainy.PREFIX);
		TeamRainy.CONFIG.loadSettings();
		TeamRainy.XRAY.read();
		TeamRainy.TRADE.read();
		TeamRainy.FRIEND.read();
	}

	@Override
	public String[] getAutocorrect(int count, List<String> seperated) {
		return null;
	}
}
