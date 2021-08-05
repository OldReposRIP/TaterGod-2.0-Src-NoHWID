package club.tater.tatergod.features.gui;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.gui.components.Component;
import club.tater.tatergod.features.gui.components.items.Item;
import club.tater.tatergod.features.gui.components.items.buttons.ModuleButton;
import club.tater.tatergod.features.modules.Module;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class Gui extends GuiScreen {

    private static Gui Gui;
    private static Gui INSTANCE = new Gui();
    private final ArrayList components = new ArrayList();

    public Gui() {
        this.setInstance();
        this.load();
    }

    public static Gui getInstance() {
        if (Gui.INSTANCE == null) {
            Gui.INSTANCE = new Gui();
        }

        return Gui.INSTANCE;
    }

    public static Gui getClickGui() {
        return getInstance();
    }

    private void setInstance() {
        Gui.INSTANCE = this;
    }

    private void load() {
        final int x = -84;
        Iterator iterator = Tater.moduleManager.getCategories().iterator();

        while (iterator.hasNext()) {
            final Module.Category category = (Module.Category) iterator.next();
            ArrayList arraylist = this.components;
            final String s = category.getName();

            x += 90;
            arraylist.add(new Component(s, x, 4, true) {
                public void setupItems() {
                    null.counter1 = new int[] { 1};
                    Tater.moduleManager.getModulesByCategory(category).forEach((module) -> {
                        if (!module.hidden) {
                            this.addButton(new ModuleButton(module));
                        }

                    });
                }
            });
        }

        this.components.forEach(accept<invokedynamic>());
    }

    public void updateModule(Module module) {
        Iterator iterator = this.components.iterator();

        while (iterator.hasNext()) {
            Component component = (Component) iterator.next();
            Iterator iterator1 = component.getItems().iterator();

            while (iterator1.hasNext()) {
                Item item = (Item) iterator1.next();

                if (item instanceof ModuleButton) {
                    ModuleButton button = (ModuleButton) item;
                    Module mod = button.getModule();

                    if (module != null && module.equals(mod)) {
                        button.initSettings();
                    }
                }
            }
        }

    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.checkMouseWheel();
        this.drawDefaultBackground();
        this.components.forEach(accept<invokedynamic>(mouseX, mouseY, partialTicks));
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        this.components.forEach(accept<invokedynamic>(mouseX, mouseY, clickedButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        this.components.forEach(accept<invokedynamic>(mouseX, mouseY, releaseButton));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList getComponents() {
        return this.components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();

        if (dWheel < 0) {
            this.components.forEach(accept<invokedynamic>());
        } else if (dWheel > 0) {
            this.components.forEach(accept<invokedynamic>());
        }

    }

    public int getTextOffset() {
        return -6;
    }

    public Component getComponentByName(String name) {
        Iterator iterator = this.components.iterator();

        Component component;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            component = (Component) iterator.next();
        } while (!component.getName().equalsIgnoreCase(name));

        return component;
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.components.forEach(accept<invokedynamic>(typedChar, keyCode));
    }

    private static void lambda$keyTyped$6(char typedChar, int keyCode, Component component) {
        component.onKeyTyped(typedChar, keyCode);
    }

    private static void lambda$checkMouseWheel$5(Component component) {
        component.setY(component.getY() + 10);
    }

    private static void lambda$checkMouseWheel$4(Component component) {
        component.setY(component.getY() - 10);
    }

    private static void lambda$mouseReleased$3(int mouseX, int mouseY, int releaseButton, Component components) {
        components.mouseReleased(mouseX, mouseY, releaseButton);
    }

    private static void lambda$mouseClicked$2(int mouseX, int mouseY, int clickedButton, Component components) {
        components.mouseClicked(mouseX, mouseY, clickedButton);
    }

    private static void lambda$drawScreen$1(int mouseX, int mouseY, float partialTicks, Component components) {
        components.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static void lambda$load$0(Component components) {
        components.getItems().sort(Comparator.comparing(apply<invokedynamic>()));
    }
}
