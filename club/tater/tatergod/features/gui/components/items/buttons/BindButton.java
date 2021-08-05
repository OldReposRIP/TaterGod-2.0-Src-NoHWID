package club.tater.tatergod.features.gui.components.items.buttons;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.gui.Gui;
import club.tater.tatergod.features.modules.client.ClickGui;
import club.tater.tatergod.features.setting.Bind;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.ColorUtil;
import club.tater.tatergod.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindButton extends Button {

    private final Setting setting;
    public boolean isListening;

    public BindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int color = ColorUtil.toARGB(((Integer) ClickGui.getInstance().red.getValue()).intValue(), ((Integer) ClickGui.getInstance().green.getValue()).intValue(), ((Integer) ClickGui.getInstance().blue.getValue()).intValue(), 255);

        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4F, this.y + (float) this.height - 0.5F, this.getState() ? (!this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515) : (!this.isHovering(mouseX, mouseY) ? Tater.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Tater.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Tater.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Tater.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())));
        if (this.isListening) {
            Tater.textManager.drawStringWithShadow("Press a Key...", this.x + 2.3F, this.y - 1.7F - (float) Gui.getClickGui().getTextOffset(), -1);
        } else {
            Tater.textManager.drawStringWithShadow(this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getValue().toString().toUpperCase(), this.x + 2.3F, this.y - 1.7F - (float) Gui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }

    }

    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            BindButton.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);

            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }

            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }

            this.setting.setValue(bind);
            this.onMouseClick();
        }

    }

    public int getHeight() {
        return 14;
    }

    public void toggle() {
        this.isListening = !this.isListening;
    }

    public boolean getState() {
        return !this.isListening;
    }
}
