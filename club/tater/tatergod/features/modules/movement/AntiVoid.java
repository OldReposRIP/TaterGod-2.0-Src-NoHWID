package club.tater.tatergod.features.modules.movement;

import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.Util;
import com.mojang.realmsclient.gui.ChatFormatting;

public class AntiVoid extends Module {

    public Setting mode;
    public Setting display;

    public AntiVoid() {
        super("AntiVoid", "Glitches you up from void.", Module.Category.MOVEMENT, false, false, false);
        this.mode = this.register(new Setting("Mode", AntiVoid.Mode.BOUNCE));
        this.display = this.register(new Setting("Display", Boolean.valueOf(true)));
    }

    public void onUpdate() {
        double yLevel = Util.mc.player.posY;

        if (yLevel <= 0.5D) {
            Command.sendMessage(ChatFormatting.RED + "Player " + ChatFormatting.GREEN + Util.mc.player.getName() + ChatFormatting.RED + " is in the void!");
            if (((AntiVoid.Mode) this.mode.getValue()).equals(AntiVoid.Mode.BOUNCE)) {
                Util.mc.player.moveVertical = 10.0F;
                Util.mc.player.jump();
            }

            if (((AntiVoid.Mode) this.mode.getValue()).equals(AntiVoid.Mode.LAUNCH)) {
                Util.mc.player.moveVertical = 100.0F;
                Util.mc.player.jump();
            }
        } else {
            Util.mc.player.moveVertical = 0.0F;
        }

    }

    public void onDisable() {
        Util.mc.player.moveVertical = 0.0F;
    }

    public String getDisplayInfo() {
        if (((Boolean) this.display.getValue()).booleanValue()) {
            if (((AntiVoid.Mode) this.mode.getValue()).equals(AntiVoid.Mode.BOUNCE)) {
                return "Bounce";
            }

            if (((AntiVoid.Mode) this.mode.getValue()).equals(AntiVoid.Mode.LAUNCH)) {
                return "Launch";
            }
        }

        return null;
    }

    public static enum Mode {

        BOUNCE, LAUNCH;
    }
}
