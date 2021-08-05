package club.tater.tatergod.manager;

import club.tater.tatergod.features.Feature;
import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.command.commands.BindCommand;
import club.tater.tatergod.features.command.commands.ConfigCommand;
import club.tater.tatergod.features.command.commands.FriendCommand;
import club.tater.tatergod.features.command.commands.HelpCommand;
import club.tater.tatergod.features.command.commands.HistoryCommand;
import club.tater.tatergod.features.command.commands.ModuleCommand;
import club.tater.tatergod.features.command.commands.PrefixCommand;
import club.tater.tatergod.features.command.commands.ReloadCommand;
import club.tater.tatergod.features.command.commands.ReloadSoundCommand;
import club.tater.tatergod.features.command.commands.UnloadCommand;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class CommandManager extends Feature {

    private final ArrayList commands = new ArrayList();
    private String clientMessage = "<TaterGod.CC>";
    private String prefix = ".";

    public CommandManager() {
        super("Command");
        this.commands.add(new BindCommand());
        this.commands.add(new ModuleCommand());
        this.commands.add(new PrefixCommand());
        this.commands.add(new ConfigCommand());
        this.commands.add(new FriendCommand());
        this.commands.add(new HelpCommand());
        this.commands.add(new HistoryCommand());
        this.commands.add(new ReloadCommand());
        this.commands.add(new UnloadCommand());
        this.commands.add(new ReloadSoundCommand());
    }

    public static String[] removeElement(String[] input, int indexToDelete) {
        LinkedList result = new LinkedList();

        for (int i = 0; i < input.length; ++i) {
            if (i != indexToDelete) {
                result.add(input[i]);
            }
        }

        return (String[]) result.toArray(input);
    }

    private static String strip(String str, String key) {
        return str.startsWith(key) && str.endsWith(key) ? str.substring(key.length(), str.length() - key.length()) : str;
    }

    public void executeCommand(String command) {
        String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String name = parts[0].substring(1);
        String[] args = removeElement(parts, 0);

        for (int i = 0; i < args.length; ++i) {
            if (args[i] != null) {
                args[i] = strip(args[i], "\"");
            }
        }

        Iterator iterator = this.commands.iterator();

        Command c;

        do {
            if (!iterator.hasNext()) {
                Command.sendMessage(ChatFormatting.GRAY + "Command not found, type \'help\' for the commands list.");
                return;
            }

            c = (Command) iterator.next();
        } while (!c.getName().equalsIgnoreCase(name));

        c.execute(parts);
    }

    public Command getCommandByName(String name) {
        Iterator iterator = this.commands.iterator();

        Command command;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            command = (Command) iterator.next();
        } while (!command.getName().equals(name));

        return command;
    }

    public ArrayList getCommands() {
        return this.commands;
    }

    public String getClientMessage() {
        return this.clientMessage;
    }

    public void setClientMessage(String clientMessage) {
        this.clientMessage = clientMessage;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
