package club.tater.tatergod.util;

import java.util.Random;
import net.minecraft.client.Minecraft;

public interface Nigger {

    Minecraft mc = Minecraft.getMinecraft();
    Random random = new Random();
    char SECTIONSIGN = '§';

    default boolean nullCheck() {
        return Nigger.mc.player == null || Nigger.mc.world == null;
    }
}
