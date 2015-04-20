package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/04/12.
 */
public class LocalConnection extends Connection {
    @Override
    public void connect(ConnectionListener listener) {
        listener.onConnectSuccess();
        listener.onReady();
    }
}
