package club.tater.tatergod.features.modules.misc;

import club.tater.tatergod.Discord;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;

public class RPC extends Module {

    public static RPC INSTANCE;
    public Setting showIP = this.register(new Setting("ShowIP", Boolean.valueOf(true), "Shows the server IP in your discord presence."));
    public Setting state = this.register(new Setting("State", "TaterGod.CC", "Sets the state of the DiscordRPC."));

    public RPC() {
        super("RPC", "Discord rich presence", Module.Category.MISC, false, false, false);
        RPC.INSTANCE = this;
    }

    public void onEnable() {
        Discord.start();
    }

    public void onDisable() {
        Discord.stop();
    }
}
