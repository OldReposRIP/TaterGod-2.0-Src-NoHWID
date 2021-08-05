package club.tater.tatergod.features.modules.misc;

import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.SoundEvents;

public class GhastNotifier extends Module {

    private final Set ghasts = new HashSet();
    public Setting Chat = this.register(new Setting("Chat", Boolean.valueOf(true)));
    public Setting Sound = this.register(new Setting("Sound", Boolean.valueOf(true)));

    public GhastNotifier() {
        super("GhastNotifier", "Helps you find ghasts", Module.Category.MISC, true, false, false);
    }

    public void onEnable() {
        this.ghasts.clear();
    }

    public void onUpdate() {
        Iterator iterator = GhastNotifier.mc.world.getLoadedEntityList().iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityGhast && !this.ghasts.contains(entity)) {
                if (((Boolean) this.Chat.getValue()).booleanValue()) {
                    Command.sendMessage("Ghast Detected at: " + entity.getPosition().getX() + "x, " + entity.getPosition().getY() + "y, " + entity.getPosition().getZ() + "z.");
                }

                this.ghasts.add(entity);
                if (((Boolean) this.Sound.getValue()).booleanValue()) {
                    GhastNotifier.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0F, 1.0F);
                }
            }
        }

    }
}
