package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/05/03.
 */
public class Packet {
    private final PacketType type;
    private final long time;

    public Packet(PacketType type) {
        this(type, 0);
    }

    public Packet(PacketType type, long time) {
        this.type = type;
        this.time = time;
    }

    public PacketType getType() {
        return type;
    }

    public long getTime() {
        return time;
    }
}
