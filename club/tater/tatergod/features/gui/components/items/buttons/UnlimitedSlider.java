package club.tater.tatergod.features.gui.components.items.buttons;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.gui.Gui;
import club.tater.tatergod.features.modules.client.ClickGui;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class UnlimitedSlider extends Button {

    public Setting setting;

    public UnlimitedSlider(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4F, this.y + (float) this.height - 0.5F, !this.isHovering(mouseX, mouseY) ? Tater.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Tater.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Tater.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Tater.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue()));
        Tater.textManager.drawStringWithShadow(" - " + this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getValue() + ChatFormatting.WHITE + " +", this.x + 2.3F, this.y - 1.7F - (float) Gui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            UnlimitedSlider.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if (this.isRight(mouseX)) {
                if (this.setting.getValue() instanceof Double) {
                    this.setting.setValue(Double.valueOf(((Double) this.setting.getValue()).doubleValue() + 1.0D));
                } else if (this.setting.getValue() instanceof Float) {
                    this.setting.setValue(Float.valueOf(((Float) this.setting.getValue()).floatValue() + 1.0F));
                } else if (this.setting.getValue() instanceof Integer) {
                    this.setting.setValue(Integer.valueOf(((Integer) this.setting.getValue()).intValue() + 1));
                }
            } else if (this.setting.getValue() instanceof Double) {
                this.setting.setValue(Double.valueOf(((Double) this.setting.getValue()).doubleValue() - 1.0D));
            } else if (this.setting.getValue() instanceof Float) {
                this.setting.setValue(Float.valueOf(((Float) this.setting.getValue()).floatValue() - 1.0F));
            } else if (this.setting.getValue() instanceof Integer) {
                this.setting.setValue(Integer.valueOf(((Integer) this.setting.getValue()).intValue() - 1));
            }
        }

    }

    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    public int getHeight() {
        return 14;
    }

    public void toggle() {}

    public boolean getState() {
        return true;
    }

    public boolean isRight(int x) {
        return (float) x > this.x + ((float) this.width + 7.4F) / 2.0F;
    }
}
