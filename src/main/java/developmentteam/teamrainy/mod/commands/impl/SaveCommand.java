package developmentteam.teamrainy.mod.commands.impl;

import developmentteam.teamrainy.core.Manager;
import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.core.impl.CommandManager;
import developmentteam.teamrainy.core.impl.ConfigManager;
import developmentteam.teamrainy.mod.commands.Command;

import java.util.List;

public class SaveCommand extends Command {

	public SaveCommand() {
		super("save", "");
	}

	@Override
	public void runCommand(String[] parameters) {
		if (parameters.length == 1) {
			CommandManager.sendChatMessage("§fSaving config named " + parameters[0]);
			ConfigManager.options = Manager.getFile(parameters[0] + ".cfg");
			TeamRainy.save();
			ConfigManager.options = Manager.getFile("options.txt");
		} else {
			CommandManager.sendChatMessage("§fSaving..");
		}
		TeamRainy.save();
	}

	@Override
	public String[] getAutocorrect(int count, List<String> seperated) {
		return null;
	}
}
