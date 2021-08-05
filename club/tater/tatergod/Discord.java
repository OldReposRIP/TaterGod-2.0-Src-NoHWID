package club.tater.tatergod;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import club.tater.tatergod.features.modules.misc.RPC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

public class Discord {

    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;
    public static DiscordRichPresence presence = new DiscordRichPresence();
    private static Thread thread;

    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();

        Discord.rpc.Discord_Initialize("837047291570290699", handlers, true, "");
        Discord.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        Discord.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu ? "In the main menu." : "Playing " + (Minecraft.getMinecraft().getCurrentServerData() != null ? (((Boolean) RPC.INSTANCE.showIP.getValue()).booleanValue() ? "on " + Minecraft.getMinecraft().getCurrentServerData().serverIP + "." : " multiplayer.") : " singleplayer.");
        Discord.presence.state = (String) RPC.INSTANCE.state.getValue();
        Discord.presence.largeImageKey = "tater";
        Discord.presence.largeImageText = "TaterGod.CC";
        Discord.rpc.Discord_UpdatePresence(Discord.presence);
        (Discord.thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Discord.rpc.Discord_RunCallbacks();
                String string = "";
                StringBuilder sb = new StringBuilder();
                DiscordRichPresence presence = Discord.presence;

                (new StringBuilder()).append("Playing ");
                if (Minecraft.getMinecraft().getCurrentServerData() != null) {
                    if (((Boolean) RPC.INSTANCE.showIP.getValue()).booleanValue()) {
                        string = "on " + Minecraft.getMinecraft().getCurrentServerData().serverIP + ".";
                    } else {
                        string = " multiplayer.";
                    }
                } else {
                    string = " singleplayer.";
                }

                presence.details = sb.append(string).toString();
                Discord.presence.state = (String) RPC.INSTANCE.state.getValue();
                Discord.rpc.Discord_UpdatePresence(Discord.presence);

                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException interruptedexception) {
                    ;
                }
            }

        }, "RPC-Callback-Handler")).start();
    }

    public static void stop() {
        if (Discord.thread != null && !Discord.thread.isInterrupted()) {
            Discord.thread.interrupt();
        }

        Discord.rpc.Discord_Shutdown();
    }
}
