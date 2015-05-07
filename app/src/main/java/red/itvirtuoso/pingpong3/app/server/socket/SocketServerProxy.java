package red.itvirtuoso.pingpong3.app.server.socket;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import red.itvirtuoso.pingpong3.app.server.Packet;
import red.itvirtuoso.pingpong3.app.server.ServerProxy;

/**
 * Created by kenji on 15/05/07.
 */
public class SocketServerProxy extends ServerProxy {
    private static final String TAG = SocketServerProxy.class.getName();

    private InetAddress address;
    private int port;
    private Socket socket;

    public SocketServerProxy(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void connect() throws IOException {
        socket = new Socket(address, port);
    }

    @Override
    public void send(Packet packet) throws IOException {
        socket.getOutputStream().write(packet.getType().getId());
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
