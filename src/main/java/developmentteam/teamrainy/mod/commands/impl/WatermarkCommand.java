package developmentteam.teamrainy.mod.commands.impl;

import developmentteam.teamrainy.mod.commands.Command;
import developmentteam.teamrainy.mod.modules.impl.client.HUD;

import java.util.Arrays;
import java.util.List;

public class WatermarkCommand extends Command {

	public WatermarkCommand() {
		super("watermark", "[text]");
	}

	@Override
	public void runCommand(String[] parameters) {
		if (parameters.length == 0) {
			sendUsage();
			return;
		}
		StringBuilder text = new StringBuilder();
		boolean first = true;
		for (String s : Arrays.stream(parameters).toList()) {
			if (first) {
				text.append(s);
				first = false;
				continue;
			}
			text.append(" ").append(s);
		}
		HUD.INSTANCE.waterMarkString.setValue(text.toString());
	}

	@Override
	public String[] getAutocorrect(int count, List<String> seperated) {
		return null;
	}
}
