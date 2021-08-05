package com.llamalad7.betterchat.command;

import com.llamalad7.betterchat.gui.GuiConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class CommandConfig extends CommandBase {

    public String getName() {
        return "betterchat";
    }

    public String getUsage(ICommandSender sender) {
        return "/betterchat";
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        MinecraftForge.EVENT_BUS.unregister(this);
        Minecraft.getMinecraft().displayGuiScreen(new GuiConfig());
    }
}
