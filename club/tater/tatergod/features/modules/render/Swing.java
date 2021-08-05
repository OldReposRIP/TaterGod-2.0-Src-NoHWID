package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.Util;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;

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

            if (((Swing.Hand) this.hand.getValue()).equals(Swing.Hand.PACKETSWING) && Util.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && (double) Util.mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9D) {
                Util.mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0F;
                Util.mc.entityRenderer.itemRenderer.itemStackMainHand = Util.mc.player.getHeldItemMainhand();
            }

        }
    }

    public static enum Hand {

        OFFHAND, MAINHAND, PACKETSWING;
    }
}
