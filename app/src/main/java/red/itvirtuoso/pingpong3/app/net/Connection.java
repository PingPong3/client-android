package red.itvirtuoso.pingpong3.app.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import red.itvirtuoso.pingpong3.app.server.Packet;
import red.itvirtuoso.pingpong3.app.server.PacketType;
import red.itvirtuoso.pingpong3.app.server.ServerProxy;

/**
 * Created by kenji on 15/04/12.
 */
public class Connection implements Runnable {
    private ServerProxy serverProxy;
    private ConnectionListener listener;
    private boolean isConnected = false;
    private ExecutorService service;

    public Connection(ServerProxy serverProxy) {
        this.serverProxy = serverProxy;
        this.service = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run() {
        while (isConnected) {
            Thread.yield();
            Packet packet = serverProxy.receive();
            if (packet == null) {
                continue;
            }
            EventType eventType = EventType.create(packet.getType());
            if (eventType != null && listener != null) {
                listener.onEvent(new Event(eventType));
            }
        }
    }

    public void setListener(ConnectionListener listener) {
        this.listener = listener;
    }

    public final void connect() {
        isConnected = serverProxy.connect();
        if (isConnected) {
            service.execute(this);
            service.shutdown();
        }
    }

    public final void disconnect() {
        isConnected = false;
        serverProxy.disconnect();
    }

    public final boolean isConnected() {
        return isConnected;
    }

    public void swing() {
        Packet packet = new Packet(PacketType.SWING);
        serverProxy.send(packet);
    }
}
