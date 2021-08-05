package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;

public class HandChams extends Module {

    private static HandChams INSTANCE = new HandChams();
    public Setting mode;
    public Setting red;
    public Setting green;
    public Setting blue;
    public Setting alpha;

    public HandChams() {
        super("HandChams", "Changes your hand color.", Module.Category.RENDER, false, false, false);
        this.mode = this.register(new Setting("Mode", HandChams.RenderMode.SOLID));
        this.red = this.register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
        this.green = this.register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
        this.blue = this.register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
        this.alpha = this.register(new Setting("Alpha", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(255)));
        this.setInstance();
    }

    public static HandChams getINSTANCE() {
        if (HandChams.INSTANCE == null) {
            HandChams.INSTANCE = new HandChams();
        }

        return HandChams.INSTANCE;
    }

    private void setInstance() {
        HandChams.INSTANCE = this;
    }

    public static enum RenderMode {

        SOLID, WIREFRAME;
    }
}
