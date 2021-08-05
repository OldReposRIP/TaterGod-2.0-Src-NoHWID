package club.tater.tatergod.features.modules.misc;

import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.Timer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatSuffix extends Module {

    private static ChatSuffix INSTANCE = new ChatSuffix();
    private final Timer timer = new Timer();
    public Setting suffix;

    public ChatSuffix() {
        super("ChatSuffix", "Mmm sexy chat suffix.", Module.Category.MISC, true, false, false);
        this.suffix = this.register(new Setting("Suffix", ChatSuffix.Suffix.UNICODE, "Shows that ur using a based Client in chat."));
        this.setInstance();
    }

    public static ChatSuffix getInstance() {
        if (ChatSuffix.INSTANCE == null) {
            ChatSuffix.INSTANCE = new ChatSuffix();
        }

        return ChatSuffix.INSTANCE;
    }

    private void setInstance() {
        ChatSuffix.INSTANCE = this;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage packet = (CPacketChatMessage) event.getPacket();
            String s = packet.getMessage();

            if (s.startsWith("/")) {
                return;
            }

            switch ((ChatSuffix.Suffix) this.suffix.getValue()) {
            case UNICODE:
                s = s + " �?? ᴛᴀᴛᴇʀɢ�?ᴅ.ᴄᴄ";

            default:
                switch ((ChatSuffix.Suffix) this.suffix.getValue()) {
                case NONUNICODE:
                    s = s + " | TaterGod.CC";

                default:
                    switch ((ChatSuffix.Suffix) this.suffix.getValue()) {
                    case CRACKUNICODE:
                        s = s + " �?? ɪ ʟ�?ᴠᴇ ꜰᴇ�?ʙ�?ሃ ꜱ�?ᴄᴋꜱ";

                    default:
                        switch ((ChatSuffix.Suffix) this.suffix.getValue()) {
                        case CRACKNONUNICODE:
                            s = s + " | I love femboy socks";

                        default:
                            if (s.length() >= 256) {
                                s = s.substring(0, 256);
                            }

                            packet.message = s;
                        }
                    }
                }
            }
        }

    }

    public static enum Suffix {

        UNICODE, NONUNICODE, CRACKUNICODE, CRACKNONUNICODE;
    }
}
