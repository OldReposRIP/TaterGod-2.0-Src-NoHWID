package club.tater.tatergod.event.events;

import club.tater.tatergod.event.EventStage;
import net.minecraft.entity.player.EntityPlayer;

public class DeathEvent extends EventStage {

    public EntityPlayer player;

    public DeathEvent(EntityPlayer player) {
        this.player = player;
    }
}
