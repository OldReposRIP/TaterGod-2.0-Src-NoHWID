package club.tater.tatergod.features.modules.client;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.ColorUtil;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class Colors extends Module {

    public static Colors INSTANCE;
    public Setting rainbow = this.register(new Setting("Rainbow", Boolean.valueOf(false), "Rainbow colors."));
    public Setting rainbowSpeed = this.register(new Setting("Speed", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(100), (v) -> {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting rainbowSaturation = this.register(new Setting("Saturation", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting rainbowBrightness = this.register(new Setting("Brightness", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting red = this.register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return !((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return !((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting blue = this.register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return !((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return !((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public float hue;
    public Map colorHeightMap = new HashMap();

    public Colors() {
        super("Colors", "Universal colors.", Module.Category.CLIENT, true, false, true);
        Colors.INSTANCE = this;
    }

    public void onTick() {
        int colorSpeed = 101 - ((Integer) this.rainbowSpeed.getValue()).intValue();
        float tempHue = this.hue = (float) (System.currentTimeMillis() % (long) (360 * colorSpeed)) / (360.0F * (float) colorSpeed);

        for (int i = 0; i <= 510; ++i) {
            this.colorHeightMap.put(Integer.valueOf(i), Integer.valueOf(Color.HSBtoRGB(tempHue, (float) ((Integer) this.rainbowSaturation.getValue()).intValue() / 255.0F, (float) ((Integer) this.rainbowBrightness.getValue()).intValue() / 255.0F)));
            tempHue += 0.0013071896F;
        }

    }

    public int getCurrentColorHex() {
        return ((Boolean) this.rainbow.getValue()).booleanValue() ? Color.HSBtoRGB(this.hue, (float) ((Integer) this.rainbowSaturation.getValue()).intValue() / 255.0F, (float) ((Integer) this.rainbowBrightness.getValue()).intValue() / 255.0F) : ColorUtil.toARGB(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue());
    }

    public Color getCurrentColor() {
        return ((Boolean) this.rainbow.getValue()).booleanValue() ? Color.getHSBColor(this.hue, (float) ((Integer) this.rainbowSaturation.getValue()).intValue() / 255.0F, (float) ((Integer) this.rainbowBrightness.getValue()).intValue() / 255.0F) : new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue());
    }
}
