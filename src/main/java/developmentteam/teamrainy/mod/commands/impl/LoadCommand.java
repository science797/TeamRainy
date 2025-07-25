package developmentteam.teamrainy.mod.commands.impl;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.core.Manager;
import developmentteam.teamrainy.core.impl.CommandManager;
import developmentteam.teamrainy.core.impl.ConfigManager;
import developmentteam.teamrainy.mod.commands.Command;

import java.util.List;

public class LoadCommand extends Command {

	public LoadCommand() {
		super("load", "[config]");
	}

	@Override
	public void runCommand(String[] parameters) {
		if (parameters.length == 0) {
			sendUsage();
			return;
		}
		CommandManager.sendChatMessage("§fLoading..");
		ConfigManager.options = Manager.getFile(parameters[0] + ".cfg");
		TeamRainy.CONFIG = new ConfigManager();
		TeamRainy.PREFIX = TeamRainy.CONFIG.getString("prefix", TeamRainy.PREFIX);
		TeamRainy.CONFIG.loadSettings();
        ConfigManager.options = Manager.getFile("options.txt");
		TeamRainy.save();
	}

	@Override
	public String[] getAutocorrect(int count, List<String> seperated) {
		return null;
	}
}
