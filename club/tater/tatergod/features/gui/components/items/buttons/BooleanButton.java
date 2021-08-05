package club.tater.tatergod.features.gui.components.items.buttons;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.gui.Gui;
import club.tater.tatergod.features.modules.client.ClickGui;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BooleanButton extends Button {

    private final Setting setting;

    public BooleanButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4F, this.y + (float) this.height - 0.5F, this.getState() ? (!this.isHovering(mouseX, mouseY) ? Tater.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Tater.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Tater.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Tater.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())) : (!this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
        Tater.textManager.drawStringWithShadow(this.getName(), this.x + 2.3F, this.y - 1.7F - (float) Gui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    }

    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            BooleanButton.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

    }

    public int getHeight() {
        return 14;
    }

    public void toggle() {
        this.setting.setValue(Boolean.valueOf(!((Boolean) this.setting.getValue()).booleanValue()));
    }

    public boolean getState() {
        return ((Boolean) this.setting.getValue()).booleanValue();
    }
}
