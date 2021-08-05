package com.llamalad7.betterchat.gui;

import com.llamalad7.betterchat.BetterChat;
import com.llamalad7.betterchat.ChatSettings;
import com.llamalad7.betterchat.handlers.InjectHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GuiConfig extends GuiScreen {

    private final ChatSettings settings = BetterChat.getSettings();
    private final List exampleChat = new ArrayList();
    private boolean dragging = false;
    private int chatLeft;
    private int chatRight;
    private int chatTop;
    private int chatBottom;
    private int dragStartX;
    private int dragStartY;
    private GuiButton clearButton;
    private GuiButton smoothButton;
    private GuiSlider scaleSlider;
    private GuiSlider widthSlider;

    public GuiConfig() {
        this.exampleChat.add(new TextComponentString(I18n.format("gui.betterchat.text.example3", new Object[0])));
        this.exampleChat.add(new TextComponentString(I18n.format("gui.betterchat.text.example2", new Object[0])));
        this.exampleChat.add(new TextComponentString(I18n.format("gui.betterchat.text.example1", new Object[0])));
    }

    public void initGui() {
        InjectHandler.chatGUI.configuring = true;
        this.buttonList.add(this.clearButton = new GuiButton(0, this.width / 2 - 120, this.height / 2 - 50, 240, 20, this.getPropName("clear") + " " + this.getColoredBool("clear", this.settings.clear)));
        this.buttonList.add(this.smoothButton = new GuiButton(1, this.width / 2 - 120, this.height / 2 - 25, 240, 20, this.getPropName("smooth") + " " + this.getColoredBool("smooth", this.settings.smooth)));
        this.buttonList.add(this.scaleSlider = new GuiSlider(3, this.width / 2 - 120, this.height / 2, 240, 20, this.getPropName("scale") + " ", "%", 0.0D, 100.0D, (double) (this.mc.gameSettings.chatScale * 100.0F), false, true));
        this.buttonList.add(this.widthSlider = new GuiSlider(4, this.width / 2 - 120, this.height / 2 + 25, 240, 20, this.getPropName("width") + " ", "px", 40.0D, 320.0D, (double) GuiNewChat.calculateChatboxWidth(this.mc.gameSettings.chatWidth), false, true));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 120, this.height / 2 + 50, 240, 20, this.getPropName("reset")));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.mc.fontRenderer, I18n.format("gui.betterchat.text.title", new Object[] { TextFormatting.GREEN + TextFormatting.BOLD.toString() + "Better Chat" + TextFormatting.RESET, TextFormatting.AQUA + TextFormatting.BOLD.toString() + "LlamaLad7"}), this.width / 2, this.height / 2 - 75, 16777215);
        this.drawCenteredString(this.mc.fontRenderer, I18n.format("gui.betterchat.text.drag", new Object[0]), this.width / 2, this.height / 2 - 63, 16777215);
        if (this.dragging) {
            this.settings.xOffset += mouseX - this.dragStartX;
            this.settings.yOffset += mouseY - this.dragStartY;
            this.dragStartX = mouseX;
            this.dragStartY = mouseY;
        }

        this.mc.gameSettings.chatScale = (float) this.scaleSlider.getValueInt() / 100.0F;
        this.mc.gameSettings.chatWidth = ((float) this.widthSlider.getValueInt() - 40.0F) / 280.0F;
        this.drawExampleChat();
    }

    public void drawExampleChat() {
        ArrayList lines = new ArrayList();
        int i = MathHelper.floor((float) InjectHandler.chatGUI.getChatWidth() / InjectHandler.chatGUI.getChatScale());
        Iterator scaledresolution = this.exampleChat.iterator();

        while (scaledresolution.hasNext()) {
            ITextComponent f = (ITextComponent) scaledresolution.next();

            lines.addAll(GuiUtilRenderComponents.splitText(f, i, this.mc.fontRenderer, false, false));
        }

        Collections.reverse(lines);
        GlStateManager.pushMatrix();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);

        GlStateManager.translate(2.0F + (float) this.settings.xOffset, 8.0F + (float) this.settings.yOffset + (float) scaledresolution.getScaledHeight() - 48.0F, 0.0F);
        float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
        float f1 = this.mc.gameSettings.chatScale;
        int k = MathHelper.ceil((float) InjectHandler.chatGUI.getChatWidth() / f1);

        GlStateManager.scale(f1, f1, 1.0F);
        int i1 = 0;
        double d0 = 1.0D;
        int l1 = (int) (255.0D * d0);

        l1 = (int) ((float) l1 * f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        this.chatLeft = this.settings.xOffset;
        this.chatRight = (int) ((float) this.settings.xOffset + (float) (k + 4) * f1);
        this.chatBottom = 8 + this.settings.yOffset + scaledresolution.getScaledHeight() - 48;

        for (Iterator iterator = lines.iterator(); iterator.hasNext(); ++i1) {
            ITextComponent message = (ITextComponent) iterator.next();
            int j2 = -i1 * 9;

            if (!this.settings.clear) {
                drawRect(-2, j2 - 9, k + 4, j2, l1 / 2 << 24);
            }

            this.mc.fontRenderer.drawStringWithShadow(message.getFormattedText(), 0.0F, (float) (j2 - 8), 16777215 + (l1 << 24));
        }

        this.chatTop = (int) ((float) (8 + this.settings.yOffset + scaledresolution.getScaledHeight() - 48) + (float) (-i1 * 9) * f1);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && mouseX >= this.chatLeft && mouseX <= this.chatRight && mouseY >= this.chatTop && mouseY <= this.chatBottom) {
            this.dragging = true;
            this.dragStartX = mouseX;
            this.dragStartY = mouseY;
        }

    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        this.dragging = false;
    }

    public void onGuiClosed() {
        this.settings.saveConfig();
        InjectHandler.chatGUI.configuring = false;
        this.mc.gameSettings.saveOptions();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
        case 0:
            this.settings.clear = !this.settings.clear;
            button.displayString = this.getPropName("clear") + " " + this.getColoredBool("clear", this.settings.clear);
            break;

        case 1:
            this.settings.smooth = !this.settings.smooth;
            button.displayString = this.getPropName("smooth") + " " + this.getColoredBool("smooth", this.settings.smooth);
            break;

        case 2:
            this.settings.resetConfig();
            this.clearButton.displayString = this.getPropName("clear") + " " + this.getColoredBool("clear", this.settings.clear);
            this.smoothButton.displayString = this.getPropName("smooth") + " " + this.getColoredBool("smooth", this.settings.smooth);
            this.scaleSlider.setValue((double) (this.mc.gameSettings.chatScale * 100.0F));
            this.scaleSlider.updateSlider();
            this.widthSlider.setValue((double) GuiNewChat.calculateChatboxWidth(this.mc.gameSettings.chatWidth));
            this.widthSlider.updateSlider();
        }

    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    private String getColoredBool(String prop, boolean bool) {
        return bool ? TextFormatting.GREEN + I18n.format("gui.betterchat.text." + prop + ".enabled", new Object[0]) : TextFormatting.RED + I18n.format("gui.betterchat.text." + prop + ".disabled", new Object[0]);
    }

    private String getPropName(String prop) {
        return I18n.format("gui.betterchat.text." + prop + ".name", new Object[0]);
    }
}
