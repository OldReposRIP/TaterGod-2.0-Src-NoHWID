package club.tater.tatergod.features.modules.player;

import club.tater.tatergod.event.events.Packet;
import club.tater.tatergod.features.Feature;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.Util;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Swing extends Module {

    private final Setting hand;

    public Swing() {
        super("Swing", "Changes the hand you swing with", Module.Category.RENDER, false, false, false);
        this.hand = this.register(new Setting("Hand", Swing.Hand.OFFHAND));
    }

    public void onUpdate() {
        if (Util.mc.world != null) {
            if (((Swing.Hand) this.hand.getValue()).equals(Swing.Hand.OFFHAND)) {
                Util.mc.player.swingingHand = EnumHand.OFF_HAND;
            }

            if (((Swing.Hand) this.hand.getValue()).equals(Swing.Hand.MAINHAND)) {
                Util.mc.player.swingingHand = EnumHand.MAIN_HAND;
            }

        }
    }

    @SubscribeEvent
    public void onPacket(Packet event) {
        if (!Feature.nullCheck() && event.getType() != Packet.Type.INCOMING) {
            if (event.getPacket() instanceof CPacketAnimation) {
                event.setCanceled(true);
            }

        }
    }

    public static enum Hand {

        OFFHAND, MAINHAND, PACKETSWING;
    }
}
