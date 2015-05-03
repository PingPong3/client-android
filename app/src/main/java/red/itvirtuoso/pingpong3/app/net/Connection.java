package red.itvirtuoso.pingpong3.app.net;

import red.itvirtuoso.pingpong3.app.server.ServerProxy;

/**
 * Created by kenji on 15/04/12.
 */
public class Connection {
    private ServerProxy serverProxy;
    private ConnectionListener listener;
    private boolean isConnected = false;

    public Connection(ServerProxy serverProxy) {
        this.serverProxy = serverProxy;
    }

    public void setListener(ConnectionListener listener) {
        this.listener = listener;
    }

    public final void connect() {
        isConnected = this.serverProxy.connect();
    }

    public final void disconnect() {
        isConnected = false;
    }

    public final boolean isConnected() {
        return isConnected;
    }

    protected final ConnectionListener getListener() {
        return listener;
    }

    public void swing() {
        /* TODO */
    }
}
