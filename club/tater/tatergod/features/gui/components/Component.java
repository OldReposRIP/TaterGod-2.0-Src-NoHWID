package club.tater.tatergod.features.gui.components;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.Feature;
import club.tater.tatergod.features.gui.components.items.Item;
import club.tater.tatergod.features.gui.components.items.buttons.Button;
import club.tater.tatergod.features.modules.client.ClickGui;
import club.tater.tatergod.util.ColorUtil;
import club.tater.tatergod.util.RenderUtil;
import club.tater.tatergod.util.Util;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.SoundEvents;

public class Component extends Feature {

    public static int[] counter1 = new int[] { 1};
    private final ArrayList items = new ArrayList();
    public boolean drag;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;
    private boolean hidden = false;

    public Component(String name, int x, int y, boolean open) {
        super(name);
        this.x = x;
        this.y = y;
        this.width = 88;
        this.height = 18;
        this.open = open;
        this.setupItems();
    }

    public void setupItems() {}

    private void drag(int mouseX, int mouseY) {
        if (this.drag) {
            this.x = this.x2 + mouseX;
            this.y = this.y2 + mouseY;
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drag(mouseX, mouseY);
        Component.counter1 = new int[] { 1};
        float totalItemHeight = this.open ? this.getTotalItemHeight() - 2.0F : 0.0F;
        int color = ColorUtil.toARGB(((Integer) ClickGui.getInstance().topRed.getValue()).intValue(), ((Integer) ClickGui.getInstance().topGreen.getValue()).intValue(), ((Integer) ClickGui.getInstance().topBlue.getValue()).intValue(), 255);

        Gui.drawRect(this.x, this.y - 1, this.x + this.width, this.y + this.height - 6, ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : color);
        if (this.open) {
            RenderUtil.drawRect((float) this.x, (float) this.y + 12.5F, (float) (this.x + this.width), (float) (this.y + this.height) + totalItemHeight, 1996488704);
        }

        Tater.textManager.drawStringWithShadow(this.getName(), (float) this.x + 3.0F, (float) this.y - 4.0F - (float) club.tater.tatergod.features.gui.Gui.getClickGui().getTextOffset(), -1);
        if (this.open) {
            float y = (float) (this.getY() + this.getHeight()) - 3.0F;
            Iterator iterator = this.getItems().iterator();

            while (iterator.hasNext()) {
                Item item = (Item) iterator.next();

                ++Component.counter1[0];
                if (!item.isHidden()) {
                    item.setLocation((float) this.x + 2.0F, y);
                    item.setWidth(this.getWidth() - 4);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                    y += (float) item.getHeight() + 1.5F;
                }
            }
        }

    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.x2 = this.x - mouseX;
            this.y2 = this.y - mouseY;
            club.tater.tatergod.features.gui.Gui.getClickGui().getComponents().forEach((component) -> {
                // $FF: Couldn't be decompiled
            });
            this.drag = true;
        } else if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
            this.open = !this.open;
            Util.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        } else if (this.open) {
            this.getItems().forEach((item) -> {
                item.mouseClicked(mouseX, mouseY, mouseButton);
            });
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0) {
            this.drag = false;
        }

        if (this.open) {
            this.getItems().forEach((item) -> {
                item.mouseReleased(mouseX, mouseY, releaseButton);
            });
        }
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.open) {
            this.getItems().forEach((item) -> {
                item.onKeyTyped(typedChar, keyCode);
            });
        }
    }

    public void addButton(Button button) {
        this.items.add(button);
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isOpen() {
        return this.open;
    }

    public final ArrayList getItems() {
        return this.items;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
    }

    private float getTotalItemHeight() {
        float height = 0.0F;

        Item item;

        for (Iterator iterator = this.getItems().iterator(); iterator.hasNext(); height += (float) item.getHeight() + 1.5F) {
            item = (Item) iterator.next();
        }

        return height;
    }
}
