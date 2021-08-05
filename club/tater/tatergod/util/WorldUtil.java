package club.tater.tatergod.util;

import club.tater.tatergod.MinecraftInstance;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WorldUtil implements MinecraftInstance {

    public static void placeBlock(BlockPos pos) {
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing enumFacing = aenumfacing[j];

            if (!WorldUtil.mc.world.getBlockState(pos.offset(enumFacing)).getBlock().equals(Blocks.AIR) && !isIntercepted(pos)) {
                Vec3d vec = new Vec3d((double) pos.getX() + 0.5D + (double) enumFacing.getXOffset() * 0.5D, (double) pos.getY() + 0.5D + (double) enumFacing.getYOffset() * 0.5D, (double) pos.getZ() + 0.5D + (double) enumFacing.getZOffset() * 0.5D);
                float[] old = new float[] { WorldUtil.mc.player.rotationYaw, WorldUtil.mc.player.rotationPitch};

                WorldUtil.mc.player.connection.sendPacket(new Rotation((float) Math.toDegrees(Math.atan2(vec.z - WorldUtil.mc.player.posZ, vec.x - WorldUtil.mc.player.posX)) - 90.0F, (float) (-Math.toDegrees(Math.atan2(vec.y - (WorldUtil.mc.player.posY + (double) WorldUtil.mc.player.getEyeHeight()), Math.sqrt((vec.x - WorldUtil.mc.player.posX) * (vec.x - WorldUtil.mc.player.posX) + (vec.z - WorldUtil.mc.player.posZ) * (vec.z - WorldUtil.mc.player.posZ))))), WorldUtil.mc.player.onGround));
                WorldUtil.mc.player.connection.sendPacket(new CPacketEntityAction(WorldUtil.mc.player, Action.START_SNEAKING));
                WorldUtil.mc.playerController.processRightClickBlock(WorldUtil.mc.player, WorldUtil.mc.world, pos.offset(enumFacing), enumFacing.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
                WorldUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
                WorldUtil.mc.player.connection.sendPacket(new CPacketEntityAction(WorldUtil.mc.player, Action.STOP_SNEAKING));
                WorldUtil.mc.player.connection.sendPacket(new Rotation(old[0], old[1], WorldUtil.mc.player.onGround));
                return;
            }
        }

    }

    public static void placeBlock(BlockPos pos, int slot) {
        if (slot != -1) {
            int prev = WorldUtil.mc.player.inventory.currentItem;

            WorldUtil.mc.player.inventory.currentItem = slot;
            placeBlock(pos);
            WorldUtil.mc.player.inventory.currentItem = prev;
        }
    }

    public static boolean isIntercepted(BlockPos pos) {
        Iterator iterator = WorldUtil.mc.world.loadedEntityList.iterator();

        Entity entity;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entity = (Entity) iterator.next();
        } while (!(new AxisAlignedBB(pos)).intersects(entity.getEntityBoundingBox()));

        return true;
    }

    public static BlockPos GetLocalPlayerPosFloored() {
        return new BlockPos(Math.floor(WorldUtil.mc.player.posX), Math.floor(WorldUtil.mc.player.posY), Math.floor(WorldUtil.mc.player.posZ));
    }

    public static boolean canBreak(BlockPos pos) {
        return WorldUtil.mc.world.getBlockState(pos).getBlock().getBlockHardness(WorldUtil.mc.world.getBlockState(pos), WorldUtil.mc.world, pos) != -1.0F;
    }

    public static Block getWorld() {
        return null;
    }
}
