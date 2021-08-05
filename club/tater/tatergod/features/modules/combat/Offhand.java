package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.event.events.ProcessRightClickBlockEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.Timer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class Offhand extends Module {

    private static Offhand instance;
    private final Queue taskList = new ConcurrentLinkedQueue();
    private final Timer timer = new Timer();
    private final Timer secondTimer = new Timer();
    public Setting crystal = this.register(new Setting("Crystal", Boolean.valueOf(true)));
    public Setting crystalHealth = this.register(new Setting("CrystalHP", Float.valueOf(15.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
    public Setting crystalHoleHealth = this.register(new Setting("CrystalHoleHP", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
    public Setting gapple = this.register(new Setting("Gapple", Boolean.valueOf(true)));
    public Setting armorCheck = this.register(new Setting("ArmorCheck", Boolean.valueOf(true)));
    public Setting actions = this.register(new Setting("Packets", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(4)));
    public Offhand.Mode2 currentMode;
    public int totems;
    public int crystals;
    public int gapples;
    public int lastTotemSlot;
    public int lastGappleSlot;
    public int lastCrystalSlot;
    public int lastObbySlot;
    public int lastWebSlot;
    public boolean holdingCrystal;
    public boolean holdingTotem;
    public boolean holdingGapple;
    public boolean didSwitchThisTick;
    private boolean second;
    private boolean switchedForHealthReason;

    public Offhand() {
        super("Offhand", "Allows you to switch up your Offhand.", Module.Category.COMBAT, true, false, false);
        this.currentMode = Offhand.Mode2.TOTEMS;
        this.totems = 0;
        this.crystals = 0;
        this.gapples = 0;
        this.lastTotemSlot = -1;
        this.lastGappleSlot = -1;
        this.lastCrystalSlot = -1;
        this.lastObbySlot = -1;
        this.lastWebSlot = -1;
        this.holdingCrystal = false;
        this.holdingTotem = false;
        this.holdingGapple = false;
        this.didSwitchThisTick = false;
        this.second = false;
        this.switchedForHealthReason = false;
        Offhand.instance = this;
    }

    public static Offhand getInstance() {
        if (Offhand.instance == null) {
            Offhand.instance = new Offhand();
        }

        return Offhand.instance;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(ProcessRightClickBlockEvent event) {
        if (event.hand == EnumHand.MAIN_HAND && event.stack.getItem() == Items.END_CRYSTAL && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.objectMouseOver != null && event.pos == Offhand.mc.objectMouseOver.getBlockPos()) {
            event.setCanceled(true);
            Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
            Offhand.mc.playerController.processRightClick(Offhand.mc.player, Offhand.mc.world, EnumHand.OFF_HAND);
        }

    }

    public void onUpdate() {
        if (this.timer.passedMs(50L)) {
            if (Offhand.mc.player != null && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Mouse.isButtonDown(1)) {
                Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
                Offhand.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
            }
        } else if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            Offhand.mc.gameSettings.keyBindUseItem.pressed = false;
        }

        if (!nullCheck()) {
            this.doOffhand();
            if (this.secondTimer.passedMs(50L) && this.second) {
                this.second = false;
                this.timer.reset();
            }

        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!fullNullCheck() && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock) event.getPacket();

                if (packet2.getHand() == EnumHand.MAIN_HAND) {
                    if (this.timer.passedMs(50L)) {
                        Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
                        Offhand.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                    }

                    event.setCanceled(true);
                }
            } else if (event.getPacket() instanceof CPacketPlayerTryUseItem && ((CPacketPlayerTryUseItem) event.getPacket()).getHand() == EnumHand.OFF_HAND && !this.timer.passedMs(50L)) {
                event.setCanceled(true);
            }
        }

    }

    public String getDisplayInfo() {
        return Offhand.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? "Crystal" : (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING ? "Totem" : (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE ? "Gapple" : null));
    }

    public void doOffhand() {
        this.didSwitchThisTick = false;
        this.holdingCrystal = Offhand.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        this.holdingTotem = Offhand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING;
        this.holdingGapple = Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE;
        this.totems = Offhand.mc.player.inventory.mainInventory.stream().filter(test<invokedynamic>()).mapToInt(applyAsInt<invokedynamic>()).sum();
        if (this.holdingTotem) {
            this.totems += Offhand.mc.player.inventory.offHandInventory.stream().filter(test<invokedynamic>()).mapToInt(applyAsInt<invokedynamic>()).sum();
        }

        this.crystals = Offhand.mc.player.inventory.mainInventory.stream().filter(test<invokedynamic>()).mapToInt(applyAsInt<invokedynamic>()).sum();
        if (this.holdingCrystal) {
            this.crystals += Offhand.mc.player.inventory.offHandInventory.stream().filter(test<invokedynamic>()).mapToInt(applyAsInt<invokedynamic>()).sum();
        }

        this.gapples = Offhand.mc.player.inventory.mainInventory.stream().filter(test<invokedynamic>()).mapToInt(applyAsInt<invokedynamic>()).sum();
        if (this.holdingGapple) {
            this.gapples += Offhand.mc.player.inventory.offHandInventory.stream().filter(test<invokedynamic>()).mapToInt(applyAsInt<invokedynamic>()).sum();
        }

        this.doSwitch();
    }

    public void doSwitch() {
        this.currentMode = Offhand.Mode2.TOTEMS;
        if (((Boolean) this.gapple.getValue()).booleanValue() && Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            this.currentMode = Offhand.Mode2.GAPPLES;
        } else if (this.currentMode != Offhand.Mode2.CRYSTALS && ((Boolean) this.crystal.getValue()).booleanValue() && (EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > ((Float) this.crystalHoleHealth.getValue()).floatValue() || EntityUtil.getHealth(Offhand.mc.player, true) > ((Float) this.crystalHealth.getValue()).floatValue())) {
            this.currentMode = Offhand.Mode2.CRYSTALS;
        }

        if (this.currentMode == Offhand.Mode2.CRYSTALS && this.crystals == 0) {
            this.setMode(Offhand.Mode2.TOTEMS);
        }

        if (this.currentMode == Offhand.Mode2.CRYSTALS && (!EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) <= ((Float) this.crystalHealth.getValue()).floatValue() || EntityUtil.getHealth(Offhand.mc.player, true) <= ((Float) this.crystalHoleHealth.getValue()).floatValue())) {
            if (this.currentMode == Offhand.Mode2.CRYSTALS) {
                this.switchedForHealthReason = true;
            }

            this.setMode(Offhand.Mode2.TOTEMS);
        }

        if (this.switchedForHealthReason && (EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > ((Float) this.crystalHoleHealth.getValue()).floatValue() || EntityUtil.getHealth(Offhand.mc.player, true) > ((Float) this.crystalHealth.getValue()).floatValue())) {
            this.setMode(Offhand.Mode2.CRYSTALS);
            this.switchedForHealthReason = false;
        }

        if (this.currentMode == Offhand.Mode2.CRYSTALS && ((Boolean) this.armorCheck.getValue()).booleanValue() && (Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR)) {
            this.setMode(Offhand.Mode2.TOTEMS);
        }

        if (!(Offhand.mc.currentScreen instanceof GuiContainer) || Offhand.mc.currentScreen instanceof GuiInventory) {
            Item currentOffhandItem = Offhand.mc.player.getHeldItemOffhand().getItem();
            int i;

            switch (this.currentMode) {
            case TOTEMS:
                if (this.totems > 0 && !this.holdingTotem) {
                    this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
                    i = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
                    this.putItemInOffhand(this.lastTotemSlot, i);
                }
                break;

            case GAPPLES:
                if (this.gapples > 0 && !this.holdingGapple) {
                    this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
                    i = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
                    this.putItemInOffhand(this.lastGappleSlot, i);
                }
                break;

            default:
                if (this.crystals > 0 && !this.holdingCrystal) {
                    this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
                    i = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
                    this.putItemInOffhand(this.lastCrystalSlot, i);
                }
            }

            for (i = 0; i < ((Integer) this.actions.getValue()).intValue(); ++i) {
                InventoryUtil.Task task = (InventoryUtil.Task) this.taskList.poll();

                if (task != null) {
                    task.run();
                    if (task.isSwitching()) {
                        this.didSwitchThisTick = true;
                    }
                }
            }

        }
    }

    private int getLastSlot(Item item, int slotIn) {
        return item == Items.END_CRYSTAL ? this.lastCrystalSlot : (item == Items.GOLDEN_APPLE ? this.lastGappleSlot : (item == Items.TOTEM_OF_UNDYING ? this.lastTotemSlot : (InventoryUtil.isBlock(item, BlockObsidian.class) ? this.lastObbySlot : (InventoryUtil.isBlock(item, BlockWeb.class) ? this.lastWebSlot : (item == Items.AIR ? -1 : slotIn)))));
    }

    private void putItemInOffhand(int slotIn, int slotOut) {
        if (slotIn != -1 && this.taskList.isEmpty()) {
            this.taskList.add(new InventoryUtil.Task(slotIn));
            this.taskList.add(new InventoryUtil.Task(45));
            this.taskList.add(new InventoryUtil.Task(slotOut));
            this.taskList.add(new InventoryUtil.Task());
        }

    }

    public void setMode(Offhand.Mode2 mode) {
        this.currentMode = this.currentMode == mode ? Offhand.Mode2.TOTEMS : mode;
    }

    private static boolean lambda$doOffhand$5(ItemStack itemStack) {
        return itemStack.getItem() == Items.GOLDEN_APPLE;
    }

    private static boolean lambda$doOffhand$4(ItemStack itemStack) {
        return itemStack.getItem() == Items.GOLDEN_APPLE;
    }

    private static boolean lambda$doOffhand$3(ItemStack itemStack) {
        return itemStack.getItem() == Items.END_CRYSTAL;
    }

    private static boolean lambda$doOffhand$2(ItemStack itemStack) {
        return itemStack.getItem() == Items.END_CRYSTAL;
    }

    private static boolean lambda$doOffhand$1(ItemStack itemStack) {
        return itemStack.getItem() == Items.TOTEM_OF_UNDYING;
    }

    private static boolean lambda$doOffhand$0(ItemStack itemStack) {
        return itemStack.getItem() == Items.TOTEM_OF_UNDYING;
    }

    public static enum Mode2 {

        TOTEMS, GAPPLES, CRYSTALS;
    }
}
