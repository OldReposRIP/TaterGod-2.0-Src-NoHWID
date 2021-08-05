package club.tater.tatergod.features.modules.client;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.ClientEvent;
import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.gui.Gui;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.Util;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickGui extends Module {

    private static ClickGui INSTANCE = new ClickGui();
    public Setting prefix = this.register(new Setting("Prefix", "."));
    public Setting customFov = this.register(new Setting("CustomFov", Boolean.valueOf(false)));
    public Setting fov = this.register(new Setting("Fov", Float.valueOf(150.0F), Float.valueOf(-180.0F), Float.valueOf(180.0F)));
    public Setting red = this.register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting green = this.register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting blue = this.register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting hoverAlpha = this.register(new Setting("Alpha", Integer.valueOf(180), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting topRed = this.register(new Setting("SecondRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting topGreen = this.register(new Setting("SecondGreen", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting topBlue = this.register(new Setting("SecondBlue", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting alpha = this.register(new Setting("HoverAlpha", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting rainbow = this.register(new Setting("Rainbow", Boolean.valueOf(false)));
    public Setting rainbowModeHud;
    public Setting rainbowModeA;
    public Setting rainbowHue;
    public Setting rainbowBrightness;
    public Setting rainbowSaturation;
    private Gui click;

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
        this.rainbowModeHud = this.register(new Setting("HRainbowMode", ClickGui.rainbowMode.Static, test<invokedynamic>(this)));
        this.rainbowModeA = this.register(new Setting("ARainbowMode", ClickGui.rainbowModeArray.Static, test<invokedynamic>(this)));
        this.rainbowHue = this.register(new Setting("Delay", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(600), test<invokedynamic>(this)));
        this.rainbowBrightness = this.register(new Setting("Brightness ", Float.valueOf(150.0F), Float.valueOf(1.0F), Float.valueOf(255.0F), test<invokedynamic>(this)));
        this.rainbowSaturation = this.register(new Setting("Saturation", Float.valueOf(150.0F), Float.valueOf(1.0F), Float.valueOf(255.0F), test<invokedynamic>(this)));
        this.setInstance();
    }

    public static ClickGui getInstance() {
        if (ClickGui.INSTANCE == null) {
            ClickGui.INSTANCE = new ClickGui();
        }

        return ClickGui.INSTANCE;
    }

    private void setInstance() {
        ClickGui.INSTANCE = this;
    }

    public void onUpdate() {
        if (((Boolean) this.customFov.getValue()).booleanValue()) {
            ClickGui.mc.gameSettings.setOptionFloatValue(Options.FOV, ((Float) this.fov.getValue()).floatValue());
        }

    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                Tater.commandManager.setPrefix((String) this.prefix.getPlannedValue());
                Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + Tater.commandManager.getPrefix());
            }

            Tater.colorManager.setColor(((Integer) this.red.getPlannedValue()).intValue(), ((Integer) this.green.getPlannedValue()).intValue(), ((Integer) this.blue.getPlannedValue()).intValue(), ((Integer) this.hoverAlpha.getPlannedValue()).intValue());
        }

    }

    public void onEnable() {
        Util.mc.displayGuiScreen(Gui.getClickGui());
    }

    public void onLoad() {
        Tater.colorManager.setColor(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.hoverAlpha.getValue()).intValue());
        Tater.commandManager.setPrefix((String) this.prefix.getValue());
    }

    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof Gui)) {
            this.disable();
        }

    }

    private boolean lambda$new$4(Object v) {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }

    private boolean lambda$new$3(Object v) {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }

    private boolean lambda$new$2(Object v) {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }

    private boolean lambda$new$1(Object v) {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }

    private boolean lambda$new$0(Object v) {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }

    public static enum rainbowMode {

        Static, Sideway;
    }

    public static enum rainbowModeArray {

        Static, Up;
    }
}
