package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.Timer;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Criticals extends Module {

    private final Setting packets = this.register(new Setting("Packets", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(4), "Amount of packets you want to send."));
    private final Timer timer = new Timer();
    private final boolean resetTimer = false;

    public Criticals() {
        super("Criticals", "Scores criticals for you", Module.Category.COMBAT, true, false, false);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        CPacketUseEntity packet;

        if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity) event.getPacket()).getAction() == Action.ATTACK) {
            if (!this.timer.passedMs(0L)) {
                return;
            }

            if (Criticals.mc.player.onGround && !Criticals.mc.gameSettings.keyBindJump.isKeyDown() && packet.getEntityFromWorld(Criticals.mc.world) instanceof EntityLivingBase && !Criticals.mc.player.isInWater() && !Criticals.mc.player.isInLava()) {
                switch (((Integer) this.packets.getValue()).intValue()) {
                case 1:
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.10000000149011612D, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    break;

                case 2:
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.0625101D, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 1.1E-5D, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    break;

                case 3:
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.0625101D, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.0125D, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    break;

                case 4:
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.1625D, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 4.0E-6D, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 1.0E-6D, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket(new CPacketPlayer());
                    Criticals.mc.player.onCriticalHit((Entity) Objects.requireNonNull(packet.getEntityFromWorld(Criticals.mc.world)));
                }

                this.timer.reset();
            }
        }

    }

    public String getDisplayInfo() {
        return "Packet";
    }
}
