package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/05/03.
 */
public interface ServerProxy {
    boolean connect();

    void disconnect();

    void send(Packet packet);
    Packet receive();
}
