package club.tater.tatergod.util;

import java.awt.Color;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class RenderMethods {

    public static void drawGradientRect(float nameFloat1, float nameFloat2, float nameFloat3, float nameFloat4, int nameInt1, int nameInt2) {
        enableGL2D();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        glColor(nameInt1);
        GL11.glVertex2f(nameFloat1, nameFloat4);
        GL11.glVertex2f(nameFloat3, nameFloat4);
        glColor(nameInt2);
        GL11.glVertex2f(nameFloat3, nameFloat2);
        GL11.glVertex2f(nameFloat1, nameFloat2);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        disableGL2D();
    }

    public static void drawBorderedRect(float nameFloat1, float nameFloat2, float nameFloat3, float nameFloat4, int nameInt1, int nameInt2) {
        enableGL2D();
        nameFloat1 *= 2.0F;
        nameFloat3 *= 2.0F;
        nameFloat2 *= 2.0F;
        nameFloat4 *= 2.0F;
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        drawVLine(nameFloat1, nameFloat2, nameFloat4 - 1.0F, nameInt2);
        drawVLine(nameFloat3 - 1.0F, nameFloat2, nameFloat4, nameInt2);
        drawHLine(nameFloat1, nameFloat3 - 1.0F, nameFloat2, nameInt2);
        drawHLine(nameFloat1, nameFloat3 - 2.0F, nameFloat4 - 1.0F, nameInt2);
        drawRect(nameFloat1 + 1.0F, nameFloat2 + 1.0F, nameFloat3 - 1.0F, nameFloat4 - 1.0F, nameInt1);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        disableGL2D();
    }

    public static void drawOutlinedBox(AxisAlignedBB nameAxisAlignedBB) {
        if (nameAxisAlignedBB != null) {
            GL11.glBegin(3);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glEnd();
            GL11.glBegin(3);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glEnd();
            GL11.glBegin(1);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glEnd();
        }
    }

    public static void drawBorderedRectReliant(float nameFloat1, float nameFloat2, float nameFloat3, float nameFloat4, float nameFloat5, int nameInt1, int nameInt2) {
        enableGL2D();
        drawRect(nameFloat1, nameFloat2, nameFloat3, nameFloat4, nameInt1);
        glColor(nameInt2);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(nameFloat5);
        GL11.glBegin(3);
        GL11.glVertex2f(nameFloat1, nameFloat2);
        GL11.glVertex2f(nameFloat1, nameFloat4);
        GL11.glVertex2f(nameFloat3, nameFloat4);
        GL11.glVertex2f(nameFloat3, nameFloat2);
        GL11.glVertex2f(nameFloat1, nameFloat2);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        disableGL2D();
    }

    public static void drawStrip(int nameInt1, int nameInt2, float nameFloat1, double nameDouble, float nameFloat2, float nameFloat3, int nameInt3) {
        float f1 = (float) (nameInt3 >> 24 & 255) / 255.0F;
        float f2 = (float) (nameInt3 >> 16 & 255) / 255.0F;
        float f3 = (float) (nameInt3 >> 8 & 255) / 255.0F;
        float f4 = (float) (nameInt3 & 255) / 255.0F;

        GL11.glPushMatrix();
        GL11.glTranslated((double) nameInt1, (double) nameInt2, 0.0D);
        GL11.glColor4f(f2, f3, f4, f1);
        GL11.glLineWidth(nameFloat1);
        byte b;
        float f5;
        float f6;
        float f7;

        if (nameDouble > 0.0D) {
            GL11.glBegin(3);

            for (b = 0; (double) b < nameDouble; ++b) {
                f5 = (float) ((double) b * nameDouble * 3.141592653589793D / (double) nameFloat2);
                f6 = (float) (Math.cos((double) f5) * (double) nameFloat3);
                f7 = (float) (Math.sin((double) f5) * (double) nameFloat3);
                GL11.glVertex2f(f6, f7);
            }

            GL11.glEnd();
        }

        if (nameDouble < 0.0D) {
            GL11.glBegin(3);

            for (b = 0; (double) b > nameDouble; --b) {
                f5 = (float) ((double) b * nameDouble * 3.141592653589793D / (double) nameFloat2);
                f6 = (float) (Math.cos((double) f5) * (double) (-nameFloat3));
                f7 = (float) (Math.sin((double) f5) * (double) (-nameFloat3));
                GL11.glVertex2f(f6, f7);
            }

            GL11.glEnd();
        }

        disableGL2D();
        GL11.glDisable(3479);
        GL11.glPopMatrix();
    }

    public static void enableGL3D() {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4353);
        GL11.glDisable(2896);
    }

    public static void drawBorderedRect(float nameFloat1, float nameFloat2, float nameFloat3, float nameFloat4, float nameFloat5, int nameInt1, int nameInt2) {
        enableGL2D();
        glColor(nameInt1);
        drawRect(nameFloat1 + nameFloat5, nameFloat2 + nameFloat5, nameFloat3 - nameFloat5, nameFloat4 - nameFloat5);
        glColor(nameInt2);
        drawRect(nameFloat1 + nameFloat5, nameFloat2, nameFloat3 - nameFloat5, nameFloat2 + nameFloat5);
        drawRect(nameFloat1, nameFloat2, nameFloat1 + nameFloat5, nameFloat4);
        drawRect(nameFloat3 - nameFloat5, nameFloat2, nameFloat3, nameFloat4);
        drawRect(nameFloat1 + nameFloat5, nameFloat4 - nameFloat5, nameFloat3 - nameFloat5, nameFloat4);
        disableGL2D();
    }

    public static void glColor(Color nameColor) {
        GL11.glColor4f((float) nameColor.getRed() / 255.0F, (float) nameColor.getGreen() / 255.0F, (float) nameColor.getBlue() / 255.0F, (float) nameColor.getAlpha() / 255.0F);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void drawBox(AxisAlignedBB nameAxisAlignedBB) {
        if (nameAxisAlignedBB != null) {
            GL11.glBegin(7);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
            GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
            GL11.glEnd();
        }
    }

    public static void drawGradientBorderedRectReliant(float nameFloat1, float nameFloat2, float nameFloat3, float nameFloat4, float nameFloat5, int nameInt1, int nameInt2, int nameInt3) {
        enableGL2D();
        drawGradientRect(nameFloat1, nameFloat2, nameFloat3, nameFloat4, nameInt3, nameInt2);
        glColor(nameInt1);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(nameFloat5);
        GL11.glBegin(3);
        GL11.glVertex2f(nameFloat1, nameFloat2);
        GL11.glVertex2f(nameFloat1, nameFloat4);
        GL11.glVertex2f(nameFloat3, nameFloat4);
        GL11.glVertex2f(nameFloat3, nameFloat2);
        GL11.glVertex2f(nameFloat1, nameFloat2);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        disableGL2D();
    }

    public static int applyTexture(int nameInt1, int nameInt2, int nameInt3, ByteBuffer nameByteBuffer, boolean nameBoolean1, boolean nameBoolean2) {
        GL11.glBindTexture(3553, nameInt1);
        GL11.glTexParameteri(3553, 10241, nameBoolean1 ? 9729 : 9728);
        GL11.glTexParameteri(3553, 10240, nameBoolean1 ? 9729 : 9728);
        GL11.glTexParameteri(3553, 10242, nameBoolean2 ? 10497 : 10496);
        GL11.glTexParameteri(3553, 10243, nameBoolean2 ? 10497 : 10496);
        GL11.glPixelStorei(3317, 1);
        GL11.glTexImage2D(3553, 0, 'è?˜', nameInt2, nameInt3, 0, 6408, 5121, nameByteBuffer);
        return nameInt1;
    }

    public static void disableGL3D() {
        GL11.glEnable(2896);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
    }

    public static void enableGL3D(float nameFloat) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        Minecraft.getMinecraft().entityRenderer.enableLightmap();
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(nameFloat);
    }

    public static void drawRoundedRect(float nameFloat1, float nameFloat2, float nameFloat3, float nameFloat4, int nameInt1, int nameInt2) {
        enableGL2D();
        nameFloat1 *= 2.0F;
        nameFloat2 *= 2.0F;
        nameFloat3 *= 2.0F;
        nameFloat4 *= 2.0F;
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        drawVLine(nameFloat1, nameFloat2 + 1.0F, nameFloat4 - 2.0F, nameInt1);
        drawVLine(nameFloat3 - 1.0F, nameFloat2 + 1.0F, nameFloat4 - 2.0F, nameInt1);
        drawHLine(nameFloat1 + 2.0F, nameFloat3 - 3.0F, nameFloat2, nameInt1);
        drawHLine(nameFloat1 + 2.0F, nameFloat3 - 3.0F, nameFloat4 - 1.0F, nameInt1);
        drawHLine(nameFloat1 + 1.0F, nameFloat1 + 1.0F, nameFloat2 + 1.0F, nameInt1);
        drawHLine(nameFloat3 - 2.0F, nameFloat3 - 2.0F, nameFloat2 + 1.0F, nameInt1);
        drawHLine(nameFloat3 - 2.0F, nameFloat3 - 2.0F, nameFloat4 - 2.0F, nameInt1);
        drawHLine(nameFloat1 + 1.0F, nameFloat1 + 1.0F, nameFloat4 - 2.0F, nameInt1);
        drawRect(nameFloat1 + 1.0F, nameFloat2 + 1.0F, nameFloat3 - 1.0F, nameFloat4 - 1.0F, nameInt2);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        disableGL2D();
    }

    public static double getDiff(double nameDouble1, double nameDouble2, float nameFloat, double nameDouble3) {
        return nameDouble1 + (nameDouble2 - nameDouble1) * (double) nameFloat - nameDouble3;
    }

    public static void drawRect(float nameFloat1, float nameFloat2, float nameFloat3, float nameFloat4) {
        GL11.glBegin(7);
        GL11.glVertex2f(nameFloat1, nameFloat4);
        GL11.glVertex2f(nameFloat3, nameFloat4);
        GL11.glVertex2f(nameFloat3, nameFloat2);
        GL11.glVertex2f(nameFloat1, nameFloat2);
        GL11.glEnd();
    }

    public static void renderCrosses(AxisAlignedBB nameAxisAlignedBB) {
        GL11.glBegin(1);
        GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
        GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
        GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
        GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
        GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
        GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
        GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
        GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.maxZ);
        GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
        GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.maxY, nameAxisAlignedBB.minZ);
        GL11.glVertex3d(nameAxisAlignedBB.minX, nameAxisAlignedBB.minY, nameAxisAlignedBB.minZ);
        GL11.glVertex3d(nameAxisAlignedBB.maxX, nameAxisAlignedBB.minY, nameAxisAlignedBB.maxZ);
        GL11.glEnd();
    }

    public static void glColor(int nameInt) {
        float f1 = (float) (nameInt >> 24 & 255) / 255.0F;
        float f2 = (float) (nameInt >> 16 & 255) / 255.0F;
        float f3 = (float) (nameInt >> 8 & 255) / 255.0F;
        float f4 = (float) (nameInt & 255) / 255.0F;

        GL11.glColor4f(f2, f3, f4, f1);
    }

    public static void drawHLine(float nameFloat1, float nameFloat2, float nameFloat3, int nameInt) {
        if (nameFloat2 < nameFloat1) {
            float f = nameFloat1;

            nameFloat1 = nameFloat2;
            nameFloat2 = f;
        }

        drawRect(nameFloat1, nameFloat3, nameFloat2 + 1.0F, nameFloat3 + 1.0F, nameInt);
    }

    public static void drawLine(float nameFloat1, float nameFloat2, float nameFloat3, float nameFloat4, float nameFloat5) {
        GL11.glDisable(3553);
        GL11.glLineWidth(nameFloat5);
        GL11.glBegin(1);
        GL11.glVertex2f(nameFloat1, nameFloat2);
        GL11.glVertex2f(nameFloat3, nameFloat4);
        GL11.glEnd();
        GL11.glEnable(3553);
    }

    public static void drawRect(float nameFloat1, float nameFloat2, float nameFloat3, float nameFloat4, int nameInt) {
        enableGL2D();
        glColor(nameInt);
        drawRect(nameFloat1, nameFloat2, nameFloat3, nameFloat4);
        disableGL2D();
    }

    public static void drawGradientHRect(float nameFloat1, float nameFloat2, float nameFloat3, float nameFloat4, int nameInt1, int nameInt2) {
        enableGL2D();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        glColor(nameInt1);
        GL11.glVertex2f(nameFloat1, nameFloat2);
        GL11.glVertex2f(nameFloat1, nameFloat4);
        glColor(nameInt2);
        GL11.glVertex2f(nameFloat3, nameFloat4);
        GL11.glVertex2f(nameFloat3, nameFloat2);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        disableGL2D();
    }

    public static void rectangle(double nameDouble1, double nameDouble2, double nameDouble3, double nameDouble4, int nameInt) {
        double f1;

        if (nameDouble1 < nameDouble3) {
            f1 = nameDouble1;
            nameDouble1 = nameDouble3;
            nameDouble3 = f1;
        }

        if (nameDouble2 < nameDouble4) {
            f1 = nameDouble2;
            nameDouble2 = nameDouble4;
            nameDouble4 = f1;
        }

        float f11 = (float) (nameInt >> 24 & 255) / 255.0F;
        float f2 = (float) (nameInt >> 16 & 255) / 255.0F;
        float f3 = (float) (nameInt >> 8 & 255) / 255.0F;
        float f4 = (float) (nameInt & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f2, f3, f4, f11);
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        bufferBuilder.pos(nameDouble1, nameDouble4, 0.0D);
        bufferBuilder.pos(nameDouble3, nameDouble4, 0.0D);
        bufferBuilder.pos(nameDouble3, nameDouble2, 0.0D);
        bufferBuilder.pos(nameDouble1, nameDouble2, 0.0D);
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void drawFullCircle(int nameInt1, int nameInt2, double nameDouble, int nameInt3) {
        nameDouble *= 2.0D;
        nameInt1 *= 2;
        nameInt2 *= 2;
        float f1 = (float) (nameInt3 >> 24 & 255) / 255.0F;
        float f2 = (float) (nameInt3 >> 16 & 255) / 255.0F;
        float f3 = (float) (nameInt3 >> 8 & 255) / 255.0F;
        float f4 = (float) (nameInt3 & 255) / 255.0F;

        enableGL2D();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glColor4f(f2, f3, f4, f1);
        GL11.glBegin(6);

        for (byte b = 0; b <= 360; ++b) {
            double d1 = Math.sin((double) b * 3.141592653589793D / 180.0D) * nameDouble;
            double d2 = Math.cos((double) b * 3.141592653589793D / 180.0D) * nameDouble;

            GL11.glVertex2d((double) nameInt1 + d1, (double) nameInt2 + d2);
        }

        GL11.glEnd();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        disableGL2D();
    }

    public static void drawTriangle(int nameInt1, int nameInt2, int nameInt3, int nameInt4, int nameInt5) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        float f1 = (float) (nameInt5 >> 24 & 255) / 255.0F;
        float f2 = (float) (nameInt5 >> 16 & 255) / 255.0F;
        float f3 = (float) (nameInt5 >> 8 & 255) / 255.0F;
        float f4 = (float) (nameInt5 & 255) / 255.0F;

        GL11.glColor4f(f2, f3, f4, f1);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0F);
        GL11.glShadeModel(7425);
        switch (nameInt3) {
        case 0:
            GL11.glBegin(2);
            GL11.glVertex2d((double) nameInt1, (double) (nameInt2 + nameInt4));
            GL11.glVertex2d((double) (nameInt1 + nameInt4), (double) (nameInt2 - nameInt4));
            GL11.glVertex2d((double) (nameInt1 - nameInt4), (double) (nameInt2 - nameInt4));
            GL11.glEnd();
            GL11.glBegin(4);
            GL11.glVertex2d((double) nameInt1, (double) (nameInt2 + nameInt4));
            GL11.glVertex2d((double) (nameInt1 + nameInt4), (double) (nameInt2 - nameInt4));
            GL11.glVertex2d((double) (nameInt1 - nameInt4), (double) (nameInt2 - nameInt4));
            GL11.glEnd();
            break;

        case 1:
            GL11.glBegin(2);
            GL11.glVertex2d((double) nameInt1, (double) nameInt2);
            GL11.glVertex2d((double) nameInt1, (double) (nameInt2 + nameInt4 / 2));
            GL11.glVertex2d((double) (nameInt1 + nameInt4 + nameInt4 / 2), (double) nameInt2);
            GL11.glEnd();
            GL11.glBegin(4);
            GL11.glVertex2d((double) nameInt1, (double) nameInt2);
            GL11.glVertex2d((double) nameInt1, (double) (nameInt2 + nameInt4 / 2));
            GL11.glVertex2d((double) (nameInt1 + nameInt4 + nameInt4 / 2), (double) nameInt2);
            GL11.glEnd();

        case 2:
        default:
            break;

        case 3:
            GL11.glBegin(2);
            GL11.glVertex2d((double) nameInt1, (double) nameInt2);
            GL11.glVertex2d((double) nameInt1 + (double) nameInt4 * 1.25D, (double) (nameInt2 - nameInt4 / 2));
            GL11.glVertex2d((double) nameInt1 + (double) nameInt4 * 1.25D, (double) (nameInt2 + nameInt4 / 2));
            GL11.glEnd();
            GL11.glBegin(4);
            GL11.glVertex2d((double) nameInt1 + (double) nameInt4 * 1.25D, (double) (nameInt2 - nameInt4 / 2));
            GL11.glVertex2d((double) nameInt1, (double) nameInt2);
            GL11.glVertex2d((double) nameInt1 + (double) nameInt4 * 1.25D, (double) (nameInt2 + nameInt4 / 2));
            GL11.glEnd();
        }

        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static void drawGradientRect(double nameDouble1, double nameDouble2, double nameDouble3, double nameDouble4, int nameInt1, int nameInt2) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        glColor(nameInt1);
        GL11.glVertex2d(nameDouble3, nameDouble2);
        GL11.glVertex2d(nameDouble1, nameDouble2);
        glColor(nameInt2);
        GL11.glVertex2d(nameDouble1, nameDouble4);
        GL11.glVertex2d(nameDouble3, nameDouble4);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static Color rainbow(long nameLong, float nameFloat) {
        float f = (float) (System.nanoTime() + nameLong) / 1.0E10F % 1.0F;
        long l = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(f, 1.0F, 1.0F)), 16);
        Color color = new Color((int) l);

        return new Color((float) color.getRed() / 255.0F * nameFloat, (float) color.getGreen() / 255.0F * nameFloat, (float) color.getBlue() / 255.0F * nameFloat, (float) color.getAlpha() / 255.0F);
    }

    public static void drawRect(float nameFloat1, float nameFloat2, float nameFloat3, float nameFloat4, float nameFloat5, float nameFloat6, float nameFloat7, float nameFloat8) {
        enableGL2D();
        GL11.glColor4f(nameFloat5, nameFloat6, nameFloat7, nameFloat8);
        drawRect(nameFloat1, nameFloat2, nameFloat3, nameFloat4);
        disableGL2D();
    }

    public static void glColor(float nameFloat, int nameInt1, int nameInt2, int nameInt3) {
        float f1 = 0.003921569F * (float) nameInt1;
        float f2 = 0.003921569F * (float) nameInt2;
        float f3 = 0.003921569F * (float) nameInt3;

        GL11.glColor4f(f1, f2, f3, nameFloat);
    }

    public static void drawRect(Rectangle nameRectangle, int nameInt) {
        drawRect((float) nameRectangle.x, (float) nameRectangle.y, (float) (nameRectangle.x + nameRectangle.width), (float) (nameRectangle.y + nameRectangle.height), nameInt);
    }

    public static Color blend(Color nameColor1, Color nameColor2, float nameFloat) {
        float f = 1.0F - nameFloat;
        float[] arrayOfFloat1 = new float[3];
        float[] arrayOfFloat2 = new float[3];

        nameColor1.getColorComponents(arrayOfFloat1);
        nameColor2.getColorComponents(arrayOfFloat2);
        return new Color(arrayOfFloat1[0] * nameFloat + arrayOfFloat2[0] * f, arrayOfFloat1[1] * nameFloat + arrayOfFloat2[1] * f, arrayOfFloat1[2] * nameFloat + arrayOfFloat2[2] * f);
    }

    public static void drawHLine(float nameFloat1, float nameFloat2, float nameFloat3, int nameInt1, int nameInt2) {
        if (nameFloat2 < nameFloat1) {
            float f = nameFloat1;

            nameFloat1 = nameFloat2;
            nameFloat2 = f;
        }

        drawGradientRect(nameFloat1, nameFloat3, nameFloat2 + 1.0F, nameFloat3 + 1.0F, nameInt1, nameInt2);
    }

    public static void drawBorderedRect(Rectangle nameRectangle, float nameFloat, int nameInt1, int nameInt2) {
        float f1 = (float) nameRectangle.x;
        float f2 = (float) nameRectangle.y;
        float f3 = (float) (nameRectangle.x + nameRectangle.width);
        float f4 = (float) (nameRectangle.y + nameRectangle.height);

        enableGL2D();
        glColor(nameInt1);
        drawRect(f1 + nameFloat, f2 + nameFloat, f3 - nameFloat, f4 - nameFloat);
        glColor(nameInt2);
        drawRect(f1 + 1.0F, f2, f3 - 1.0F, f2 + nameFloat);
        drawRect(f1, f2, f1 + nameFloat, f4);
        drawRect(f3 - nameFloat, f2, f3, f4);
        drawRect(f1 + 1.0F, f4 - nameFloat, f3 - 1.0F, f4);
        disableGL2D();
    }

    public static void drawGradientBorderedRect(double nameDouble1, double nameDouble2, double nameDouble3, double nameDouble4, float nameFloat, int nameInt1, int nameInt2, int nameInt3) {
        enableGL2D();
        GL11.glPushMatrix();
        glColor(nameInt1);
        GL11.glLineWidth(1.0F);
        GL11.glBegin(1);
        GL11.glVertex2d(nameDouble1, nameDouble2);
        GL11.glVertex2d(nameDouble1, nameDouble4);
        GL11.glVertex2d(nameDouble3, nameDouble4);
        GL11.glVertex2d(nameDouble3, nameDouble2);
        GL11.glVertex2d(nameDouble1, nameDouble2);
        GL11.glVertex2d(nameDouble3, nameDouble2);
        GL11.glVertex2d(nameDouble1, nameDouble4);
        GL11.glVertex2d(nameDouble3, nameDouble4);
        GL11.glEnd();
        GL11.glPopMatrix();
        drawGradientRect(nameDouble1, nameDouble2, nameDouble3, nameDouble4, nameInt2, nameInt3);
        disableGL2D();
    }

    public static void drawCircle(float nameFloat1, float nameFloat2, float nameFloat3, int nameInt1, int nameInt2) {
        nameFloat3 *= 2.0F;
        nameFloat1 *= 2.0F;
        nameFloat2 *= 2.0F;
        float f1 = (float) (nameInt2 >> 24 & 255) / 255.0F;
        float f2 = (float) (nameInt2 >> 16 & 255) / 255.0F;
        float f3 = (float) (nameInt2 >> 8 & 255) / 255.0F;
        float f4 = (float) (nameInt2 & 255) / 255.0F;
        float f5 = (float) (6.2831852D / (double) nameInt1);
        float f6 = (float) Math.cos((double) f5);
        float f7 = (float) Math.sin((double) f5);
        float f8 = nameFloat3;
        float f9 = 0.0F;

        enableGL2D();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glColor4f(f2, f3, f4, f1);
        GL11.glBegin(2);

        for (byte b = 0; b < nameInt1; ++b) {
            GL11.glVertex2f(f8 + nameFloat1, f9 + nameFloat2);
            float f = f8;

            f8 = f6 * f8 - f7 * f9;
            f9 = f7 * f + f6 * f9;
        }

        GL11.glEnd();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        disableGL2D();
    }

    public static void drawVLine(float nameFloat1, float nameFloat2, float nameFloat3, int nameInt) {
        if (nameFloat3 < nameFloat2) {
            float f = nameFloat2;

            nameFloat2 = nameFloat3;
            nameFloat3 = f;
        }

        drawRect(nameFloat1, nameFloat2 + 1.0F, nameFloat1 + 1.0F, nameFloat3, nameInt);
    }
}
