package developmentteam.teamrainy.mod.commands;

import developmentteam.teamrainy.core.impl.CommandManager;
import developmentteam.teamrainy.api.utils.Wrapper;

import java.util.List;
import java.util.Objects;

public abstract class Command implements Wrapper {
	protected final String name;
	protected final String syntax;
	
	public Command(String name, String syntax) {
		this.name = Objects.requireNonNull(name);
		this.syntax = Objects.requireNonNull(syntax);
	}

	public String getName() {
		return this.name;
	}
	public String getSyntax() {
		return this.syntax;
	}

	public abstract void runCommand(String[] parameters);

	public abstract String[] getAutocorrect(int count, List<String> seperated);

	public void sendUsage() {
		CommandManager.sendChatMessage("§fusage: §r" + getName() + " " + getSyntax());
	}
}
