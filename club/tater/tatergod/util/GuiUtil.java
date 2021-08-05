package club.tater.tatergod.util;

import club.tater.tatergod.Tater;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiUtil {

    public static void drawString(String nameString, float nameFloat1, float nameFloat2, int nameInt) {
        if (Tater.moduleManager.getModuleByName("CustomFont").isEnabled()) {
            Tater.fontRenderer.drawStringWithShadow(nameString, (double) nameFloat1, (double) nameFloat2, nameInt);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(nameString, nameFloat1, nameFloat2, nameInt);
        }

    }

    public static int getStringWidth(String nameString) {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(nameString);
    }

    public static void drawHorizontalLine(int nameInt1, int nameInt2, int nameInt3, int nameInt4) {
        if (nameInt2 < nameInt1) {
            int i = nameInt1;

            nameInt1 = nameInt2;
            nameInt2 = i;
        }

        drawRect(nameInt1, nameInt3, nameInt2 + 1, nameInt3 + 1, nameInt4);
    }

    public static void drawString(String nameString, int nameInt1, int nameInt2, int nameInt3) {
        if (Tater.moduleManager.getModuleByName("CustomFont").isEnabled()) {
            Tater.fontRenderer.drawStringWithShadow(nameString, (double) nameInt1, (double) nameInt2, nameInt3);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(nameString, (float) nameInt1, (float) nameInt2, nameInt3);
        }

    }

    public static String getCFont() {
        return Tater.fontRenderer.getFont().getFamily();
    }

    public static void drawRect(int nameInt1, int nameInt2, int nameInt3, int nameInt4, int nameInt5) {
        int f1;

        if (nameInt1 < nameInt3) {
            f1 = nameInt1;
            nameInt1 = nameInt3;
            nameInt3 = f1;
        }

        if (nameInt2 < nameInt4) {
            f1 = nameInt2;
            nameInt2 = nameInt4;
            nameInt4 = f1;
        }

        float f11 = (float) (nameInt5 >> 24 & 255) / 255.0F;
        float f2 = (float) (nameInt5 >> 16 & 255) / 255.0F;
        float f3 = (float) (nameInt5 >> 8 & 255) / 255.0F;
        float f4 = (float) (nameInt5 & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        GlStateManager.color(f2, f3, f4, f11);
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferBuilder.pos((double) nameInt1, (double) nameInt4, 0.0D).endVertex();
        bufferBuilder.pos((double) nameInt3, (double) nameInt4, 0.0D).endVertex();
        bufferBuilder.pos((double) nameInt3, (double) nameInt2, 0.0D).endVertex();
        bufferBuilder.pos((double) nameInt1, (double) nameInt2, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static int getHeight() {
        return Tater.moduleManager.getModuleByName("CustomFont").isEnabled() ? Tater.fontRenderer.getHeight() : Tater.fontRenderer.getHeight();
    }

    public static void drawVerticalLine(int nameInt1, int nameInt2, int nameInt3, int nameInt4) {
        if (nameInt3 < nameInt2) {
            int i = nameInt2;

            nameInt2 = nameInt3;
            nameInt3 = i;
        }

        drawRect(nameInt1, nameInt2 + 1, nameInt1 + 1, nameInt3, nameInt4);
    }

    public static void drawCenteredString(String nameString, int nameInt1, int nameInt2, int nameInt3) {
        if (Tater.moduleManager.getModuleByName("CustomFont").isEnabled()) {
            Tater.fontRenderer.drawStringWithShadow(nameString, (double) (nameInt1 - Tater.fontRenderer.getStringWidth(nameString) / 2), (double) nameInt2, nameInt3);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(nameString, (float) (nameInt1 - Tater.fontRenderer.getStringWidth(nameString) / 2), (float) nameInt2, nameInt3);
        }

    }
}
