package club.tater.tatergod.features.modules.misc;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatModifier extends Module {

    private static ChatModifier INSTANCE = new ChatModifier();
    public Setting clean = this.register(new Setting("NoChatBackground", Boolean.valueOf(false), "Cleans your chat"));
    public Setting infinite = this.register(new Setting("InfiniteChat", Boolean.valueOf(false), "Makes your chat infinite."));
    public boolean check;

    public ChatModifier() {
        super("BetterChat", "Modifies your chat", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static ChatModifier getInstance() {
        if (ChatModifier.INSTANCE == null) {
            ChatModifier.INSTANCE = new ChatModifier();
        }

        return ChatModifier.INSTANCE;
    }

    private void setInstance() {
        ChatModifier.INSTANCE = this;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();

            this.check = !s.startsWith(Tater.commandManager.getPrefix());
        }

    }
}
