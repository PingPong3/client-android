package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/05/03.
 */
public abstract class PacketAction extends Action {
    private PacketType type;

    public PacketAction(long time, PacketType type) {
        super(time);
        this.type = type;
    }
}
