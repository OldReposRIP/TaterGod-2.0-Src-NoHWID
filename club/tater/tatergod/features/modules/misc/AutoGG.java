package club.tater.tatergod.features.modules.misc;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoGG extends Module {

    private static AutoGG INSTANCE = new AutoGG();
    public Setting custom = this.register(new Setting("Custom", "TaterGod.CC"));
    public Setting test = this.register(new Setting("Test", "https://discord.gg/mJgBfqYRvR"));
    private ConcurrentHashMap targetedPlayers = null;

    public AutoGG() {
        super("AutoGG", "Sends msg after you kill someone", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static AutoGG getINSTANCE() {
        if (AutoGG.INSTANCE == null) {
            AutoGG.INSTANCE = new AutoGG();
        }

        return AutoGG.INSTANCE;
    }

    private void setInstance() {
        AutoGG.INSTANCE = this;
    }

    public void onEnable() {
        this.targetedPlayers = new ConcurrentHashMap();
    }

    public void onDisable() {
        this.targetedPlayers = null;
    }

    public void onUpdate() {
        if (!nullCheck()) {
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }

            Iterator iterator = AutoGG.mc.world.getLoadedEntityList().iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();
                String name2;
                EntityPlayer player;

                if (entity instanceof EntityPlayer && (player = (EntityPlayer) entity).getHealth() <= 0.0F && this.shouldAnnounce(name2 = player.getName())) {
                    this.doAnnounce(name2);
                    break;
                }
            }

            this.targetedPlayers.forEach((name, timeout) -> {
                if (timeout.intValue() <= 0) {
                    this.targetedPlayers.remove(name);
                } else {
                    this.targetedPlayers.put(name, Integer.valueOf(timeout.intValue() - 1));
                }

            });
        }
    }

    @SubscribeEvent
    public void onLeavingDeathEvent(LivingDeathEvent event) {
        if (AutoGG.mc.player != null) {
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }

            EntityLivingBase entity;

            if ((entity = event.getEntityLiving()) != null) {
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;

                    if (player.getHealth() <= 0.0F) {
                        String name = player.getName();

                        if (this.shouldAnnounce(name)) {
                            this.doAnnounce(name);
                        }

                    }
                }
            }
        }
    }

    private boolean shouldAnnounce(String name) {
        return this.targetedPlayers.containsKey(name);
    }

    private void doAnnounce(String name) {
        this.targetedPlayers.remove(name);
        AutoGG.mc.player.connection.sendPacket(new CPacketChatMessage((String) this.custom.getValue()));
        int u = 0;

        for (int i = 0; i < 10; ++i) {
            ++u;
        }

        if (!((String) this.test.getValue()).equalsIgnoreCase("null")) {
            AutoGG.mc.player.connection.sendPacket(new CPacketChatMessage((String) this.test.getValue()));
        }

    }

    public void addTargetedPlayer(String name) {
        if (!Objects.equals(name, AutoGG.mc.player.getName())) {
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }

            this.targetedPlayers.put(name, Integer.valueOf(20));
        }
    }
}
