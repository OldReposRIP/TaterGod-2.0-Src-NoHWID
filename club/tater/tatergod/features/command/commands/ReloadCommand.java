package club.tater.tatergod.features.command.commands;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.command.Command;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("reload", new String[0]);
    }

    public void execute(String[] commands) {
        Tater.reload();
    }
}
