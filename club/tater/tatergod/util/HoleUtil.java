package club.tater.tatergod.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HoleUtil {

    public static final List holeBlocks = Arrays.asList(new BlockPos[] { new BlockPos(0, -1, 0), new BlockPos(0, 0, -1), new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1)});
    public static final Vec3d[] cityOffsets = new Vec3d[] { new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D)};
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isInHole() {
        Vec3d playerPos = CombatUtil.interpolateEntity(HoleUtil.mc.player);
        BlockPos blockpos = new BlockPos(playerPos.x, playerPos.y, playerPos.z);
        int size = 0;
        Iterator iterator = HoleUtil.holeBlocks.iterator();

        while (iterator.hasNext()) {
            BlockPos bPos = (BlockPos) iterator.next();

            if (CombatUtil.isHard(HoleUtil.mc.world.getBlockState(blockpos.add(bPos)).getBlock())) {
                ++size;
            }
        }

        return size == 5;
    }
}
