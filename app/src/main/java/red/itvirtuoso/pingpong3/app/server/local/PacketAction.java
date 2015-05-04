package red.itvirtuoso.pingpong3.app.server.local;

import red.itvirtuoso.pingpong3.app.server.PacketType;

/**
 * Created by kenji on 15/05/03.
 */
abstract class PacketAction extends Action {
    private PacketType type;

    public PacketAction(long time, PacketType type) {
        super(time);
        this.type = type;
    }
}
