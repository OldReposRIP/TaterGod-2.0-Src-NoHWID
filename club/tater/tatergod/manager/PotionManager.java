package club.tater.tatergod.manager;

import club.tater.tatergod.features.Feature;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionManager extends Feature {

    private final Map potions = new ConcurrentHashMap();

    public List getOwnPotions() {
        return this.getPlayerPotions(PotionManager.mc.player);
    }

    public List getPlayerPotions(EntityPlayer player) {
        PotionManager.PotionList list = (PotionManager.PotionList) this.potions.get(player);
        Object potions = new ArrayList();

        if (list != null) {
            potions = list.getEffects();
        }

        return (List) potions;
    }

    public PotionEffect[] getImportantPotions(EntityPlayer player) {
        PotionEffect[] array = new PotionEffect[3];
        Iterator iterator = this.getPlayerPotions(player).iterator();

        while (iterator.hasNext()) {
            PotionEffect effect = (PotionEffect) iterator.next();
            Potion potion = effect.getPotion();
            String s = I18n.format(potion.getName(), new Object[0]).toLowerCase();
            byte b0 = -1;

            switch (s.hashCode()) {
            case -736186929:
                if (s.equals("weakness")) {
                    b0 = 1;
                }
                break;

            case 109641799:
                if (s.equals("speed")) {
                    b0 = 2;
                }
                break;

            case 1791316033:
                if (s.equals("strength")) {
                    b0 = 0;
                }
            }

            switch (b0) {
            case 0:
                array[0] = effect;

            case 1:
                array[1] = effect;

            case 2:
                array[2] = effect;
            }
        }

        return array;
    }

    public String getPotionString(PotionEffect effect) {
        Potion potion = effect.getPotion();

        return I18n.format(potion.getName(), new Object[0]) + " " + (effect.getAmplifier() + 1) + " " + ChatFormatting.WHITE + Potion.getPotionDurationString(effect, 1.0F);
    }

    public String getColoredPotionString(PotionEffect effect) {
        return this.getPotionString(effect);
    }

    public static class PotionList {

        private final List effects = new ArrayList();

        public void addEffect(PotionEffect effect) {
            if (effect != null) {
                this.effects.add(effect);
            }

        }

        public List getEffects() {
            return this.effects;
        }
    }
}
