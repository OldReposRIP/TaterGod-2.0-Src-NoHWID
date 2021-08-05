package club.tater.tatergod.util;

import club.tater.tatergod.Tater;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryUtil implements Util {

    public static void switchToHotbarSlot(int slot, boolean silent) {
        if (InventoryUtil.mc.player.inventory.currentItem != slot && slot >= 0) {
            if (silent) {
                InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                InventoryUtil.mc.playerController.updateController();
            } else {
                InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                InventoryUtil.mc.player.inventory.currentItem = slot;
                InventoryUtil.mc.playerController.updateController();
            }

        }
    }

    public static void switchToHotbarSlot(Class clazz, boolean silent) {
        int slot = findHotbarBlock(clazz);

        if (slot > -1) {
            switchToHotbarSlot(slot, silent);
        }

    }

    public static boolean isNull(ItemStack stack) {
        return stack == null || stack.getItem() instanceof ItemAir;
    }

    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);

            if (stack != ItemStack.EMPTY) {
                if (clazz.isInstance(stack.getItem())) {
                    return i;
                }

                if (stack.getItem() instanceof ItemBlock && clazz.isInstance(((ItemBlock) stack.getItem()).getBlock())) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int findHotbarBlock(Block blockIn) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);

            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == blockIn) {
                return i;
            }
        }

        return -1;
    }

    public static int getItemHotbar(Item input) {
        for (int i = 0; i < 9; ++i) {
            Item item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();

            if (Item.getIdFromItem(item) == Item.getIdFromItem(input)) {
                return i;
            }
        }

        return -1;
    }

    public static int findStackInventory(Item input) {
        return findStackInventory(input, false);
    }

    public static int findStackInventory(Item input, boolean withHotbar) {
        for (int i = withHotbar ? 0 : 9; i < 36; ++i) {
            Item item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();

            if (Item.getIdFromItem(input) == Item.getIdFromItem(item)) {
                return i + (i < 9 ? 36 : 0);
            }
        }

        return -1;
    }

    public static int findItemInventorySlot(Item item, boolean offHand) {
        AtomicInteger slot = new AtomicInteger();

        slot.set(-1);
        Iterator iterator = getInventoryAndHotbarSlots().entrySet().iterator();

        Entry entry;

        do {
            do {
                if (!iterator.hasNext()) {
                    return slot.get();
                }

                entry = (Entry) iterator.next();
            } while (((ItemStack) entry.getValue()).getItem() != item);
        } while (((Integer) entry.getKey()).intValue() == 45 && !offHand);

        slot.set(((Integer) entry.getKey()).intValue());
        return slot.get();
    }

    public static List findEmptySlots(boolean withXCarry) {
        ArrayList outPut = new ArrayList();
        Iterator i = getInventoryAndHotbarSlots().entrySet().iterator();

        while (i.hasNext()) {
            Entry craftingSlot = (Entry) i.next();

            if (((ItemStack) craftingSlot.getValue()).isEmpty || ((ItemStack) craftingSlot.getValue()).getItem() == Items.AIR) {
                outPut.add(craftingSlot.getKey());
            }
        }

        if (withXCarry) {
            for (int i = 1; i < 5; ++i) {
                Slot slot = (Slot) InventoryUtil.mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = slot.getStack();

                if (craftingStack.isEmpty() || craftingStack.getItem() == Items.AIR) {
                    outPut.add(Integer.valueOf(i));
                }
            }
        }

        return outPut;
    }

    public static int findInventoryBlock(Class clazz, boolean offHand) {
        AtomicInteger slot = new AtomicInteger();

        slot.set(-1);
        Iterator iterator = getInventoryAndHotbarSlots().entrySet().iterator();

        Entry entry;

        do {
            do {
                if (!iterator.hasNext()) {
                    return slot.get();
                }

                entry = (Entry) iterator.next();
            } while (!isBlock(((ItemStack) entry.getValue()).getItem(), clazz));
        } while (((Integer) entry.getKey()).intValue() == 45 && !offHand);

        slot.set(((Integer) entry.getKey()).intValue());
        return slot.get();
    }

    public static boolean isBlock(Item item, Class clazz) {
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();

            return clazz.isInstance(block);
        } else {
            return false;
        }
    }

    public static void confirmSlot(int slot) {
        InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        InventoryUtil.mc.player.inventory.currentItem = slot;
        InventoryUtil.mc.playerController.updateController();
    }

    public static Map getInventoryAndHotbarSlots() {
        return getInventorySlots(9, 44);
    }

    private static Map getInventorySlots(int currentI, int last) {
        HashMap fullInventorySlots = new HashMap();

        for (int current = currentI; current <= last; ++current) {
            fullInventorySlots.put(Integer.valueOf(current), InventoryUtil.mc.player.inventoryContainer.getInventory().get(current));
        }

        return fullInventorySlots;
    }

    public static boolean[] switchItem(boolean back, int lastHotbarSlot, boolean switchedItem, InventoryUtil.Switch mode, Class clazz) {
        boolean[] switchedItemSwitched = new boolean[] { switchedItem, false};

        switch (mode) {
        case NORMAL:
            if (!back && !switchedItem) {
                switchToHotbarSlot(findHotbarBlock(clazz), false);
                switchedItemSwitched[0] = true;
            } else if (back && switchedItem) {
                switchToHotbarSlot(lastHotbarSlot, false);
                switchedItemSwitched[0] = false;
            }

            switchedItemSwitched[1] = true;
            break;

        case SILENT:
            if (!back && !switchedItem) {
                switchToHotbarSlot(findHotbarBlock(clazz), true);
                switchedItemSwitched[0] = true;
            } else if (back && switchedItem) {
                switchedItemSwitched[0] = false;
                Tater.inventoryManager.recoverSilent(lastHotbarSlot);
            }

            switchedItemSwitched[1] = true;
            break;

        case NONE:
            switchedItemSwitched[1] = back || InventoryUtil.mc.player.inventory.currentItem == findHotbarBlock(clazz);
        }

        return switchedItemSwitched;
    }

    public static boolean[] switchItemToItem(boolean back, int lastHotbarSlot, boolean switchedItem, InventoryUtil.Switch mode, Item item) {
        boolean[] switchedItemSwitched = new boolean[] { switchedItem, false};

        switch (mode) {
        case NORMAL:
            if (!back && !switchedItem) {
                switchToHotbarSlot(getItemHotbar(item), false);
                switchedItemSwitched[0] = true;
            } else if (back && switchedItem) {
                switchToHotbarSlot(lastHotbarSlot, false);
                switchedItemSwitched[0] = false;
            }

            switchedItemSwitched[1] = true;
            break;

        case SILENT:
            if (!back && !switchedItem) {
                switchToHotbarSlot(getItemHotbar(item), true);
                switchedItemSwitched[0] = true;
            } else if (back && switchedItem) {
                switchedItemSwitched[0] = false;
                Tater.inventoryManager.recoverSilent(lastHotbarSlot);
            }

            switchedItemSwitched[1] = true;
            break;

        case NONE:
            switchedItemSwitched[1] = back || InventoryUtil.mc.player.inventory.currentItem == getItemHotbar(item);
        }

        return switchedItemSwitched;
    }

    public static boolean holdingItem(Class clazz) {
        boolean result = false;
        ItemStack stack = InventoryUtil.mc.player.getHeldItemMainhand();

        result = isInstanceOf(stack, clazz);
        if (!result) {
            ItemStack offhand = InventoryUtil.mc.player.getHeldItemOffhand();

            result = isInstanceOf(stack, clazz);
        }

        return result;
    }

    public static boolean isInstanceOf(ItemStack stack, Class clazz) {
        if (stack == null) {
            return false;
        } else {
            Item item = stack.getItem();

            if (clazz.isInstance(item)) {
                return true;
            } else if (item instanceof ItemBlock) {
                Block block = Block.getBlockFromItem(item);

                return clazz.isInstance(block);
            } else {
                return false;
            }
        }
    }

    public static int getEmptyXCarry() {
        for (int i = 1; i < 5; ++i) {
            Slot craftingSlot = (Slot) InventoryUtil.mc.player.inventoryContainer.inventorySlots.get(i);
            ItemStack craftingStack = craftingSlot.getStack();

            if (craftingStack.isEmpty() || craftingStack.getItem() == Items.AIR) {
                return i;
            }
        }

        return -1;
    }

    public static boolean isSlotEmpty(int i) {
        Slot slot = (Slot) InventoryUtil.mc.player.inventoryContainer.inventorySlots.get(i);
        ItemStack stack = slot.getStack();

        return stack.isEmpty();
    }

    public static int convertHotbarToInv(int input) {
        return 36 + input;
    }

    public static boolean areStacksCompatible(ItemStack stack1, ItemStack stack2) {
        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        } else {
            if (stack1.getItem() instanceof ItemBlock && stack2.getItem() instanceof ItemBlock) {
                Block block1 = ((ItemBlock) stack1.getItem()).getBlock();
                Block block2 = ((ItemBlock) stack2.getItem()).getBlock();

                if (!block1.material.equals(block2.material)) {
                    return false;
                }
            }

            return !stack1.getDisplayName().equals(stack2.getDisplayName()) ? false : stack1.getItemDamage() == stack2.getItemDamage();
        }
    }

    public static EntityEquipmentSlot getEquipmentFromSlot(int slot) {
        return slot == 5 ? EntityEquipmentSlot.HEAD : (slot == 6 ? EntityEquipmentSlot.CHEST : (slot == 7 ? EntityEquipmentSlot.LEGS : EntityEquipmentSlot.FEET));
    }

    public static int findArmorSlot(EntityEquipmentSlot type, boolean binding) {
        int slot = -1;
        float damage = 0.0F;

        for (int i = 9; i < 45; ++i) {
            ItemStack s = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getStack();

            if (s.getItem() != Items.AIR && s.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) s.getItem();

                if (armor.armorType == type) {
                    float currentDamage = (float) (armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, s));
                    boolean cursed = binding && EnchantmentHelper.hasBindingCurse(s);

                    if (currentDamage > damage && !cursed) {
                        damage = currentDamage;
                        slot = i;
                    }
                }
            }
        }

        return slot;
    }

    public static int findArmorSlot(EntityEquipmentSlot type, boolean binding, boolean withXCarry) {
        int slot = findArmorSlot(type, binding);

        if (slot == -1 && withXCarry) {
            float damage = 0.0F;

            for (int i = 1; i < 5; ++i) {
                Slot craftingSlot = (Slot) InventoryUtil.mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();

                if (craftingStack.getItem() != Items.AIR && craftingStack.getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) craftingStack.getItem();

                    if (armor.armorType == type) {
                        float currentDamage = (float) (armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, craftingStack));
                        boolean cursed = binding && EnchantmentHelper.hasBindingCurse(craftingStack);

                        if (currentDamage > damage && !cursed) {
                            damage = currentDamage;
                            slot = i;
                        }
                    }
                }
            }
        }

        return slot;
    }

    public static int findItemInventorySlot(Item item, boolean offHand, boolean withXCarry) {
        int slot = findItemInventorySlot(item, offHand);

        if (slot == -1 && withXCarry) {
            for (int i = 1; i < 5; ++i) {
                Slot craftingSlot = (Slot) InventoryUtil.mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();

                if (craftingStack.getItem() != Items.AIR && craftingStack.getItem() == item) {
                    slot = i;
                }
            }
        }

        return slot;
    }

    public static int findBlockSlotInventory(Class clazz, boolean offHand, boolean withXCarry) {
        int slot = findInventoryBlock(clazz, offHand);

        if (slot == -1 && withXCarry) {
            for (int i = 1; i < 5; ++i) {
                Slot craftingSlot = (Slot) InventoryUtil.mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();

                if (craftingStack.getItem() != Items.AIR) {
                    Item craftingStackItem = craftingStack.getItem();

                    if (clazz.isInstance(craftingStackItem)) {
                        slot = i;
                    } else if (craftingStackItem instanceof ItemBlock && clazz.isInstance(((ItemBlock) craftingStackItem).getBlock())) {
                        slot = i;
                    }
                }
            }
        }

        return slot;
    }

    public static int findItemInHotbar(Item item) {
        int index = -1;

        for (int i = 0; i < 9; ++i) {
            if (InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem() == item) {
                index = i;
                break;
            }
        }

        return index;
    }

    public static class Task {

        private final int slot;
        private final boolean update;
        private final boolean quickClick;

        public Task() {
            this.update = true;
            this.slot = -1;
            this.quickClick = false;
        }

        public Task(int slot) {
            this.slot = slot;
            this.quickClick = false;
            this.update = false;
        }

        public Task(int slot, boolean quickClick) {
            this.slot = slot;
            this.quickClick = quickClick;
            this.update = false;
        }

        public void run() {
            if (this.update) {
                Util.mc.playerController.updateController();
            }

            if (this.slot != -1) {
                Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, Util.mc.player);
            }

        }

        public boolean isSwitching() {
            return !this.update;
        }
    }

    public static enum Switch {

        NORMAL, SILENT, NONE;
    }
}
