package club.tater.tatergod.features.modules.client;

import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;

public class NickHider extends Module {

    private static NickHider instance;
    public final Setting NameString = this.register(new Setting("Name", "New Name Here..."));

    public NickHider() {
        super("NickHider", "Changes name", Module.Category.CLIENT, false, false, false);
        NickHider.instance = this;
    }

    public static NickHider getInstance() {
        if (NickHider.instance == null) {
            NickHider.instance = new NickHider();
        }

        return NickHider.instance;
    }

    public void onEnable() {
        Command.sendMessage(ChatFormatting.GRAY + "Success! Name succesfully changed to " + ChatFormatting.GREEN + (String) this.NameString.getValue());
    }
}
