package club.tater.tatergod.features.modules.misc;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;

public class PearlNotify extends Module {

    private final HashMap list = new HashMap();
    private Entity enderPearl;
    private boolean flag;

    public PearlNotify() {
        super("PearlResolver", "Notify pearl throws.", Module.Category.MISC, true, false, false);
    }

    public void onEnable() {
        this.flag = true;
    }

    public void onUpdate() {
        if (PearlNotify.mc.world != null && PearlNotify.mc.player != null) {
            this.enderPearl = null;
            Iterator closestPlayer = PearlNotify.mc.world.loadedEntityList.iterator();

            while (closestPlayer.hasNext()) {
                Entity faceing = (Entity) closestPlayer.next();

                if (faceing instanceof EntityEnderPearl) {
                    this.enderPearl = faceing;
                    break;
                }
            }

            if (this.enderPearl == null) {
                this.flag = true;
            } else {
                EntityPlayer closestPlayer1 = null;
                Iterator faceing1 = PearlNotify.mc.world.playerEntities.iterator();

                while (faceing1.hasNext()) {
                    EntityPlayer entity = (EntityPlayer) faceing1.next();

                    if (closestPlayer1 == null) {
                        closestPlayer1 = entity;
                    } else if (closestPlayer1.getDistance(this.enderPearl) > entity.getDistance(this.enderPearl)) {
                        closestPlayer1 = entity;
                    }
                }

                if (closestPlayer1 == PearlNotify.mc.player) {
                    this.flag = false;
                }

                if (closestPlayer1 != null && this.flag) {
                    String faceing2 = this.enderPearl.getHorizontalFacing().toString();

                    if (faceing2.equals("west")) {
                        faceing2 = "east";
                    } else if (faceing2.equals("east")) {
                        faceing2 = "west";
                    }

                    Command.sendSilentMessage(Tater.friendManager.isFriend(closestPlayer1.getName()) ? ChatFormatting.AQUA + closestPlayer1.getName() + ChatFormatting.DARK_GRAY + " has just thrown a pearl heading " + faceing2 + "!" : ChatFormatting.RED + closestPlayer1.getName() + ChatFormatting.DARK_GRAY + " has just thrown a pearl heading " + faceing2 + "!");
                    this.flag = false;
                }

            }
        }
    }
}
