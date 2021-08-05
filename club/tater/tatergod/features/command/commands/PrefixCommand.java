package club.tater.tatergod.features.command.commands;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.command.Command;
import com.mojang.realmsclient.gui.ChatFormatting;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super("prefix", new String[] { "<char>"});
    }

    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(ChatFormatting.GREEN + "Current prefix is " + Tater.commandManager.getPrefix());
        } else {
            Tater.commandManager.setPrefix(commands[0]);
            Command.sendMessage("Prefix changed to " + ChatFormatting.GRAY + commands[0]);
        }
    }
}
