package club.tater.tatergod.features.modules.misc;

import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;

public class PopCounter extends Module {

    public static HashMap TotemPopContainer = new HashMap();
    public static PopCounter INSTANCE = new PopCounter();
    public final Setting clientname = this.register(new Setting("Name", "TaterGod.CC"));

    public PopCounter() {
        super("PopCounter", "Counts other players totem pops.", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static PopCounter getInstance() {
        if (PopCounter.INSTANCE == null) {
            PopCounter.INSTANCE = new PopCounter();
        }

        return PopCounter.INSTANCE;
    }

    private void setInstance() {
        PopCounter.INSTANCE = this;
    }

    public void onEnable() {
        PopCounter.TotemPopContainer.clear();
    }

    public void onDeath(EntityPlayer player) {
        if (PopCounter.TotemPopContainer.containsKey(player.getName())) {
            int l_Count = ((Integer) PopCounter.TotemPopContainer.get(player.getName())).intValue();

            PopCounter.TotemPopContainer.remove(player.getName());
            if (l_Count == 1) {
                Command.sendSilentMessage(ChatFormatting.RED + player.getName() + " died after popping " + ChatFormatting.GRAY + l_Count + ChatFormatting.RED + ChatFormatting.RED + " totem, thanks to " + this.clientname.getValueAsString());
            } else {
                Command.sendSilentMessage(ChatFormatting.RED + player.getName() + " died after popping " + ChatFormatting.GRAY + l_Count + ChatFormatting.RED + ChatFormatting.RED + " totems, thanks to " + this.clientname.getValueAsString());
            }
        }

    }

    public void onTotemPop(EntityPlayer player) {
        if (!fullNullCheck()) {
            if (!PopCounter.mc.player.equals(player)) {
                int l_Count = 1;

                if (PopCounter.TotemPopContainer.containsKey(player.getName())) {
                    l_Count = ((Integer) PopCounter.TotemPopContainer.get(player.getName())).intValue();
                    HashMap hashmap = PopCounter.TotemPopContainer;
                    String s = player.getName();

                    ++l_Count;
                    hashmap.put(s, Integer.valueOf(l_Count));
                } else {
                    PopCounter.TotemPopContainer.put(player.getName(), Integer.valueOf(l_Count));
                }

                if (l_Count == 1) {
                    Command.sendSilentMessage(ChatFormatting.RED + player.getName() + " popped " + ChatFormatting.GRAY + l_Count + ChatFormatting.RED + " totem, " + ChatFormatting.RED + "thanks to " + this.clientname.getValueAsString());
                } else {
                    Command.sendSilentMessage(ChatFormatting.RED + player.getName() + " popped " + ChatFormatting.GRAY + l_Count + ChatFormatting.RED + " totems, " + ChatFormatting.RED + "thanks to " + this.clientname.getValueAsString());
                }

            }
        }
    }
}
