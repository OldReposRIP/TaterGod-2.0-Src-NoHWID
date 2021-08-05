package club.tater.tatergod.features.modules.movement;

import club.tater.tatergod.features.modules.Module;

public class Speed extends Module {

    public Speed() {
        super("Speed", "Speed.", Module.Category.MOVEMENT, true, false, false);
    }

    public String getDisplayInfo() {
        return "Strafe";
    }
}
