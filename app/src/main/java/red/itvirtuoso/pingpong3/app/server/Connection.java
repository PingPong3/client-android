package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/04/12.
 */
public abstract class Connection {
    public void connect(ConnectionListener listener) {

    }

    public boolean isConnected() {
        return false;
    }
}
