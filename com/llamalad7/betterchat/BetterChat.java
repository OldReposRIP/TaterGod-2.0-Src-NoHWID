package com.llamalad7.betterchat;

import com.llamalad7.betterchat.command.CommandConfig;
import com.llamalad7.betterchat.handlers.InjectHandler;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
    modid = "betterchat",
    name = "Better Chat",
    version = "1.4"
)
@SideOnly(Side.CLIENT)
public class BetterChat {

    public static final String MODID = "betterchat";
    public static final String NAME = "Better Chat";
    public static final String VERSION = "1.4";
    private static ChatSettings settings;

    public static ChatSettings getSettings() {
        return BetterChat.settings;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        BetterChat.settings = new ChatSettings(new Configuration(event.getSuggestedConfigurationFile()));
        BetterChat.settings.loadConfig();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new InjectHandler());
        ClientCommandHandler.instance.registerCommand(new CommandConfig());
    }
}
