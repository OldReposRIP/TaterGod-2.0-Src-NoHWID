package club.tater.tatergod.features;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.gui.Gui;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.manager.TextManager;
import club.tater.tatergod.util.Util;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Feature implements Util {

    public List settings = new ArrayList();
    public TextManager renderer;
    private String name;

    public Feature() {
        this.renderer = Tater.textManager;
    }

    public Feature(String name) {
        this.renderer = Tater.textManager;
        this.name = name;
    }

    public static boolean nullCheck() {
        return Feature.mc.player == null;
    }

    public static boolean fullNullCheck() {
        return Feature.mc.player == null || Feature.mc.world == null;
    }

    public String getName() {
        return this.name;
    }

    public List getSettings() {
        return this.settings;
    }

    public boolean hasSettings() {
        return !this.settings.isEmpty();
    }

    public boolean isEnabled() {
        return this instanceof Module ? ((Module) this).isOn() : false;
    }

    public boolean isDisabled() {
        return !this.isEnabled();
    }

    public Setting register(Setting setting) {
        setting.setFeature(this);
        this.settings.add(setting);
        if (this instanceof Module && Feature.mc.currentScreen instanceof Gui) {
            Gui.getInstance().updateModule((Module) this);
        }

        return setting;
    }

    public void unregister(Setting settingIn) {
        ArrayList removeList = new ArrayList();
        Iterator iterator = this.settings.iterator();

        while (iterator.hasNext()) {
            Setting setting = (Setting) iterator.next();

            if (setting.equals(settingIn)) {
                removeList.add(setting);
            }
        }

        if (!removeList.isEmpty()) {
            this.settings.removeAll(removeList);
        }

        if (this instanceof Module && Feature.mc.currentScreen instanceof Gui) {
            Gui.getInstance().updateModule((Module) this);
        }

    }

    public Setting getSettingByName(String name) {
        Iterator iterator = this.settings.iterator();

        Setting setting;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            setting = (Setting) iterator.next();
        } while (!setting.getName().equalsIgnoreCase(name));

        return setting;
    }

    public void reset() {
        Iterator iterator = this.settings.iterator();

        while (iterator.hasNext()) {
            Setting setting = (Setting) iterator.next();

            setting.setValue(setting.getDefaultValue());
        }

    }

    public void clearSettings() {
        this.settings = new ArrayList();
    }
}
