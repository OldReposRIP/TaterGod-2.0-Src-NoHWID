package club.tater.tatergod.event.events;

public class EventRecievePacket {

    private Packet packet;

    public EventRecievePacket(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public void setCancelled(boolean b) {}
}
