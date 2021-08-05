package club.tater.tatergod.features.modules.movement;

import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.Timer;
import club.tater.tatergod.util.Util;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoFall extends Module {

    private static final Timer bypassTimer = new Timer();
    private static int ogslot = -1;
    private final Setting mode;
    private final Setting distance;
    private final Setting glide;
    private final Setting silent;
    private final Setting bypass;
    private final Timer timer;
    private boolean equipped;
    private boolean gotElytra;
    private NoFall.State currentState;

    public NoFall() {
        super("NoFall", "Prevents fall damage.", Module.Category.MOVEMENT, true, false, false);
        this.mode = this.register(new Setting("Mode", NoFall.Mode.PACKET));
        this.distance = this.register(new Setting("Distance", Integer.valueOf(15), Integer.valueOf(0), Integer.valueOf(50), test<invokedynamic>(this)));
        this.glide = this.register(new Setting("Glide", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.silent = this.register(new Setting("Silent", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.bypass = this.register(new Setting("Bypass", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.timer = new Timer();
        this.equipped = false;
        this.gotElytra = false;
        this.currentState = NoFall.State.FALL_CHECK;
    }

    public void onEnable() {
        NoFall.ogslot = -1;
        this.currentState = NoFall.State.FALL_CHECK;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!fullNullCheck()) {
            if (this.mode.getValue() == NoFall.Mode.ELYTRA) {
                if (((Boolean) this.bypass.getValue()).booleanValue()) {
                    this.currentState = this.currentState.onSend(event);
                } else if (!this.equipped && event.getPacket() instanceof CPacketPlayer && NoFall.mc.player.fallDistance >= 3.0F) {
                    RayTraceResult packet = null;

                    if (!((Boolean) this.glide.getValue()).booleanValue()) {
                        packet = NoFall.mc.world.rayTraceBlocks(NoFall.mc.player.getPositionVector(), NoFall.mc.player.getPositionVector().add(0.0D, -3.0D, 0.0D), true, true, false);
                    }

                    if (((Boolean) this.glide.getValue()).booleanValue() || packet != null && packet.typeOfHit == Type.BLOCK) {
                        if (NoFall.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem().equals(Items.ELYTRA)) {
                            NoFall.mc.player.connection.sendPacket(new CPacketEntityAction(NoFall.mc.player, Action.START_FALL_FLYING));
                        } else if (((Boolean) this.silent.getValue()).booleanValue()) {
                            int slot = InventoryUtil.getItemHotbar(Items.ELYTRA);

                            if (slot != -1) {
                                NoFall.mc.playerController.windowClick(NoFall.mc.player.inventoryContainer.windowId, 6, slot, ClickType.SWAP, NoFall.mc.player);
                                NoFall.mc.player.connection.sendPacket(new CPacketEntityAction(NoFall.mc.player, Action.START_FALL_FLYING));
                            }

                            NoFall.ogslot = slot;
                            this.equipped = true;
                        }
                    }
                }
            }

            if (this.mode.getValue() == NoFall.Mode.PACKET && event.getPacket() instanceof CPacketPlayer) {
                CPacketPlayer packet1 = (CPacketPlayer) event.getPacket();

                packet1.onGround = true;
            }

        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!fullNullCheck()) {
            if ((this.equipped || ((Boolean) this.bypass.getValue()).booleanValue()) && this.mode.getValue() == NoFall.Mode.ELYTRA && (event.getPacket() instanceof SPacketWindowItems || event.getPacket() instanceof SPacketSetSlot)) {
                if (((Boolean) this.bypass.getValue()).booleanValue()) {
                    this.currentState = this.currentState.onReceive(event);
                } else {
                    this.gotElytra = true;
                }
            }

        }
    }

    public void onUpdate() {
        if (!fullNullCheck()) {
            if (this.mode.getValue() == NoFall.Mode.ELYTRA) {
                if (((Boolean) this.bypass.getValue()).booleanValue()) {
                    this.currentState = this.currentState.onUpdate();
                } else if (((Boolean) this.silent.getValue()).booleanValue() && this.equipped && this.gotElytra) {
                    NoFall.mc.playerController.windowClick(NoFall.mc.player.inventoryContainer.windowId, 6, NoFall.ogslot, ClickType.SWAP, NoFall.mc.player);
                    NoFall.mc.playerController.updateController();
                    this.equipped = false;
                    this.gotElytra = false;
                } else {
                    int slot;

                    if (((Boolean) this.silent.getValue()).booleanValue() && InventoryUtil.getItemHotbar(Items.ELYTRA) == -1 && (slot = InventoryUtil.findStackInventory(Items.ELYTRA)) != -1 && NoFall.ogslot != -1) {
                        System.out.println(String.format("Moving %d to hotbar %d", new Object[] { Integer.valueOf(slot), Integer.valueOf(NoFall.ogslot)}));
                        NoFall.mc.playerController.windowClick(NoFall.mc.player.inventoryContainer.windowId, slot, NoFall.ogslot, ClickType.SWAP, NoFall.mc.player);
                        NoFall.mc.playerController.updateController();
                    }
                }
            }

        }
    }

    public void onTick() {
        if (!fullNullCheck()) {
            Vec3d posVec;
            RayTraceResult result;

            if (this.mode.getValue() == NoFall.Mode.BUCKET && NoFall.mc.player.fallDistance >= (float) ((Integer) this.distance.getValue()).intValue() && !EntityUtil.isAboveWater(NoFall.mc.player) && this.timer.passedMs(100L) && (result = NoFall.mc.world.rayTraceBlocks(posVec = NoFall.mc.player.getPositionVector(), posVec.add(0.0D, -5.329999923706055D, 0.0D), true, true, false)) != null && result.typeOfHit == Type.BLOCK) {
                EnumHand hand = EnumHand.MAIN_HAND;

                if (NoFall.mc.player.getHeldItemOffhand().getItem() == Items.WATER_BUCKET) {
                    hand = EnumHand.OFF_HAND;
                } else if (NoFall.mc.player.getHeldItemMainhand().getItem() != Items.WATER_BUCKET) {
                    for (int i = 0; i < 9; ++i) {
                        if (NoFall.mc.player.inventory.getStackInSlot(i).getItem() == Items.WATER_BUCKET) {
                            NoFall.mc.player.inventory.currentItem = i;
                            NoFall.mc.player.rotationPitch = 90.0F;
                            this.timer.reset();
                            return;
                        }
                    }

                    return;
                }

                NoFall.mc.player.rotationPitch = 90.0F;
                NoFall.mc.playerController.processRightClick(NoFall.mc.player, NoFall.mc.world, hand);
                this.timer.reset();
            }

        }
    }

    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }

    private boolean lambda$new$3(Object v) {
        return this.mode.getValue() == NoFall.Mode.ELYTRA;
    }

    private boolean lambda$new$2(Object v) {
        return this.mode.getValue() == NoFall.Mode.ELYTRA;
    }

    private boolean lambda$new$1(Object v) {
        return this.mode.getValue() == NoFall.Mode.ELYTRA;
    }

    private boolean lambda$new$0(Object v) {
        return this.mode.getValue() == NoFall.Mode.BUCKET;
    }

    public static enum Mode {

        PACKET, BUCKET, ELYTRA;
    }

    public static enum State {

        FALL_CHECK {;
            public NoFall.State onSend(PacketEvent.Send event) {
                RayTraceResult result = Util.mc.world.rayTraceBlocks(Util.mc.player.getPositionVector(), Util.mc.player.getPositionVector().add(0.0D, -3.0D, 0.0D), true, true, false);

                if (event.getPacket() instanceof CPacketPlayer && Util.mc.player.fallDistance >= 3.0F && result != null && result.typeOfHit == Type.BLOCK) {
                    int slot = InventoryUtil.getItemHotbar(Items.ELYTRA);

                    if (slot != -1) {
                        Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 6, slot, ClickType.SWAP, Util.mc.player);
                        NoFall.ogslot = slot;
                        Util.mc.player.connection.sendPacket(new CPacketEntityAction(Util.mc.player, Action.START_FALL_FLYING));
                        return null.WAIT_FOR_ELYTRA_DEQUIP;
                    } else {
                        return this;
                    }
                } else {
                    return this;
                }
            }
        }, WAIT_FOR_ELYTRA_DEQUIP {;
    public NoFall.State onReceive(PacketEvent.Receive event) {
        return (NoFall.State) (!(event.getPacket() instanceof SPacketWindowItems) && !(event.getPacket() instanceof SPacketSetSlot) ? this : null.REEQUIP_ELYTRA);
    }
}, REEQUIP_ELYTRA {;
    public NoFall.State onUpdate() {
        Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 6, NoFall.ogslot, ClickType.SWAP, Util.mc.player);
        Util.mc.playerController.updateController();
        int slot = InventoryUtil.findStackInventory(Items.ELYTRA, true);

        if (slot == -1) {
            return null.WAIT_FOR_NEXT_REQUIP;
        } else {
            Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, slot, NoFall.ogslot, ClickType.SWAP, Util.mc.player);
            Util.mc.playerController.updateController();
            NoFall.bypassTimer.reset();
            return null.RESET_TIME;
        }
    }
}, WAIT_FOR_NEXT_REQUIP {;
    public NoFall.State onUpdate() {
        return (NoFall.State) (NoFall.bypassTimer.passedMs(250L) ? null.REEQUIP_ELYTRA : this);
    }
}, RESET_TIME {;
    public NoFall.State onUpdate() {
        if (!Util.mc.player.onGround && !NoFall.bypassTimer.passedMs(250L)) {
            return this;
        } else {
            Util.mc.player.connection.sendPacket(new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, new ItemStack(Blocks.BEDROCK), (short) 1337));
            return null.FALL_CHECK;
        }
    }
};

        private State() {}

        public NoFall.State onSend(PacketEvent.Send e) {
            return this;
        }

        public NoFall.State onReceive(PacketEvent.Receive e) {
            return this;
        }

        public NoFall.State onUpdate() {
            return this;
        }

        State(Object x2) {
            this();
        }
    }
}
