package club.tater.tatergod.manager;

import club.tater.tatergod.features.Feature;
import club.tater.tatergod.util.BlockUtil;
import club.tater.tatergod.util.EntityUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToDoubleFunction;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class HoleManager extends Feature {

    private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets(0, true));
    private final List midSafety = new ArrayList();
    private List holes = new ArrayList();

    public void update() {
        if (!fullNullCheck()) {
            this.holes = this.calcHoles();
        }

    }

    public List getHoles() {
        return this.holes;
    }

    public List getMidSafety() {
        return this.midSafety;
    }

    public List getSortedHoles() {
        this.holes.sort(Comparator.comparingDouble((hole) -> {
            return HoleManager.mc.player.getDistanceSq(hole);
        }));
        return this.getHoles();
    }

    public List calcHoles() {
        ArrayList safeSpots = new ArrayList();

        this.midSafety.clear();
        List positions = BlockUtil.getSphere(EntityUtil.getPlayerPos(HoleManager.mc.player), 6.0F, 6, false, true, 0);
        Iterator iterator = positions.iterator();

        while (iterator.hasNext()) {
            BlockPos pos = (BlockPos) iterator.next();

            if (HoleManager.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && HoleManager.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && HoleManager.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                boolean isSafe = true;
                boolean midSafe = true;
                BlockPos[] ablockpos = HoleManager.surroundOffset;
                int i = ablockpos.length;

                for (int j = 0; j < i; ++j) {
                    BlockPos offset = ablockpos[j];
                    Block block = HoleManager.mc.world.getBlockState(pos.add(offset)).getBlock();

                    if (BlockUtil.isBlockUnSolid(block)) {
                        midSafe = false;
                    }

                    if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                        isSafe = false;
                    }
                }

                if (isSafe) {
                    safeSpots.add(pos);
                }

                if (midSafe) {
                    this.midSafety.add(pos);
                }
            }
        }

        return safeSpots;
    }

    public boolean isSafe(BlockPos pos) {
        boolean isSafe = true;
        BlockPos[] ablockpos = HoleManager.surroundOffset;
        int i = ablockpos.length;

        for (int j = 0; j < i; ++j) {
            BlockPos offset = ablockpos[j];
            Block block = HoleManager.mc.world.getBlockState(pos.add(offset)).getBlock();

            if (block != Blocks.BEDROCK) {
                isSafe = false;
                break;
            }
        }

        return isSafe;
    }
}
