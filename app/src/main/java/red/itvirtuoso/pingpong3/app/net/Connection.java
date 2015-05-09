package red.itvirtuoso.pingpong3.app.net;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import red.itvirtuoso.pingpong3.app.server.Packet;
import red.itvirtuoso.pingpong3.app.server.PacketType;
import red.itvirtuoso.pingpong3.app.server.ServerProxy;

/**
 * Created by kenji on 15/04/12.
 */
public class Connection implements Runnable {
    private static final String TAG = Connection.class.getName();

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
        Log.i(TAG, "Start connection loop");
        while (isConnected) {
            Thread.yield();
            Packet packet = serverProxy.receive();
            if (packet == null) {
                continue;
            }
            Log.i(TAG, "RCV " + packet);
            EventType eventType = EventType.create(packet.getType());
            if (eventType != null && listener != null) {
                listener.onEvent(new Event(eventType));
            }
        }
        Log.i(TAG, "End connection loop");
    }

    public void setListener(ConnectionListener listener) {
        this.listener = listener;
    }

    public final void connect() throws IOException {
        serverProxy.connect();
        service.execute(this);
        service.shutdown();
        isConnected = true;
    }

    public final void disconnect() {
        isConnected = false;
        serverProxy.disconnect();
    }

    public final boolean isConnected() {
        return isConnected;
    }

    public void swing() throws IOException{
        Packet packet = new Packet(PacketType.SWING);
        Log.i(TAG, "SND " + packet);
        serverProxy.send(packet);
    }
}
