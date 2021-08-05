package club.tater.tatergod.features.gui.components.items.buttons;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.gui.Gui;
import club.tater.tatergod.features.gui.components.Component;
import club.tater.tatergod.features.modules.client.ClickGui;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Iterator;
import org.lwjgl.input.Mouse;

public class Slider extends Button {

    private final Number min;
    private final Number max;
    private final int difference;
    public Setting setting;

    public Slider(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.min = (Number) setting.getMin();
        this.max = (Number) setting.getMax();
        this.difference = this.max.intValue() - this.min.intValue();
        this.width = 15;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.dragSetting(mouseX, mouseY);
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4F, this.y + (float) this.height - 0.5F, !this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515);
        RenderUtil.drawRect(this.x, this.y, ((Number) this.setting.getValue()).floatValue() <= this.min.floatValue() ? this.x : this.x + ((float) this.width + 7.4F) * this.partialMultiplier(), this.y + (float) this.height - 0.5F, !this.isHovering(mouseX, mouseY) ? Tater.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Tater.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Tater.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Tater.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue()));
        Tater.textManager.drawStringWithShadow(this.getName() + " " + ChatFormatting.GRAY + (this.setting.getValue() instanceof Float ? this.setting.getValue() : Double.valueOf(((Number) this.setting.getValue()).doubleValue())), this.x + 2.3F, this.y - 1.7F - (float) Gui.getClickGui().getTextOffset(), -1);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            this.setSettingFromX(mouseX);
        }

    }

    public boolean isHovering(int mouseX, int mouseY) {
        Iterator iterator = Gui.getClickGui().getComponents().iterator();

        while (iterator.hasNext()) {
            Component component = (Component) iterator.next();

            if (component.drag) {
                return false;
            }
        }

        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() + 8.0F && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }

    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    private void dragSetting(int mouseX, int mouseY) {
        if (this.isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            this.setSettingFromX(mouseX);
        }

    }

    public int getHeight() {
        return 14;
    }

    private void setSettingFromX(int mouseX) {
        float percent = ((float) mouseX - this.x) / ((float) this.width + 7.4F);

        if (this.setting.getValue() instanceof Double) {
            double result = ((Double) this.setting.getMin()).doubleValue() + (double) ((float) this.difference * percent);

            this.setting.setValue(Double.valueOf((double) Math.round(10.0D * result) / 10.0D));
        } else if (this.setting.getValue() instanceof Float) {
            float result1 = ((Float) this.setting.getMin()).floatValue() + (float) this.difference * percent;

            this.setting.setValue(Float.valueOf((float) Math.round(10.0F * result1) / 10.0F));
        } else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue(Integer.valueOf(((Integer) this.setting.getMin()).intValue() + (int) ((float) this.difference * percent)));
        }

    }

    private float middle() {
        return this.max.floatValue() - this.min.floatValue();
    }

    private float part() {
        return ((Number) this.setting.getValue()).floatValue() - this.min.floatValue();
    }

    private float partialMultiplier() {
        return this.part() / this.middle();
    }
}
