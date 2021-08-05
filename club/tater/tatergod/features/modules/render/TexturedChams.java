package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;

public class TexturedChams extends Module {

    public static Setting red;
    public static Setting green;
    public static Setting blue;
    public static Setting alpha;

    public TexturedChams() {
        super("TexturedChams", "hi yes", Module.Category.RENDER, true, false, true);
        TexturedChams.red = this.register(new Setting("Red", Integer.valueOf(168), Integer.valueOf(0), Integer.valueOf(255)));
        TexturedChams.green = this.register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
        TexturedChams.blue = this.register(new Setting("Blue", Integer.valueOf(232), Integer.valueOf(0), Integer.valueOf(255)));
        TexturedChams.alpha = this.register(new Setting("Alpha", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255)));
    }
}
