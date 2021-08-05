package club.tater.tatergod.features.command.commands;

import club.tater.tatergod.features.command.Command;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReloadSoundCommand extends Command {

    public ReloadSoundCommand() {
        super("reloadsound", new String[0]);
    }

    public void execute(String[] commands) {
        try {
            SoundManager e = (SoundManager) ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, ReloadSoundCommand.mc.getSoundHandler(), new String[] { "sndManager", "sndManager"});

            e.reloadSoundSystem();
            Command.sendMessage(ChatFormatting.GREEN + "Reloaded Sound System.");
        } catch (Exception exception) {
            System.out.println(ChatFormatting.RED + "Could not restart sound manager: " + exception);
            exception.printStackTrace();
            Command.sendMessage(ChatFormatting.RED + "Couldn\'t Reload Sound System!");
        }

    }
}
