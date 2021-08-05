package club.tater.tatergod.features.modules.movement;

import club.tater.tatergod.event.events.MoveEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint extends Module {

    private static Sprint INSTANCE = new Sprint();
    public Setting mode;

    public Sprint() {
        super("Sprint", "Modifies sprinting", Module.Category.MOVEMENT, false, false, false);
        this.mode = this.register(new Setting("Mode", Sprint.Mode.LEGIT));
        this.setInstance();
    }

    public static Sprint getInstance() {
        if (Sprint.INSTANCE == null) {
            Sprint.INSTANCE = new Sprint();
        }

        return Sprint.INSTANCE;
    }

    private void setInstance() {
        Sprint.INSTANCE = this;
    }

    @SubscribeEvent
    public void onSprint(MoveEvent event) {
        if (event.getStage() == 1 && this.mode.getValue() == Sprint.Mode.RAGE && (Sprint.mc.player.movementInput.moveForward != 0.0F || Sprint.mc.player.movementInput.moveStrafe != 0.0F)) {
            event.setCanceled(true);
        }

    }

    public void onUpdate() {
        switch ((Sprint.Mode) this.mode.getValue()) {
        case RAGE:
            if ((Sprint.mc.gameSettings.keyBindForward.isKeyDown() || Sprint.mc.gameSettings.keyBindBack.isKeyDown() || Sprint.mc.gameSettings.keyBindLeft.isKeyDown() || Sprint.mc.gameSettings.keyBindRight.isKeyDown()) && !Sprint.mc.player.isSneaking() && !Sprint.mc.player.collidedHorizontally && (float) Sprint.mc.player.getFoodStats().getFoodLevel() > 6.0F) {
                Sprint.mc.player.setSprinting(true);
            }
            break;

        case LEGIT:
            if (Sprint.mc.gameSettings.keyBindForward.isKeyDown() && !Sprint.mc.player.isSneaking() && !Sprint.mc.player.isHandActive() && !Sprint.mc.player.collidedHorizontally && (float) Sprint.mc.player.getFoodStats().getFoodLevel() > 6.0F && Sprint.mc.currentScreen == null) {
                Sprint.mc.player.setSprinting(true);
            }
        }

    }

    public void onDisable() {
        if (!nullCheck()) {
            Sprint.mc.player.setSprinting(false);
        }

    }

    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }

    public static enum Mode {

        LEGIT, RAGE;
    }
}
