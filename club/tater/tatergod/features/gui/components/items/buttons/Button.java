package club.tater.tatergod.features.gui.components.items.buttons;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.gui.Gui;
import club.tater.tatergod.features.gui.components.Component;
import club.tater.tatergod.features.gui.components.items.Item;
import club.tater.tatergod.features.modules.client.ClickGui;
import club.tater.tatergod.util.RenderUtil;
import club.tater.tatergod.util.Util;
import java.util.Iterator;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Button extends Item {

    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width, this.y + (float) this.height - 0.5F, this.getState() ? (!this.isHovering(mouseX, mouseY) ? Tater.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Tater.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Tater.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Tater.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())) : (!this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
        Tater.textManager.drawStringWithShadow(this.getName(), this.x + 2.3F, this.y - 2.0F - (float) Gui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }

    }

    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        Util.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void toggle() {}

    public boolean getState() {
        return this.state;
    }

    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        Iterator iterator = Gui.getClickGui().getComponents().iterator();

        while (iterator.hasNext()) {
            Component component = (Component) iterator.next();

            if (component.drag) {
                return false;
            }
        }

        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }
}
