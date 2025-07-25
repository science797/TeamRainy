package developmentteam.teamrainy.api.events.impl;

import developmentteam.teamrainy.api.events.Event;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends Event {

    private final Packet<?> packet;
    public PacketEvent(Packet<?> packet, Stage stage) {
        super(stage);
        this.packet = packet;
    }
    public <T extends Packet<?>> T getPacket() {
        return (T) packet;
    }
    public static class Send extends PacketEvent {
        public Send(Packet<?> packet) {
            super(packet, Stage.Pre);
        }
    }
    public static class SendPost extends PacketEvent {
        public SendPost(Packet<?> packet) {
            super(packet, Stage.Post);
        }
    }

    public static class Receive extends PacketEvent {
        public Receive(Packet<?> packet) {
            super(packet, Stage.Pre);
        }
    }
}
