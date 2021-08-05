package club.tater.tatergod.manager;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.Feature;
import club.tater.tatergod.features.gui.font.CustomFont;
import club.tater.tatergod.features.modules.client.FontMod;
import club.tater.tatergod.util.MathUtil;
import club.tater.tatergod.util.Timer;
import java.awt.Color;
import java.awt.Font;
import net.minecraft.util.math.MathHelper;

public class TextManager extends Feature {

    private final Timer idleTimer = new Timer();
    public int scaledWidth;
    public int scaledHeight;
    public int scaleFactor;
    private CustomFont customFont = new CustomFont(new Font("Verdana", 0, 17), true, false);
    private boolean idling;

    public TextManager() {
        this.updateResolution();
    }

    public void init(boolean startup) {
        FontMod cFont = (FontMod) Tater.moduleManager.getModuleByClass(FontMod.class);

        try {
            this.setFontRenderer(new Font((String) cFont.fontName.getValue(), ((Integer) cFont.fontStyle.getValue()).intValue(), ((Integer) cFont.fontSize.getValue()).intValue()), ((Boolean) cFont.antiAlias.getValue()).booleanValue(), ((Boolean) cFont.fractionalMetrics.getValue()).booleanValue());
        } catch (Exception exception) {
            ;
        }

    }

    public void drawStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, x, y, color, true);
    }

    public float drawString(String text, float x, float y, int color, boolean shadow) {
        if (Tater.moduleManager.isModuleEnabled(FontMod.getInstance().getName())) {
            if (shadow) {
                this.customFont.drawStringWithShadow(text, (double) x, (double) y, color);
            } else {
                this.customFont.drawString(text, x, y, color);
            }

            return x;
        } else {
            TextManager.mc.fontRenderer.drawString(text, x, y, color, shadow);
            return x;
        }
    }

    public void drawRainbowString(String text, float x, float y, int startColor, float factor, boolean shadow) {
        Color currentColor = new Color(startColor);
        float hueIncrement = 1.0F / factor;
        String[] rainbowStrings = text.split("§.");
        float currentHue = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), (float[]) null)[0];
        float saturation = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), (float[]) null)[1];
        float brightness = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), (float[]) null)[2];
        int currentWidth = 0;
        boolean shouldRainbow = true;
        boolean shouldContinue = false;

        for (int i = 0; i < text.length(); ++i) {
            char currentChar = text.charAt(i);
            char nextChar = text.charAt(MathUtil.clamp(i + 1, 0, text.length() - 1));

            if ((String.valueOf(currentChar) + nextChar).equals("§r")) {
                shouldRainbow = false;
            } else if ((String.valueOf(currentChar) + nextChar).equals("§+")) {
                shouldRainbow = true;
            }

            if (shouldContinue) {
                shouldContinue = false;
            } else {
                if ((String.valueOf(currentChar) + nextChar).equals("§r")) {
                    String escapeString = text.substring(i);

                    this.drawString(escapeString, x + (float) currentWidth, y, Color.WHITE.getRGB(), shadow);
                    break;
                }

                this.drawString(String.valueOf(currentChar).equals("§") ? "" : String.valueOf(currentChar), x + (float) currentWidth, y, shouldRainbow ? currentColor.getRGB() : Color.WHITE.getRGB(), shadow);
                if (String.valueOf(currentChar).equals("§")) {
                    shouldContinue = true;
                }

                currentWidth += this.getStringWidth(String.valueOf(currentChar));
                if (!String.valueOf(currentChar).equals(" ")) {
                    currentColor = new Color(Color.HSBtoRGB(currentHue, saturation, brightness));
                    currentHue += hueIncrement;
                }
            }
        }

    }

    public int getStringWidth(String text) {
        return Tater.moduleManager.isModuleEnabled(FontMod.getInstance().getName()) ? this.customFont.getStringWidth(text) : TextManager.mc.fontRenderer.getStringWidth(text);
    }

    public int getFontHeight() {
        if (Tater.moduleManager.isModuleEnabled(FontMod.getInstance().getName())) {
            String text = "A";

            return this.customFont.getStringHeight(text);
        } else {
            return TextManager.mc.fontRenderer.FONT_HEIGHT;
        }
    }

    public void setFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        this.customFont = new CustomFont(font, antiAlias, fractionalMetrics);
    }

    public Font getCurrentFont() {
        return this.customFont.getFont();
    }

    public void updateResolution() {
        this.scaledWidth = TextManager.mc.displayWidth;
        this.scaledHeight = TextManager.mc.displayHeight;
        this.scaleFactor = 1;
        boolean flag = TextManager.mc.isUnicode();
        int i = TextManager.mc.gameSettings.guiScale;

        if (i == 0) {
            i = 1000;
        }

        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }

        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }

        double scaledWidthD = (double) (this.scaledWidth / this.scaleFactor);
        double scaledHeightD = (double) (this.scaledHeight / this.scaleFactor);

        this.scaledWidth = MathHelper.ceil(scaledWidthD);
        this.scaledHeight = MathHelper.ceil(scaledHeightD);
    }

    public String getIdleSign() {
        if (this.idleTimer.passedMs(500L)) {
            this.idling = !this.idling;
            this.idleTimer.reset();
        }

        return this.idling ? "_" : "";
    }
}
