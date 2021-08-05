package club.tater.tatergod.util;

import club.tater.tatergod.Tater;
import net.minecraft.client.Minecraft;

public class FontUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float drawStringWithShadow(boolean customFont, String text, int x, int y, int color) {
        return customFont ? Tater.fontRenderer.drawStringWithShadow(text, (double) x, (double) y, color) : (float) FontUtils.mc.fontRenderer.drawStringWithShadow(text, (float) x, (float) y, color);
    }

    public static int getStringWidth(boolean customFont, String str) {
        return customFont ? Tater.fontRenderer.getStringWidth(str) : FontUtils.mc.fontRenderer.getStringWidth(str);
    }

    public static int getFontHeight(boolean customFont) {
        return customFont ? Tater.fontRenderer.getHeight() : FontUtils.mc.fontRenderer.FONT_HEIGHT;
    }

    public static float drawKeyStringWithShadow(boolean customFont, String text, int x, int y, int color) {
        return customFont ? Tater.fontRenderer.drawStringWithShadow(text, (double) x, (double) y, color) : (float) FontUtils.mc.fontRenderer.drawStringWithShadow(text, (float) x, (float) y, color);
    }
}
