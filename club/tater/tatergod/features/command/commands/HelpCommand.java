package club.tater.tatergod.features.command.commands;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.command.Command;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Iterator;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help");
    }

    public void execute(String[] commands) {
        sendMessage("Commands: ");
        Iterator iterator = Tater.commandManager.getCommands().iterator();

        while (iterator.hasNext()) {
            Command command = (Command) iterator.next();

            sendMessage(ChatFormatting.GRAY + Tater.commandManager.getPrefix() + command.getName());
        }

    }
}
