

package com.ragemachine.moon.impl.util;

import com.ragemachine.moon.impl.client.modules.client.ClickGui;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class RotationUtil implements Util
{
    public static Vec3d getEyesPos() {
        return new Vec3d(RotationUtil.mc.player.posX, RotationUtil.mc.player.posY + RotationUtil.mc.player.getEyeHeight(), RotationUtil.mc.player.posZ);
    }

    public static double[] calculateLookAt(final double px, final double py, final double pz, final EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;
        final double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0 / 3.141592653589793;
        yaw = yaw * 180.0 / 3.141592653589793;
        yaw += 90.0;
        return new double[] { yaw, pitch };
    }

    public static float[] getLegitRotations(final Vec3d vec) {
        final Vec3d eyesPos = getEyesPos();
        final double diffX = vec.x - eyesPos.x;
        final double diffY = vec.y - eyesPos.y;
        final double diffZ = vec.z - eyesPos.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { RotationUtil.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - RotationUtil.mc.player.rotationYaw), RotationUtil.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - RotationUtil.mc.player.rotationPitch) };
    }

    public static float[] simpleFacing(final EnumFacing facing) {
        switch (facing) {
            case DOWN: {
                return new float[] { RotationUtil.mc.player.rotationYaw, 90.0f };
            }
            case UP: {
                return new float[] { RotationUtil.mc.player.rotationYaw, -90.0f };
            }
            case NORTH: {
                return new float[] { 180.0f, 0.0f };
            }
            case SOUTH: {
                return new float[] { 0.0f, 0.0f };
            }
            case WEST: {
                return new float[] { 90.0f, 0.0f };
            }
            default: {
                return new float[] { 270.0f, 0.0f };
            }
        }
    }

    public static void faceYawAndPitch(final float yaw, final float pitch) {
        RotationUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(yaw, pitch, RotationUtil.mc.player.onGround));
    }

    public static void faceVector(final Vec3d vec, final boolean normalizeAngle) {
        final float[] rotations = getLegitRotations(vec);
        RotationUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? ((float)MathHelper.normalizeAngle((int)rotations[1], 360)) : rotations[1], RotationUtil.mc.player.onGround));
    }

    public static void faceEntity(final Entity entity) {
        final float[] angle = MathUtil.calcAngle(RotationUtil.mc.player.getPositionEyes(RotationUtil.mc.getRenderPartialTicks()), entity.getPositionEyes(RotationUtil.mc.getRenderPartialTicks()));
        faceYawAndPitch(angle[0], angle[1]);
    }

    public static float[] getAngle(final Entity entity) {
        return MathUtil.calcAngle(RotationUtil.mc.player.getPositionEyes(RotationUtil.mc.getRenderPartialTicks()), entity.getPositionEyes(RotationUtil.mc.getRenderPartialTicks()));
    }

    public static float transformYaw() {
        float yaw = RotationUtil.mc.player.rotationYaw % 360.0f;
        if (RotationUtil.mc.player.rotationYaw > 0.0f) {
            if (yaw > 180.0f) {
                yaw = -180.0f + (yaw - 180.0f);
            }
        }
        else if (yaw < -180.0f) {
            yaw = 180.0f + (yaw + 180.0f);
        }
        if (yaw < 0.0f) {
            return 180.0f + yaw;
        }
        return -180.0f + yaw;
    }

    public static boolean isInFov(final BlockPos pos) {
        return pos != null && (RotationUtil.mc.player.getDistanceSq(pos) < 4.0 || isInFov(new Vec3d((Vec3i)pos), RotationUtil.mc.player.getPositionVector()));
    }

    public static boolean isInFov(final Entity entity) {
        return entity != null && (RotationUtil.mc.player.getDistanceSq(entity) < 4.0 || isInFov(entity.getPositionVector(), RotationUtil.mc.player.getPositionVector()));
    }

    public static boolean isInFov(final Vec3d vec3d, final Vec3d other) {
        if (RotationUtil.mc.player.rotationPitch > 30.0f) {
            if (other.y > RotationUtil.mc.player.posY) {
                return true;
            }
        }
        else if (RotationUtil.mc.player.rotationPitch < -30.0f && other.y < RotationUtil.mc.player.posY) {
            return true;
        }
        final float angle = MathUtil.calcAngleNoY(vec3d, other)[0] - transformYaw();
        if (angle < -270.0f) {
            return true;
        }
        final float fov = (ClickGui.getInstance().customFov.getValue() ? ClickGui.getInstance().fov.getValue() : RotationUtil.mc.gameSettings.fovSetting) / 2.0f;
        return angle < fov + 10.0f && angle > -fov - 10.0f;
    }

    public static int getDirection4D() {
        return MathHelper.floor(RotationUtil.mc.player.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
    }

    public static String getDirection4D(final boolean northRed) {
        final int dirnumber = getDirection4D();
        if (dirnumber == 0) {
            return "South (+Z)";
        }
        if (dirnumber == 1) {
            return "West (-X)";
        }
        if (dirnumber == 2) {
            return (northRed ? "§c" : "") + "North (-Z)";
        }
        if (dirnumber == 3) {
            return "East (+X)";
        }
        return "Loading...";
    }
}
