package developmentteam.teamrainy.mod.commands.impl;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.mod.commands.Command;
import developmentteam.teamrainy.core.impl.CommandManager;

import java.util.List;

public class PrefixCommand extends Command {

	public PrefixCommand() {
		super("prefix", "[prefix]");
	}

	@Override
	public void runCommand(String[] parameters) {
		if (parameters.length == 0) {
			sendUsage();
			return;
		}
		if (parameters[0].startsWith("/")) {
			CommandManager.sendChatMessage("§fPlease specify a valid §bprefix.");
			return;
		}
		TeamRainy.PREFIX = parameters[0];
		CommandManager.sendChatMessage("§bPrefix §fset to §e" + parameters[0]);
	}

	@Override
	public String[] getAutocorrect(int count, List<String> seperated) {
		return null;
	}
}
