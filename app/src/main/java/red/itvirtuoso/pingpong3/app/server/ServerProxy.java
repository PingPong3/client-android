package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/05/03.
 */
public abstract class ServerProxy {
    public abstract boolean connect();
    public abstract void send(Packet packet);
    public abstract Packet receive();
    public abstract void disconnect();
}
