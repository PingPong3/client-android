package red.itvirtuoso.pingpong3.app.server;

import java.util.ArrayList;

/**
 * Created by kenji on 15/05/03.
 */
public class LocalServerProxy {
    private long stepTime;
    private ArrayList<Packet> packets;

    public LocalServerProxy(long stepTime) {
        this.stepTime = stepTime;
        this.packets = new ArrayList<>();
    }

    private void addPacket(PacketType type, long currentTime, long step) {
        synchronized (this.packets) {
            this.packets.add(new Packet(type, currentTime + step * stepTime));
        }
    }

    public void send(Packet packet) {
        switch (packet.getType()) {
            case SWING:
                sendSwing();
                break;
            default: /* nop */
        }
    }

    private void sendSwing() {
        long currentTime = System.currentTimeMillis();
        addPacket(PacketType.SERVE, currentTime, 0);
        addPacket(PacketType.BOUND_MY_AREA, currentTime, 1);
        addPacket(PacketType.BOUND_RIVAL_AREA, currentTime, 2);
        addPacket(PacketType.RETURN, currentTime, 3);
        addPacket(PacketType.BOUND_MY_AREA, currentTime, 5);
        addPacket(PacketType.POINT_RIVAL, currentTime, 7);
    }

    public Packet receive() {
        synchronized (this.packets) {
            if (this.packets.size() == 0) {
                return null;
            }
            Packet packet = this.packets.get(0);
            if (System.currentTimeMillis() < packet.getTime()) {
                return null;
            }
            this.packets.remove(0);
            return packet;
        }
    }
}
