package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BlockInteractionUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.Util;
import club.tater.tatergod.util.WorldUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SelfWeb extends Module {

    public Setting alwayson = this.register(new Setting("AlwaysOn", Boolean.valueOf(false)));
    public Setting rotate = this.register(new Setting("Rotate", Boolean.valueOf(true)));
    public Setting webRange = this.register(new Setting("EnemyRange", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(8)));
    int new_slot = -1;
    boolean sneak = false;

    public SelfWeb() {
        super("SelfWeb", "Places webs at your feet", Module.Category.COMBAT, false, false, false);
    }

    public void enable() {
        if (Util.mc.player != null) {
            this.new_slot = this.find_in_hotbar();
            if (this.new_slot == -1) {
                Command.sendMessage(ChatFormatting.RED + "< " + ChatFormatting.GRAY + "SelfWeb" + ChatFormatting.RED + "> " + ChatFormatting.DARK_RED + "No webs in hotbar!");
            }
        }

    }

    public void disable() {
        if (Util.mc.player != null && this.sneak) {
            Util.mc.player.connection.sendPacket(new CPacketEntityAction(Util.mc.player, Action.STOP_SNEAKING));
            this.sneak = false;
        }

    }

    public void onUpdate() {
        if (Util.mc.player != null) {
            if (((Boolean) this.alwayson.getValue()).booleanValue()) {
                EntityPlayer last_slot = this.find_closest_target();

                if (last_slot == null) {
                    return;
                }

                if (Util.mc.player.getDistance(last_slot) < (float) ((Integer) this.webRange.getValue()).intValue() && this.is_surround()) {
                    int last_slot1 = Util.mc.player.inventory.currentItem;

                    Util.mc.player.inventory.currentItem = this.new_slot;
                    Util.mc.playerController.updateController();
                    this.place_blocks(WorldUtil.GetLocalPlayerPosFloored());
                    Util.mc.player.inventory.currentItem = last_slot1;
                }
            } else {
                int last_slot2 = Util.mc.player.inventory.currentItem;

                Util.mc.player.inventory.currentItem = this.new_slot;
                Util.mc.playerController.updateController();
                this.place_blocks(WorldUtil.GetLocalPlayerPosFloored());
                Util.mc.player.inventory.currentItem = last_slot2;
                this.disable();
            }

        }
    }

    public EntityPlayer find_closest_target() {
        if (Util.mc.world.playerEntities.isEmpty()) {
            return null;
        } else {
            EntityPlayer closestTarget = null;
            Iterator iterator = Util.mc.world.playerEntities.iterator();

            while (iterator.hasNext()) {
                EntityPlayer target = (EntityPlayer) iterator.next();

                if (target != Util.mc.player && !EntityUtil.isLiving(target) && target.getHealth() > 0.0F && (closestTarget == null || Util.mc.player.getDistance(target) <= Util.mc.player.getDistance(closestTarget))) {
                    closestTarget = target;
                }
            }

            return closestTarget;
        }
    }

    private int find_in_hotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = Util.mc.player.inventory.getStackInSlot(i);

            if (stack.getItem() == Item.getItemById(30)) {
                return i;
            }
        }

        return -1;
    }

    private boolean is_surround() {
        BlockPos player_block = WorldUtil.GetLocalPlayerPosFloored();

        return Util.mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR && Util.mc.world.getBlockState(player_block.west()).getBlock() != Blocks.AIR && Util.mc.world.getBlockState(player_block.north()).getBlock() != Blocks.AIR && Util.mc.world.getBlockState(player_block.south()).getBlock() != Blocks.AIR && Util.mc.world.getBlockState(player_block).getBlock() == Blocks.AIR;
    }

    private void place_blocks(BlockPos pos) {
        if (Util.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            if (BlockInteractionUtil.checkForNeighbours(pos)) {
                EnumFacing[] aenumfacing = EnumFacing.values();
                int i = aenumfacing.length;

                for (int j = 0; j < i; ++j) {
                    EnumFacing side = aenumfacing[j];
                    BlockPos neighbor = pos.offset(side);
                    EnumFacing side2 = side.getOpposite();

                    if (BlockInteractionUtil.canBeClicked(neighbor)) {
                        if (BlockInteractionUtil.blackList.contains(Util.mc.world.getBlockState(neighbor).getBlock())) {
                            Util.mc.player.connection.sendPacket(new CPacketEntityAction(Util.mc.player, Action.START_SNEAKING));
                            this.sneak = true;
                        }

                        Vec3d hitVec = (new Vec3d(neighbor)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(side2.getDirectionVec())).scale(0.5D));

                        if (((Boolean) this.rotate.getValue()).booleanValue()) {
                            BlockInteractionUtil.faceVectorPacketInstant(hitVec);
                        }

                        Util.mc.playerController.processRightClickBlock(Util.mc.player, Util.mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                        Util.mc.player.swingArm(EnumHand.MAIN_HAND);
                        return;
                    }
                }

            }
        }
    }
}
