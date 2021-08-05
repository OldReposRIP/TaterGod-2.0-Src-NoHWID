package club.tater.tatergod.features.command.commands;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.command.Command;

public class UnloadCommand extends Command {

    public UnloadCommand() {
        super("unload", new String[0]);
    }

    public void execute(String[] commands) {
        Tater.unload(true);
    }
}
