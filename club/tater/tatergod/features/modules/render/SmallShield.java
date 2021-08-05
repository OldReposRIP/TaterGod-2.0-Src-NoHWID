package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;

public class SmallShield extends Module {

    private static SmallShield INSTANCE = new SmallShield();
    public Setting offX = this.register(new Setting("OffHandX", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
    public Setting offY = this.register(new Setting("OffHandY", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
    public Setting mainX = this.register(new Setting("MainHandX", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
    public Setting mainY = this.register(new Setting("MainHandY", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));

    public SmallShield() {
        super("SmallShield", "Makes you offhand lower.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static SmallShield getINSTANCE() {
        if (SmallShield.INSTANCE == null) {
            SmallShield.INSTANCE = new SmallShield();
        }

        return SmallShield.INSTANCE;
    }

    private void setInstance() {
        SmallShield.INSTANCE = this;
    }
}
