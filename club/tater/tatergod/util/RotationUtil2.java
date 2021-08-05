package club.tater.tatergod.util;

import club.tater.tatergod.features.modules.client.ClickGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil2 implements Util {

    public static Vec3d getEyesPos() {
        return new Vec3d(RotationUtil.mc.player.posX, RotationUtil.mc.player.posY + (double) RotationUtil.mc.player.getEyeHeight(), RotationUtil.mc.player.posZ);
    }

    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        double pitch = Math.asin(diry / len);
        double yaw = Math.atan2(dirz / len, dirx / len);

        pitch = pitch * 180.0D / 3.141592653589793D;
        yaw = yaw * 180.0D / 3.141592653589793D;
        return new double[] { yaw += 90.0D, pitch};
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = RotationUtil.getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        return new float[] { RotationUtil.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - RotationUtil.mc.player.rotationYaw), RotationUtil.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - RotationUtil.mc.player.rotationPitch)};
    }

    public static float[] simpleFacing(EnumFacing facing) {
        switch (facing) {
        case DOWN:
            return new float[] { RotationUtil.mc.player.rotationYaw, 90.0F};

        case UP:
            return new float[] { RotationUtil.mc.player.rotationYaw, -90.0F};

        case NORTH:
            return new float[] { 180.0F, 0.0F};

        case SOUTH:
            return new float[] { 0.0F, 0.0F};

        case WEST:
            return new float[] { 90.0F, 0.0F};

        default:
            return new float[] { 270.0F, 0.0F};
        }
    }

    public static void faceYawAndPitch(float yaw, float pitch) {
        RotationUtil.mc.player.connection.sendPacket(new Rotation(yaw, pitch, RotationUtil.mc.player.onGround));
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = RotationUtil.getLegitRotations(vec);

        RotationUtil.mc.player.connection.sendPacket(new Rotation(rotations[0], normalizeAngle ? (float) MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], RotationUtil.mc.player.onGround));
    }

    public static void faceEntity(Entity entity) {
        float[] angle = MathUtil.calcAngle(RotationUtil.mc.player.getPositionEyes(RotationUtil2.mc.getRenderPartialTicks()), entity.getPositionEyes(RotationUtil2.mc.getRenderPartialTicks()));

        RotationUtil.faceYawAndPitch(angle[0], angle[1]);
    }

    public static float[] getAngle(Entity entity) {
        return MathUtil.calcAngle(RotationUtil.mc.player.getPositionEyes(RotationUtil2.mc.getRenderPartialTicks()), entity.getPositionEyes(RotationUtil2.mc.getRenderPartialTicks()));
    }

    public static float transformYaw() {
        float yaw = RotationUtil.mc.player.rotationYaw % 360.0F;

        if (RotationUtil.mc.player.rotationYaw > 0.0F) {
            if (yaw > 180.0F) {
                yaw = -180.0F + (yaw - 180.0F);
            }
        } else if (yaw < -180.0F) {
            yaw = 180.0F + yaw + 180.0F;
        }

        return yaw < 0.0F ? 180.0F + yaw : -180.0F + yaw;
    }

    public static boolean isInFov(BlockPos pos) {
        return pos != null && (RotationUtil.mc.player.getDistanceSq(pos) < 4.0D || yawDist(pos) < (double) (getHalvedfov() + 2.0F));
    }

    public static boolean isInFov(Entity entity) {
        return entity != null && (RotationUtil.mc.player.getDistanceSq(entity) < 4.0D || yawDist(entity) < (double) (getHalvedfov() + 2.0F));
    }

    public static double yawDist(BlockPos pos) {
        if (pos != null) {
            Vec3d difference = (new Vec3d(pos)).subtract(RotationUtil.mc.player.getPositionEyes(RotationUtil2.mc.getRenderPartialTicks()));
            double d = Math.abs((double) RotationUtil.mc.player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0D)) % 360.0D;

            return d > 180.0D ? 360.0D - d : d;
        } else {
            return 0.0D;
        }
    }

    public static double yawDist(Entity e) {
        if (e != null) {
            Vec3d difference = e.getPositionVector().add(0.0D, (double) (e.getEyeHeight() / 2.0F), 0.0D).subtract(RotationUtil.mc.player.getPositionEyes(RotationUtil2.mc.getRenderPartialTicks()));
            double d = Math.abs((double) RotationUtil.mc.player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0D)) % 360.0D;

            return d > 180.0D ? 360.0D - d : d;
        } else {
            return 0.0D;
        }
    }

    public static boolean isInFov(Vec3d vec3d, Vec3d other) {
        if (RotationUtil.mc.player.rotationPitch > 30.0F) {
            if (other.y > RotationUtil.mc.player.posY) {
                return true;
            }
        } else if (RotationUtil.mc.player.rotationPitch < -30.0F && other.y < RotationUtil.mc.player.posY) {
            return true;
        }

        float angle = MathUtil.calcAngleNoY(vec3d, other)[0] - RotationUtil.transformYaw();

        if (angle < -270.0F) {
            return true;
        } else {
            float fov = (((Boolean) ClickGui.getInstance().customFov.getValue()).booleanValue() ? ((Float) ClickGui.getInstance().fov.getValue()).floatValue() : RotationUtil.mc.gameSettings.fovSetting) / 2.0F;

            return angle < fov + 10.0F && angle > -fov - 10.0F;
        }
    }

    public static float getFov() {
        return ((Boolean) ClickGui.getInstance().customFov.getValue()).booleanValue() ? ((Float) ClickGui.getInstance().fov.getValue()).floatValue() : RotationUtil.mc.gameSettings.fovSetting;
    }

    public static float getHalvedfov() {
        return getFov() / 2.0F;
    }

    public static int getDirection4D() {
        return MathHelper.floor((double) (RotationUtil.mc.player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
    }

    public static String getDirection4D(boolean northRed) {
        int dirnumber = RotationUtil.getDirection4D();

        return dirnumber == 0 ? "South (+Z)" : (dirnumber == 1 ? "West (-X)" : (dirnumber == 2 ? (northRed ? "§c" : "") + "North (-Z)" : (dirnumber == 3 ? "East (+X)" : "Loading...")));
    }
}
