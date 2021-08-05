package club.tater.tatergod.features.modules.player;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.UpdateWalkingPlayerEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.InventoryUtil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemMinecart;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastPlace extends Module {

    private final Setting all = this.register(new Setting("All", Boolean.valueOf(false)));
    private final Setting obby = this.register(new Setting("Obsidian", Boolean.valueOf(false), test<invokedynamic>(this)));
    private final Setting enderChests = this.register(new Setting("EnderChests", Boolean.valueOf(false), test<invokedynamic>(this)));
    private final Setting crystals = this.register(new Setting("Crystals", Boolean.valueOf(false), test<invokedynamic>(this)));
    private final Setting exp = this.register(new Setting("Experience", Boolean.valueOf(false), test<invokedynamic>(this)));
    private final Setting Minecart = this.register(new Setting("Minecarts", Boolean.valueOf(false), test<invokedynamic>(this)));
    private final Setting feetExp = this.register(new Setting("ExpFeet", Boolean.valueOf(false)));
    private final Setting fastCrystal = this.register(new Setting("PacketCrystal", Boolean.valueOf(false)));
    private BlockPos mousePos = null;

    public FastPlace() {
        super("FastPlace", "Fast everything.", Module.Category.PLAYER, true, false, false);
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && ((Boolean) this.feetExp.getValue()).booleanValue()) {
            boolean mainHand = FastPlace.mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE;
            boolean offHand = FastPlace.mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE;

            if (FastPlace.mc.gameSettings.keyBindUseItem.isKeyDown() && (FastPlace.mc.player.getActiveHand() == EnumHand.MAIN_HAND && mainHand || FastPlace.mc.player.getActiveHand() == EnumHand.OFF_HAND && offHand)) {
                Tater.rotationManager.lookAtVec3d(FastPlace.mc.player.getPositionVector());
            }
        }

    }

    public void onUpdate() {
        if (!fullNullCheck()) {
            if (InventoryUtil.holdingItem(ItemExpBottle.class) && ((Boolean) this.exp.getValue()).booleanValue()) {
                FastPlace.mc.rightClickDelayTimer = 0;
            }

            if (InventoryUtil.holdingItem(BlockObsidian.class) && ((Boolean) this.obby.getValue()).booleanValue()) {
                FastPlace.mc.rightClickDelayTimer = 0;
            }

            if (InventoryUtil.holdingItem(BlockEnderChest.class) && ((Boolean) this.enderChests.getValue()).booleanValue()) {
                FastPlace.mc.rightClickDelayTimer = 0;
            }

            if (InventoryUtil.holdingItem(ItemMinecart.class) && ((Boolean) this.Minecart.getValue()).booleanValue()) {
                FastPlace.mc.rightClickDelayTimer = 0;
            }

            if (((Boolean) this.all.getValue()).booleanValue()) {
                FastPlace.mc.rightClickDelayTimer = 0;
            }

            if (InventoryUtil.holdingItem(ItemEndCrystal.class) && (((Boolean) this.crystals.getValue()).booleanValue() || ((Boolean) this.all.getValue()).booleanValue())) {
                FastPlace.mc.rightClickDelayTimer = 0;
            }

            if (((Boolean) this.fastCrystal.getValue()).booleanValue() && FastPlace.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                boolean offhand = FastPlace.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;

                if (offhand || FastPlace.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                    RayTraceResult result = FastPlace.mc.objectMouseOver;

                    if (result == null) {
                        return;
                    }

                    switch (result.typeOfHit) {
                    case MISS:
                        this.mousePos = null;
                        break;

                    case BLOCK:
                        this.mousePos = FastPlace.mc.objectMouseOver.getBlockPos();
                        break;

                    case ENTITY:
                        if (this.mousePos != null) {
                            Entity entity = result.entityHit;

                            if (result.entityHit != null && this.mousePos.equals(new BlockPos(entity.posX, entity.posY - 1.0D, entity.posZ))) {
                                FastPlace.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.mousePos, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                            }
                        }
                    }
                }
            }

        }
    }

    private boolean lambda$new$4(Object v) {
        return !((Boolean) this.all.getValue()).booleanValue();
    }

    private boolean lambda$new$3(Object v) {
        return !((Boolean) this.all.getValue()).booleanValue();
    }

    private boolean lambda$new$2(Object v) {
        return !((Boolean) this.all.getValue()).booleanValue();
    }

    private boolean lambda$new$1(Object v) {
        return !((Boolean) this.all.getValue()).booleanValue();
    }

    private boolean lambda$new$0(Object v) {
        return !((Boolean) this.all.getValue()).booleanValue();
    }
}
