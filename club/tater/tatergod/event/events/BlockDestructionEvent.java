package club.tater.tatergod.event.events;

import club.tater.tatergod.event.EventStage;
import net.minecraft.util.math.BlockPos;

public class BlockDestructionEvent extends EventStage {

    BlockPos nigger;

    public BlockDestructionEvent(BlockPos nigger) {}

    public BlockPos getBlockPos() {
        return this.nigger;
    }
}
