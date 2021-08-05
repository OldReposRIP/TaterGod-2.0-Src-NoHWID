package club.tater.tatergod.manager;

import club.tater.tatergod.features.Feature;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.Packet;

public class PacketManager extends Feature {

    private final List noEventPackets = new ArrayList();

    public void sendPacketNoEvent(Packet packet) {
        if (packet != null && !nullCheck()) {
            this.noEventPackets.add(packet);
            PacketManager.mc.player.connection.sendPacket(packet);
        }

    }

    public boolean shouldSendPacket(Packet packet) {
        if (this.noEventPackets.contains(packet)) {
            this.noEventPackets.remove(packet);
            return false;
        } else {
            return true;
        }
    }
}
