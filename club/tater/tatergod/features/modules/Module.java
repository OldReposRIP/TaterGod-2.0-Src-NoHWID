package club.tater.tatergod.features.modules;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.ClientEvent;
import club.tater.tatergod.event.events.Render2DEvent;
import club.tater.tatergod.event.events.Render3DEvent;
import club.tater.tatergod.features.Feature;
import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.client.HUD;
import club.tater.tatergod.features.setting.Bind;
import club.tater.tatergod.features.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;

public class Module extends Feature {

    private final String description;
    private final Module.Category category;
    public Setting enabled = this.register(new Setting("Enabled", Boolean.valueOf(false)));
    public Setting drawn = this.register(new Setting("Drawn", Boolean.valueOf(true)));
    public Setting bind = this.register(new Setting("Keybind", new Bind(-1)));
    public Setting displayName;
    public boolean hasListener;
    public boolean alwaysListening;
    public boolean hidden;
    public float arrayListOffset = 0.0F;
    public float arrayListVOffset = 0.0F;
    public float offset;
    public float vOffset;
    public boolean sliding;

    public Module(String name, String description, Module.Category category, boolean hasListener, boolean hidden, boolean alwaysListening) {
        super(name);
        this.displayName = this.register(new Setting("DisplayName", name));
        this.description = description;
        this.category = category;
        this.hasListener = hasListener;
        this.hidden = hidden;
        this.alwaysListening = alwaysListening;
    }

    public boolean isSliding() {
        return this.sliding;
    }

    public void onEnable() {}

    public void onDisable() {}

    public void onToggle() {}

    public void onLoad() {}

    public void onTick() {}

    public void onLogin() {}

    public void onLogout() {}

    public void onUpdate() {}

    public void onRender2D(Render2DEvent event) {}

    public void onRender3D(Render3DEvent event) {}

    public void onUnload() {}

    public String getDisplayInfo() {
        return null;
    }

    public boolean isOn() {
        return ((Boolean) this.enabled.getValue()).booleanValue();
    }

    public boolean isOff() {
        return !((Boolean) this.enabled.getValue()).booleanValue();
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.enable();
        } else {
            this.disable();
        }

    }

    public void enable() {
        this.enabled.setValue(Boolean.TRUE);
        this.onToggle();
        this.onEnable();
        if (((Boolean) HUD.getInstance().notifyToggles.getValue()).booleanValue()) {
            TextComponentString text = new TextComponentString(Tater.commandManager.getClientMessage() + " " + ChatFormatting.GREEN + this.getDisplayName() + " toggled on.");

            Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        }

        if (this.isOn() && this.hasListener && !this.alwaysListening) {
            MinecraftForge.EVENT_BUS.register(this);
        }

    }

    public void disable() {
        if (this.hasListener && !this.alwaysListening) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }

        this.enabled.setValue(Boolean.valueOf(false));
        if (((Boolean) HUD.getInstance().notifyToggles.getValue()).booleanValue()) {
            TextComponentString text = new TextComponentString(Tater.commandManager.getClientMessage() + " " + ChatFormatting.RED + this.getDisplayName() + " toggled off.");

            Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        }

        this.onToggle();
        this.onDisable();
    }

    public void toggle() {
        ClientEvent event = new ClientEvent(!this.isEnabled() ? 1 : 0, this);

        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            this.setEnabled(!this.isEnabled());
        }

    }

    public String getDisplayName() {
        return (String) this.displayName.getValue();
    }

    public void setDisplayName(String name) {
        Module module = Tater.moduleManager.getModuleByDisplayName(name);
        Module originalModule = Tater.moduleManager.getModuleByName(name);

        if (module == null && originalModule == null) {
            Command.sendMessage(this.getDisplayName() + ", name: " + this.getName() + ", has been renamed to: " + name);
            this.displayName.setValue(name);
        } else {
            Command.sendMessage(ChatFormatting.RED + "A module of this name already exists.");
        }
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isDrawn() {
        return ((Boolean) this.drawn.getValue()).booleanValue();
    }

    public void setDrawn(boolean drawn) {
        this.drawn.setValue(Boolean.valueOf(drawn));
    }

    public Module.Category getCategory() {
        return this.category;
    }

    public String getInfo() {
        return null;
    }

    public Bind getBind() {
        return (Bind) this.bind.getValue();
    }

    public void setBind(int key) {
        this.bind.setValue(new Bind(key));
    }

    public boolean listening() {
        return this.hasListener && this.isOn() || this.alwaysListening;
    }

    public String getFullArrayString() {
        return this.getDisplayName() + ChatFormatting.GRAY + (this.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + this.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
    }

    public static enum Category {

        COMBAT("Combat"), MISC("Misc"), RENDER("Render"), MOVEMENT("Movement"), PLAYER("Player"), CLIENT("Client");

        private final String name;

        private Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
