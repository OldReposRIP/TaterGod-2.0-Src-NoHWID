package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BurrowUtil;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class Burrow extends Module {

    private final Setting offset = this.register(new Setting("Offset", Integer.valueOf(2), Integer.valueOf(-5), Integer.valueOf(5)));
    private final Setting rotate = this.register(new Setting("Rotate", Boolean.valueOf(false)));
    private final Setting mode;
    Block returnBlock;
    private BlockPos originalPos;
    private int oldSlot;

    public Burrow() {
        super("Burrow", "TPs you into a block", Module.Category.COMBAT, true, false, false);
        this.mode = this.register(new Setting("Mode", Burrow.Mode.OBBY));
        this.returnBlock = null;
        this.oldSlot = -1;
    }

    public void onEnable() {
        super.onEnable();
        this.originalPos = new BlockPos(Burrow.mc.player.posX, Burrow.mc.player.posY, Burrow.mc.player.posZ);
        switch ((Burrow.Mode) this.mode.getValue()) {
        case OBBY:
            this.returnBlock = Blocks.OBSIDIAN;
            break;

        case ECHEST:
            this.returnBlock = Blocks.ENDER_CHEST;
            break;

        case CHEST:
            this.returnBlock = Blocks.CHEST;
        }

        if (!Burrow.mc.world.getBlockState(new BlockPos(Burrow.mc.player.posX, Burrow.mc.player.posY, Burrow.mc.player.posZ)).getBlock().equals(this.returnBlock) && !this.intersectsWithEntity(this.originalPos)) {
            this.oldSlot = Burrow.mc.player.inventory.currentItem;
        } else {
            this.toggle();
        }
    }

    public void onUpdate() {
        switch ((Burrow.Mode) this.mode.getValue()) {
        case OBBY:
            if (BurrowUtil.findHotbarBlock(BlockObsidian.class) == -1) {
                Command.sendMessage("Can\'t find obby in hotbar!");
                this.toggle();
            }
            break;

        case ECHEST:
            if (BurrowUtil.findHotbarBlock(BlockEnderChest.class) == -1) {
                Command.sendMessage("Can\'t find echest in hotbar!");
                this.toggle();
            }
            break;

        case CHEST:
            if (BurrowUtil.findHotbarBlock(BlockChest.class) == -1) {
                Command.sendMessage("Can\'t find chest in hotbar!");
                this.toggle();
            }
        }

        switch ((Burrow.Mode) this.mode.getValue()) {
        case OBBY:
            BurrowUtil.switchToSlot(BurrowUtil.findHotbarBlock(BlockObsidian.class));
            break;

        case ECHEST:
            BurrowUtil.switchToSlot(BurrowUtil.findHotbarBlock(BlockEnderChest.class));
            break;

        case CHEST:
            BurrowUtil.switchToSlot(BurrowUtil.findHotbarBlock(BlockChest.class));
        }

        Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + 0.41999998688698D, Burrow.mc.player.posZ, true));
        Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + 0.7531999805211997D, Burrow.mc.player.posZ, true));
        Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + 1.00133597911214D, Burrow.mc.player.posZ, true));
        Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + 1.16610926093821D, Burrow.mc.player.posZ, true));
        BurrowUtil.placeBlock(this.originalPos, EnumHand.MAIN_HAND, ((Boolean) this.rotate.getValue()).booleanValue(), true, false);
        Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + (double) ((Integer) this.offset.getValue()).intValue(), Burrow.mc.player.posZ, false));
        Burrow.mc.player.connection.sendPacket(new CPacketEntityAction(Burrow.mc.player, Action.STOP_SNEAKING));
        Burrow.mc.player.setSneaking(false);
        BurrowUtil.switchToSlot(this.oldSlot);
        this.toggle();
    }

    private boolean intersectsWithEntity(BlockPos pos) {
        Iterator iterator = Burrow.mc.world.loadedEntityList.iterator();

        Entity entity;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entity = (Entity) iterator.next();
        } while (entity.equals(Burrow.mc.player) || entity instanceof EntityItem || !(new AxisAlignedBB(pos)).intersects(entity.getEntityBoundingBox()));

        return true;
    }

    public static enum Mode {

        OBBY, ECHEST, CHEST;
    }
}
