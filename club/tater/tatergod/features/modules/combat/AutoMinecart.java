package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BlockUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.MathUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Iterator;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoMinecart extends Module {

    private final Setting web;
    private final Setting rotate;
    private final Setting packet;
    private final Setting blocksPerTick;
    private final Setting delay;
    public Setting minHP;
    int wait;
    int waitFlint;
    int originalSlot;
    private boolean check;

    public AutoMinecart() {
        super("AutoMinecart", "Places and explodes minecarts on other players.", Module.Category.COMBAT, true, false, false);
        this.web = this.register(new Setting("Web", Boolean.FALSE));
        this.rotate = this.register(new Setting("Rotate", Boolean.FALSE));
        this.packet = this.register(new Setting("PacketPlace", Boolean.FALSE));
        this.blocksPerTick = this.register(new Setting("BlocksPerTick", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(4)));
        this.delay = this.register(new Setting("Carts", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(50)));
        this.minHP = this.register(new Setting("MinHP", Float.valueOf(4.0F), Float.valueOf(0.0F), Float.valueOf(36.0F)));
    }

    public void onEnable() {
        if (fullNullCheck()) {
            this.toggle();
        }

        this.wait = 0;
        this.waitFlint = 0;
        this.originalSlot = AutoMinecart.mc.player.inventory.currentItem;
        this.check = true;
    }

    public void onUpdate() {
        if (fullNullCheck()) {
            this.toggle();
        }

        int i = InventoryUtil.findStackInventory(Items.TNT_MINECART);

        int webSlot;

        for (webSlot = 0; webSlot < 9; ++webSlot) {
            if (AutoMinecart.mc.player.inventory.getStackInSlot(webSlot).getItem() == Items.AIR && i != -1) {
                AutoMinecart.mc.playerController.windowClick(AutoMinecart.mc.player.inventoryContainer.windowId, i, 0, ClickType.QUICK_MOVE, AutoMinecart.mc.player);
                AutoMinecart.mc.playerController.updateController();
            }
        }

        webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        int tntSlot = InventoryUtil.getItemHotbar(Items.TNT_MINECART);
        int flintSlot = InventoryUtil.getItemHotbar(Items.FLINT_AND_STEEL);
        int railSlot = InventoryUtil.findHotbarBlock(Blocks.ACTIVATOR_RAIL);
        int picSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);

        if (tntSlot == -1 || railSlot == -1 || flintSlot == -1 || picSlot == -1 || ((Boolean) this.web.getValue()).booleanValue() && webSlot == -1) {
            Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "No (tnt minecart/activator rail/flint/pic/webs) in hotbar disabling...");
            this.toggle();
        }

        EntityPlayer target;

        if ((target = this.getTarget()) != null) {
            BlockPos pos = new BlockPos(target.posX, target.posY, target.posZ);
            Vec3d hitVec = (new Vec3d(pos)).add(0.0D, -0.5D, 0.0D);

            if (AutoMinecart.mc.player.getDistanceSq(pos) <= MathUtil.square(6.0D)) {
                this.check = true;
                if (AutoMinecart.mc.world.getBlockState(pos).getBlock() != Blocks.ACTIVATOR_RAIL && !AutoMinecart.mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos)).isEmpty()) {
                    InventoryUtil.switchToHotbarSlot(flintSlot, false);
                    BlockUtil.rightClickBlock(pos.down(), hitVec, EnumHand.MAIN_HAND, EnumFacing.UP, ((Boolean) this.packet.getValue()).booleanValue());
                }

                if (AutoMinecart.mc.world.getBlockState(pos).getBlock() != Blocks.ACTIVATOR_RAIL && AutoMinecart.mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos)).isEmpty() && AutoMinecart.mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos.up())).isEmpty() && AutoMinecart.mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos.down())).isEmpty()) {
                    InventoryUtil.switchToHotbarSlot(railSlot, false);
                    BlockUtil.rightClickBlock(pos.down(), hitVec, EnumHand.MAIN_HAND, EnumFacing.UP, ((Boolean) this.packet.getValue()).booleanValue());
                    this.wait = 0;
                }

                if (((Boolean) this.web.getValue()).booleanValue() && this.wait != 0 && AutoMinecart.mc.world.getBlockState(pos).getBlock() == Blocks.ACTIVATOR_RAIL && !target.isInWeb && (BlockUtil.isPositionPlaceable(pos.up(), false) == 1 || BlockUtil.isPositionPlaceable(pos.up(), false) == 3) && AutoMinecart.mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos.up())).isEmpty()) {
                    InventoryUtil.switchToHotbarSlot(webSlot, false);
                    BlockUtil.placeBlock(pos.up(), EnumHand.MAIN_HAND, ((Boolean) this.rotate.getValue()).booleanValue(), ((Boolean) this.packet.getValue()).booleanValue(), false);
                }

                if (AutoMinecart.mc.world.getBlockState(pos).getBlock() == Blocks.ACTIVATOR_RAIL) {
                    InventoryUtil.switchToHotbarSlot(tntSlot, false);

                    for (int u = 0; u < ((Integer) this.blocksPerTick.getValue()).intValue(); ++u) {
                        BlockUtil.rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, EnumFacing.UP, ((Boolean) this.packet.getValue()).booleanValue());
                    }
                }

                if (this.wait < ((Integer) this.delay.getValue()).intValue()) {
                    ++this.wait;
                    return;
                }

                this.check = false;
                this.wait = 0;
                InventoryUtil.switchToHotbarSlot(picSlot, false);
                if (AutoMinecart.mc.world.getBlockState(pos).getBlock() == Blocks.ACTIVATOR_RAIL && !AutoMinecart.mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos)).isEmpty()) {
                    AutoMinecart.mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
                }

                InventoryUtil.switchToHotbarSlot(flintSlot, false);
                if (AutoMinecart.mc.world.getBlockState(pos).getBlock().getBlockState().getBaseState().getMaterial() != Material.FIRE && !AutoMinecart.mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos)).isEmpty()) {
                    BlockUtil.rightClickBlock(pos.down(), hitVec, EnumHand.MAIN_HAND, EnumFacing.UP, ((Boolean) this.packet.getValue()).booleanValue());
                }
            }

        }
    }

    public String getDisplayInfo() {
        return this.check ? ChatFormatting.GREEN + "Placing" : ChatFormatting.RED + "Breaking";
    }

    public void onDisable() {
        AutoMinecart.mc.player.inventory.currentItem = this.originalSlot;
    }

    private EntityPlayer getTarget() {
        EntityPlayer target = null;
        double distance = Math.pow(6.0D, 2.0D) + 1.0D;
        Iterator iterator = AutoMinecart.mc.world.playerEntities.iterator();

        while (iterator.hasNext()) {
            EntityPlayer player = (EntityPlayer) iterator.next();

            if (!EntityUtil.isntValid(player, 6.0D) && !player.isInWater() && !player.isInLava() && EntityUtil.isTrapped(player, false, false, false, false, false) && player.getHealth() + player.getAbsorptionAmount() <= ((Float) this.minHP.getValue()).floatValue()) {
                if (target == null) {
                    target = player;
                    distance = AutoMinecart.mc.player.getDistanceSq(player);
                } else if (AutoMinecart.mc.player.getDistanceSq(player) < distance) {
                    target = player;
                    distance = AutoMinecart.mc.player.getDistanceSq(player);
                }
            }
        }

        return target;
    }
}
