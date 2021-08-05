package club.tater.tatergod.features.modules.player;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.Timer;
import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

public class Replenish extends Module {

    private final Setting delay = this.register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(10)));
    private final Setting gapStack = this.register(new Setting("GapStack", Integer.valueOf(16), Integer.valueOf(1), Integer.valueOf(63)));
    private final Setting xpStackAt = this.register(new Setting("XPStack", Integer.valueOf(16), Integer.valueOf(1), Integer.valueOf(63)));
    private final Timer timer = new Timer();
    private final ArrayList Hotbar = new ArrayList();

    public Replenish() {
        super("Replenish", "Replenishes your hotbar", Module.Category.PLAYER, false, false, false);
    }

    public void onEnable() {
        if (!fullNullCheck()) {
            this.Hotbar.clear();

            for (int l_I = 0; l_I < 9; ++l_I) {
                ItemStack l_Stack = Replenish.mc.player.inventory.getStackInSlot(l_I);

                if (!l_Stack.isEmpty() && !this.Hotbar.contains(l_Stack.getItem())) {
                    this.Hotbar.add(l_Stack.getItem());
                } else {
                    this.Hotbar.add(Items.AIR);
                }
            }

        }
    }

    public void onUpdate() {
        if (Replenish.mc.currentScreen == null) {
            if (this.timer.passedMs((long) (((Integer) this.delay.getValue()).intValue() * 1000))) {
                for (int l_I = 0; l_I < 9; ++l_I) {
                    if (this.RefillSlotIfNeed(l_I)) {
                        this.timer.reset();
                        return;
                    }
                }

            }
        }
    }

    private boolean RefillSlotIfNeed(int p_Slot) {
        ItemStack l_Stack = Replenish.mc.player.inventory.getStackInSlot(p_Slot);

        if (!l_Stack.isEmpty() && l_Stack.getItem() != Items.AIR) {
            if (!l_Stack.isStackable()) {
                return false;
            } else if (l_Stack.getCount() >= l_Stack.getMaxStackSize()) {
                return false;
            } else if (l_Stack.getItem().equals(Items.GOLDEN_APPLE) && l_Stack.getCount() >= ((Integer) this.gapStack.getValue()).intValue()) {
                return false;
            } else if (l_Stack.getItem().equals(Items.EXPERIENCE_BOTTLE) && l_Stack.getCount() > ((Integer) this.xpStackAt.getValue()).intValue()) {
                return false;
            } else {
                for (int l_I = 9; l_I < 36; ++l_I) {
                    ItemStack l_Item = Replenish.mc.player.inventory.getStackInSlot(l_I);

                    if (!l_Item.isEmpty() && this.CanItemBeMergedWith(l_Stack, l_Item)) {
                        Replenish.mc.playerController.windowClick(Replenish.mc.player.inventoryContainer.windowId, l_I, 0, ClickType.QUICK_MOVE, Replenish.mc.player);
                        Replenish.mc.playerController.updateController();
                        return true;
                    }
                }

                return false;
            }
        } else {
            return false;
        }
    }

    private boolean CanItemBeMergedWith(ItemStack p_Source, ItemStack p_Target) {
        return p_Source.getItem() == p_Target.getItem() && p_Source.getDisplayName().equals(p_Target.getDisplayName());
    }
}
