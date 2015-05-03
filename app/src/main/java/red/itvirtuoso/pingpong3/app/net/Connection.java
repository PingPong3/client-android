package red.itvirtuoso.pingpong3.app.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import red.itvirtuoso.pingpong3.app.server.Packet;
import red.itvirtuoso.pingpong3.app.server.PacketType;
import red.itvirtuoso.pingpong3.app.server.ServerProxy;

/**
 * Created by kenji on 15/04/12.
 */
public class Connection {
    private ServerProxy serverProxy;
    private ConnectionListener listener;
    private boolean isConnected = false;
    private ExecutorService service;

    public Connection(ServerProxy serverProxy) {
        this.serverProxy = serverProxy;
        this.service = Executors.newSingleThreadExecutor();
    }

    public void setListener(ConnectionListener listener) {
        this.listener = listener;
    }

    public final void connect() {
        this.isConnected = this.serverProxy.connect();
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
        Packet packet = new Packet(PacketType.SWING);
        serverProxy.send(packet);
    }
}
