package club.tater.tatergod.features.gui.components.items.buttons;

import club.tater.tatergod.features.gui.components.Component;
import club.tater.tatergod.features.gui.components.items.Item;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Bind;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.Util;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ModuleButton extends Button {

    private final Module module;
    private final ResourceLocation logo = new ResourceLocation("textures/tatergod.png");
    private List items = new ArrayList();
    private boolean subOpen;

    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
        this.initSettings();
    }

    public static void drawCompleteImage(float posX, float posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0F);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(0.0F, (float) height, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f((float) width, (float) height, 0.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f((float) width, 0.0F, 0.0F);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void initSettings() {
        ArrayList newItems = new ArrayList();

        if (!this.module.getSettings().isEmpty()) {
            Iterator iterator = this.module.getSettings().iterator();

            while (iterator.hasNext()) {
                Setting setting = (Setting) iterator.next();

                if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled")) {
                    newItems.add(new BooleanButton(setting));
                }

                if (setting.getValue() instanceof Bind && !setting.getName().equalsIgnoreCase("Keybind") && !this.module.getName().equalsIgnoreCase("Hud")) {
                    newItems.add(new BindButton(setting));
                }

                if ((setting.getValue() instanceof String || setting.getValue() instanceof Character) && !setting.getName().equalsIgnoreCase("displayName")) {
                    newItems.add(new StringButton(setting));
                }

                if (setting.isNumberSetting() && setting.hasRestriction()) {
                    newItems.add(new Slider(setting));
                } else if (setting.isEnumSetting()) {
                    newItems.add(new EnumButton(setting));
                }
            }
        }

        newItems.add(new BindButton(this.module.getSettingByName("Keybind")));
        this.items = newItems;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!this.items.isEmpty() && this.subOpen) {
            float height = 1.0F;

            Item item;

            for (Iterator iterator = this.items.iterator(); iterator.hasNext(); item.update()) {
                item = (Item) iterator.next();
                ++Component.counter1[0];
                if (!item.isHidden()) {
                    item.setLocation(this.x + 1.0F, this.y + (height += 15.0F));
                    item.setHeight(15);
                    item.setWidth(this.width - 9);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                }
            }
        }

    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                Util.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

            if (this.subOpen) {
                Iterator iterator = this.items.iterator();

                while (iterator.hasNext()) {
                    Item item = (Item) iterator.next();

                    if (!item.isHidden()) {
                        item.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }

    }

    public void onKeyTyped(char typedChar, int keyCode) {
        super.onKeyTyped(typedChar, keyCode);
        if (!this.items.isEmpty() && this.subOpen) {
            Iterator iterator = this.items.iterator();

            while (iterator.hasNext()) {
                Item item = (Item) iterator.next();

                if (!item.isHidden()) {
                    item.onKeyTyped(typedChar, keyCode);
                }
            }
        }

    }

    public int getHeight() {
        if (this.subOpen) {
            int height = 14;
            Iterator iterator = this.items.iterator();

            while (iterator.hasNext()) {
                Item item = (Item) iterator.next();

                if (!item.isHidden()) {
                    height += item.getHeight() + 1;
                }
            }

            return height + 2;
        } else {
            return 14;
        }
    }

    public Module getModule() {
        return this.module;
    }

    public void toggle() {
        this.module.toggle();
    }

    public boolean getState() {
        return this.module.isEnabled();
    }
}
