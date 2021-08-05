package club.tater.tatergod.util;

import club.tater.tatergod.manager.EnemiesManager;
import club.tater.tatergod.manager.FriendManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class NiggaUtil implements Nigger {

    public static boolean canEntityFeetBeSeen(Entity entityIn) {
        return NiggaUtil.mc.world.rayTraceBlocks(new Vec3d(NiggaUtil.mc.player.posX, NiggaUtil.mc.player.posX + (double) NiggaUtil.mc.player.getEyeHeight(), NiggaUtil.mc.player.posZ), new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ), false, true, false) == null;
    }

    public static boolean isSafe(Entity entity, int height, boolean floor) {
        return getUnsafeBlocks(entity, height, floor).size() == 0;
    }

    public static List getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
    }

    public static boolean isAboveBlock(Entity entity, BlockPos blockPos) {
        return entity.posY >= (double) blockPos.getY();
    }

    public static List getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        ArrayList vec3ds = new ArrayList();
        Vec3d[] avec3d = getOffsets(height, floor);
        int i = avec3d.length;

        for (int j = 0; j < i; ++j) {
            Vec3d vector = avec3d[j];
            BlockPos targetPos = (new BlockPos(pos)).add(vector.x, vector.y, vector.z);
            Block block = NiggaUtil.mc.world.getBlockState(targetPos).getBlock();

            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }

        return vec3ds;
    }

    public static boolean isAboveLiquid(Entity entity) {
        if (entity == null) {
            return false;
        } else {
            double n = entity.posY + 0.01D;

            for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
                for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                    if (EntityUtil.mc.world.getBlockState(new BlockPos(i, (int) n, j)).getBlock() instanceof BlockLiquid) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean checkForLiquid(Entity entity, boolean b) {
        if (entity == null) {
            return false;
        } else {
            double posY = entity.posY;
            double n;

            if (b) {
                n = 0.03D;
            } else if (entity instanceof EntityPlayer) {
                n = 0.2D;
            } else {
                n = 0.5D;
            }

            double n2 = posY - n;

            for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
                for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                    if (EntityUtil.mc.world.getBlockState(new BlockPos(i, MathHelper.floor(n2), j)).getBlock() instanceof BlockLiquid) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean checkCollide() {
        return !NiggaUtil.mc.player.isSneaking() && (NiggaUtil.mc.player.getRidingEntity() == null || NiggaUtil.mc.player.getRidingEntity().fallDistance < 3.0F) && NiggaUtil.mc.player.fallDistance < 3.0F;
    }

    public static boolean isOnLiquid(double offset) {
        if (NiggaUtil.mc.player.fallDistance >= 3.0F) {
            return false;
        } else {
            AxisAlignedBB bb = NiggaUtil.mc.player.getRidingEntity() != null ? NiggaUtil.mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0D, 0.0D, 0.0D).offset(0.0D, -offset, 0.0D) : NiggaUtil.mc.player.getEntityBoundingBox().contract(0.0D, 0.0D, 0.0D).offset(0.0D, -offset, 0.0D);
            boolean onLiquid = false;
            int y = (int) bb.minY;

            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); ++x) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); ++z) {
                    Block block = NiggaUtil.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();

                    if (block != Blocks.AIR) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }

                        onLiquid = true;
                    }
                }
            }

            return onLiquid;
        }
    }

    public static BlockPos getRoundedBlockPos(Entity entity) {
        return new BlockPos(MathUtil.roundVec(entity.getPositionVector(), 0));
    }

    public static boolean stopSneaking(boolean isSneaking) {
        if (isSneaking && NiggaUtil.mc.player != null) {
            NiggaUtil.mc.player.connection.sendPacket(new CPacketEntityAction(NiggaUtil.mc.player, Action.STOP_SNEAKING));
        }

        return false;
    }

    public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor) {
        List list = getUnsafeBlocksFromVec3d(pos, height, floor);
        Vec3d[] array = new Vec3d[list.size()];

        return (Vec3d[]) list.toArray(array);
    }

    public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
        List list = getUnsafeBlocks(entity, height, floor);
        Vec3d[] array = new Vec3d[list.size()];

        return (Vec3d[]) list.toArray(array);
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List offsets = getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];

        return (Vec3d[]) offsets.toArray(array);
    }

    public static List getOffsetList(int y, boolean floor) {
        ArrayList offsets = new ArrayList();

        offsets.add(new Vec3d(-1.0D, (double) y, 0.0D));
        offsets.add(new Vec3d(1.0D, (double) y, 0.0D));
        offsets.add(new Vec3d(0.0D, (double) y, -1.0D));
        offsets.add(new Vec3d(0.0D, (double) y, 1.0D));
        if (floor) {
            offsets.add(new Vec3d(0.0D, (double) (y - 1), 0.0D));
        }

        return offsets;
    }

    public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            NiggaUtil.mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            NiggaUtil.mc.playerController.attackEntity(NiggaUtil.mc.player, entity);
        }

        if (swingArm) {
            NiggaUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        }

    }

    public static void attackEntity(Entity entity, boolean packet) {
        if (packet) {
            NiggaUtil.mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            NiggaUtil.mc.playerController.attackEntity(NiggaUtil.mc.player, entity);
        }

    }

    public static BlockPos getFlooredPos(Entity e) {
        return new BlockPos(Math.floor(e.posX), Math.floor(e.posY), Math.floor(e.posZ));
    }

    public static boolean isInHole(Entity entity) {
        return isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }

    public static boolean isBlockValid(BlockPos blockPos) {
        return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos);
    }

    public static boolean isObbyHole(BlockPos blockPos) {
        BlockPos[] ablockpos = new BlockPos[] { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        int i = ablockpos.length;

        for (int j = 0; j < i; ++j) {
            BlockPos pos = ablockpos[j];
            IBlockState touchingState = NiggaUtil.mc.world.getBlockState(pos);

            if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
        }

        return true;
    }

    public static boolean isBedrockHole(BlockPos blockPos) {
        BlockPos[] ablockpos = new BlockPos[] { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        int i = ablockpos.length;

        for (int j = 0; j < i; ++j) {
            BlockPos pos = ablockpos[j];
            IBlockState touchingState = NiggaUtil.mc.world.getBlockState(pos);

            if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.BEDROCK) {
                return false;
            }
        }

        return true;
    }

    public static boolean isBothHole(BlockPos blockPos) {
        BlockPos[] ablockpos = new BlockPos[] { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        int i = ablockpos.length;

        for (int j = 0; j < i; ++j) {
            BlockPos pos = ablockpos[j];
            IBlockState touchingState = NiggaUtil.mc.world.getBlockState(pos);

            if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.BEDROCK && touchingState.getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
        }

        return true;
    }

    public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
        return getInterpolatedPos(entity, partialTicks).subtract(NiggaUtil.mc.getRenderManager().renderPosX, NiggaUtil.mc.getRenderManager().renderPosY, NiggaUtil.mc.getRenderManager().renderPosZ);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
        return (new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ)).add(getInterpolatedAmount(entity, partialTicks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
        return getInterpolatedAmount(entity, (double) partialTicks, (double) partialTicks, (double) partialTicks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static BlockPos getPlayerPosWithEntity() {
        return new BlockPos(NiggaUtil.mc.player.getRidingEntity() != null ? NiggaUtil.mc.player.getRidingEntity().posX : NiggaUtil.mc.player.posX, NiggaUtil.mc.player.getRidingEntity() != null ? NiggaUtil.mc.player.getRidingEntity().posY : NiggaUtil.mc.player.posY, NiggaUtil.mc.player.getRidingEntity() != null ? NiggaUtil.mc.player.getRidingEntity().posZ : NiggaUtil.mc.player.posZ);
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) time);
    }

    public static List getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plusY) {
        ArrayList circleBlocks = new ArrayList();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();

        for (int x = cx - (int) r; (float) x <= (float) cx + r; ++x) {
            for (int z = cz - (int) r; (float) z <= (float) cz + r; ++z) {
                for (int y = sphere ? cy - (int) r : cy; (float) y < (sphere ? (float) cy + r : (float) (cy + h)); ++y) {
                    double dist = (double) ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));

                    if (dist < (double) (r * r) && (!hollow || dist >= (double) ((r - 1.0F) * (r - 1.0F)))) {
                        BlockPos l = new BlockPos(x, y + plusY, z);

                        circleBlocks.add(l);
                    }
                }
            }
        }

        return circleBlocks;
    }

    public static boolean holding32k(EntityPlayer player) {
        return is32k(player.getHeldItemMainhand());
    }

    public static boolean is32k(ItemStack stack) {
        if (stack == null) {
            return false;
        } else if (stack.getTagCompound() == null) {
            return false;
        } else {
            NBTTagList enchants = (NBTTagList) stack.getTagCompound().getTag("ench");

            if (enchants == null) {
                return false;
            } else {
                for (int i = 0; i < enchants.tagCount(); ++i) {
                    NBTTagCompound enchant = enchants.getCompoundTagAt(i);

                    if (enchant.getInteger("id") == 16) {
                        int lvl = enchant.getInteger("lvl");

                        if (lvl >= 42) {
                            return true;
                        }
                        break;
                    }
                }

                return false;
            }
        }
    }

    public static float getHealth(Entity entity) {
        if (entity.isEntityAlive()) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;

            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        } else {
            return 0.0F;
        }
    }

    public static boolean isntValid(Entity entity, double range) {
        FriendManager friendManager = new FriendManager();

        return entity == null || !entity.isEntityAlive() || entity.equals(NiggaUtil.mc.player) || entity instanceof EntityPlayer && friendManager.isFriend(entity.getName()) || NiggaUtil.mc.player.getDistanceSq(entity) > range * range;
    }

    public static Color getColor(Entity entity, int red, int green, int blue, int alpha, boolean colorFriends) {
        Color color = new Color((float) red / 255.0F, (float) green / 255.0F, (float) blue / 255.0F, (float) alpha / 255.0F);

        if (entity instanceof EntityPlayer) {
            FriendManager friendManager = new FriendManager();

            new EnemiesManager();

            if (colorFriends && friendManager.isFriend(entity.getName())) {
                color = new Color(0.33F, 1.0F, 1.0F, (float) alpha / 255.0F);
            }
        }

        return color;
    }

    public static void setTimer(float speed) {
        NiggaUtil.mc.timer.tickLength = 50.0F / speed;
    }

    public static void resetTimer() {
        NiggaUtil.mc.timer.tickLength = 50.0F;
    }

    public static Block isColliding(double posX, double posY, double posZ) {
        Block block = null;

        if (NiggaUtil.mc.player != null) {
            AxisAlignedBB bb = NiggaUtil.mc.player.getRidingEntity() != null ? NiggaUtil.mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0D, 0.0D, 0.0D).offset(posX, posY, posZ) : NiggaUtil.mc.player.getEntityBoundingBox().contract(0.0D, 0.0D, 0.0D).offset(posX, posY, posZ);
            int y = (int) bb.minY;

            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; ++x) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; ++z) {
                    block = NiggaUtil.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                }
            }
        }

        return block;
    }

    public static boolean isInLiquid() {
        if (NiggaUtil.mc.player == null) {
            return false;
        } else if (NiggaUtil.mc.player.fallDistance >= 3.0F) {
            return false;
        } else {
            boolean inLiquid = false;
            AxisAlignedBB bb = NiggaUtil.mc.player.getRidingEntity() != null ? NiggaUtil.mc.player.getRidingEntity().getEntityBoundingBox() : NiggaUtil.mc.player.getEntityBoundingBox();
            int y = (int) bb.minY;

            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; ++x) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; ++z) {
                    Block block = NiggaUtil.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();

                    if (!(block instanceof BlockAir)) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }

                        inLiquid = true;
                    }
                }
            }

            return inLiquid;
        }
    }
}
