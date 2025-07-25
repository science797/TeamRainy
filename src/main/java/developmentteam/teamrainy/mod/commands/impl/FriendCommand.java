package developmentteam.teamrainy.mod.commands.impl;

import developmentteam.teamrainy.TeamRainy;
import developmentteam.teamrainy.mod.commands.Command;
import developmentteam.teamrainy.core.impl.CommandManager;

import java.util.ArrayList;
import java.util.List;

public class FriendCommand extends Command {

	public FriendCommand() {
		super("friend", "[name/reset/list] | [add/remove] [name]");
	}

	@Override
	public void runCommand(String[] parameters) {
		if (parameters.length == 0) {
			sendUsage();
			return;
		}
        switch (parameters[0]) {
            case "reset" -> {
                TeamRainy.FRIEND.friendList.clear();
                CommandManager.sendChatMessage("§fFriends list got reset");
                return;
            }
            case "list" -> {
                if (TeamRainy.FRIEND.friendList.isEmpty()) {
                    CommandManager.sendChatMessage("§fFriends list is empty");
                    return;
                }
                StringBuilder friends = new StringBuilder();
                int time = 0;
                boolean first = true;
                boolean start = true;
                for (String name : TeamRainy.FRIEND.friendList) {
                    if (!first) {
                        friends.append(", ");
                    }
                    friends.append(name);
                    first = false;
                    time++;
                    if (time > 3) {
                        CommandManager.sendChatMessage((start ? "§eFriends §a" : "§a") + friends);
                        friends = new StringBuilder();
                        start = false;
                        first = true;
                        time = 0;
                    }
                }
                if (first) {
                    CommandManager.sendChatMessage("§a" + friends);
                }
                return;
            }
            case "add" -> {
                if (parameters.length == 2) {
                    TeamRainy.FRIEND.addFriend(parameters[1]);
                    CommandManager.sendChatMessage("§f" + parameters[1] + (TeamRainy.FRIEND.isFriend(parameters[1]) ? " §ahas been friended" : " §chas been unfriended"));
                    return;
                }
                sendUsage();
                return;
            }
            case "remove" -> {
                if (parameters.length == 2) {
                    TeamRainy.FRIEND.removeFriend(parameters[1]);
                    CommandManager.sendChatMessage("§f" + parameters[1] + (TeamRainy.FRIEND.isFriend(parameters[1]) ? " §ahas been friended" : " §chas been unfriended"));
                    return;
                }
                sendUsage();
                return;
            }
        }

        if (parameters.length == 1) {
			CommandManager.sendChatMessage("§f" + parameters[0] + (TeamRainy.FRIEND.isFriend(parameters[0]) ? " §ais friended" : " §cisn't friended"));
			return;
		}

		sendUsage();
	}

	@Override
	public String[] getAutocorrect(int count, List<String> seperated) {
		if (count == 1) {
			String input = seperated.get(seperated.size() - 1).toLowerCase();
			List<String> correct = new ArrayList<>();
			List<String> list = List.of("add", "remove", "list", "reset");
			for (String x : list) {
				if (input.equalsIgnoreCase(TeamRainy.PREFIX + "friend") || x.toLowerCase().startsWith(input)) {
					correct.add(x);
				}
			}
			int numCmds = correct.size();
			String[] commands = new String[numCmds];

			int i = 0;
			for (String x : correct) {
				commands[i++] = x;
			}

			return commands;
		}
		return null;
	}
}
