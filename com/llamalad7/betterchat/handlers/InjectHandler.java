package com.llamalad7.betterchat.handlers;

import com.llamalad7.betterchat.gui.GuiBetterChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class InjectHandler {

    public static GuiBetterChat chatGUI;

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        MinecraftForge.EVENT_BUS.unregister(this);
        InjectHandler.chatGUI = new GuiBetterChat(Minecraft.getMinecraft());
        ObfuscationReflectionHelper.setPrivateValue(GuiIngame.class, Minecraft.getMinecraft().ingameGUI, InjectHandler.chatGUI, new String[] { "persistantChatGUI"});
    }
}
