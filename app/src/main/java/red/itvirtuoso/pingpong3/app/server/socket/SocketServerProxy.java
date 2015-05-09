package red.itvirtuoso.pingpong3.app.server.socket;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import red.itvirtuoso.pingpong3.app.server.Packet;
import red.itvirtuoso.pingpong3.app.server.PacketType;
import red.itvirtuoso.pingpong3.app.server.ServerProxy;

/**
 * Created by kenji on 15/05/07.
 */
public class SocketServerProxy extends ServerProxy implements Runnable {
    private static final String TAG = SocketServerProxy.class.getName();

    private String host;
    private int port;
    private Socket socket;

    public SocketServerProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect() throws IOException {
        InetAddress address = InetAddress.getByName(host);
        socket = new Socket(address, port);
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(this);
        service.shutdown();
    }

    @Override
    public void send(Packet packet) throws IOException {
        socket.getOutputStream().write(packet.getType().getId());
    }

    @Override
    public void run() {
        while (true) {
            int data = 0;
            try {
                data = socket.getInputStream().read();
            } catch (IOException e) {
                disconnect();
                break;
            }
            if (data < 0) {
                disconnect();
                break;
            }
            Packet packet = new Packet(PacketType.valueOf(data));
            add(packet);
        }
    }

    @Override
    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            Log.w(TAG, "ソケットのクローズに失敗しました", e);
        }
    }
}
