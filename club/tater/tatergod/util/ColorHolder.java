package club.tater.tatergod.util;

import java.awt.Color;
import org.lwjgl.opengl.GL11;

public class ColorHolder {

    int r;
    int g;
    int b;
    int a;

    public ColorHolder(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 255;
    }

    public ColorHolder(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public static ColorHolder fromHex(int hex) {
        ColorHolder n = new ColorHolder(0, 0, 0);

        n.becomeHex(hex);
        return n;
    }

    public static int toHex(int r, int g, int b) {
        return -16777216 | (r & 255) << 16 | (g & 255) << 8 | b & 255;
    }

    public ColorHolder brighter() {
        return new ColorHolder(Math.min(this.r + 10, 255), Math.min(this.g + 10, 255), Math.min(this.b + 10, 255), this.getA());
    }

    public ColorHolder darker() {
        return new ColorHolder(Math.max(this.r - 10, 0), Math.max(this.g - 10, 0), Math.max(this.b - 10, 0), this.getA());
    }

    public void setGLColour() {
        this.setGLColour(-1, -1, -1, -1);
    }

    public void setGLColour(int dr, int dg, int db, int da) {
        GL11.glColor4f((float) (dr == -1 ? this.r : dr) / 255.0F, (float) (dg == -1 ? this.g : dg) / 255.0F, (float) (db == -1 ? this.b : db) / 255.0F, (float) (da == -1 ? this.a : da) / 255.0F);
    }

    public void becomeGLColour() {}

    public void becomeHex(int hex) {
        this.setR((hex & 16711680) >> 16);
        this.setG((hex & '\uff00') >> 8);
        this.setB(hex & 255);
        this.setA(255);
    }

    public int toHex() {
        return toHex(this.r, this.g, this.b);
    }

    public int getB() {
        return this.b;
    }

    public ColorHolder setB(int b) {
        this.b = b;
        return this;
    }

    public int getG() {
        return this.g;
    }

    public ColorHolder setG(int g) {
        this.g = g;
        return this;
    }

    public int getR() {
        return this.r;
    }

    public ColorHolder setR(int r) {
        this.r = r;
        return this;
    }

    public int getA() {
        return this.a;
    }

    public ColorHolder setA(int a) {
        this.a = a;
        return this;
    }

    public ColorHolder clone() {
        return new ColorHolder(this.r, this.g, this.b, this.a);
    }

    public Color toJavaColour() {
        return new Color(this.r, this.g, this.b, this.a);
    }
}
