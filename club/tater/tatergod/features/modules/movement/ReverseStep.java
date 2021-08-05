package club.tater.tatergod.features.modules.movement;

import club.tater.tatergod.features.Feature;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.Util;

public class ReverseStep extends Module {

    private final Setting speed = this.register(new Setting("Speed", Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(20)));

    public ReverseStep() {
        super("ReverseStep", "Go down", Module.Category.MOVEMENT, true, false, false);
    }

    public void onUpdate() {
        if (!Feature.fullNullCheck() && !Util.mc.player.isInWater() && !Util.mc.player.isInLava() && !Util.mc.player.isOnLadder()) {
            if (Util.mc.player.onGround) {
                Util.mc.player.motionY -= (double) ((float) ((Integer) this.speed.getValue()).intValue() / 10.0F);
            }

        }
    }

    public void onDisable() {
        super.onDisable();
        Util.mc.player.motionY = 0.0D;
    }
}
