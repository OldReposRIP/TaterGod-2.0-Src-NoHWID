package club.tater.tatergod.features.modules.misc;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;

public class NoHitBox extends Module {

    private static NoHitBox INSTANCE = new NoHitBox();
    public Setting pickaxe = this.register(new Setting("Pickaxe", Boolean.valueOf(true)));
    public Setting crystal = this.register(new Setting("Crystal", Boolean.valueOf(true)));
    public Setting gapple = this.register(new Setting("Gapple", Boolean.valueOf(false)));

    public NoHitBox() {
        super("NoHitBox", "NoHitBox.", Module.Category.MISC, false, false, false);
        this.setInstance();
    }

    public static NoHitBox getINSTANCE() {
        if (NoHitBox.INSTANCE == null) {
            NoHitBox.INSTANCE = new NoHitBox();
        }

        return NoHitBox.INSTANCE;
    }

    private void setInstance() {
        NoHitBox.INSTANCE = this;
    }
}
