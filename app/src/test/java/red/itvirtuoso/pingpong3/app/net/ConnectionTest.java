package red.itvirtuoso.pingpong3.app.net;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import red.itvirtuoso.pingpong3.app.server.Packet;
import red.itvirtuoso.pingpong3.app.server.PacketType;
import red.itvirtuoso.pingpong3.app.server.ServerProxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * Created by kenji on 15/04/21.
 */
public class ConnectionTest {
    private class TestServerProxy extends ServerProxy {
        private List<Packet> sendPackets = new ArrayList<>();
        private List<Packet> receivePackets = new ArrayList<>();

        @Override
        public boolean connect() {
            return true;
        }

        @Override
        public void disconnect() {
            /* nop */
        }

        @Override
        public void send(Packet packet) {
            this.sendPackets.add(packet);
        }

        @Override
        public Packet receive() {
            return this.receivePackets.size() == 0
                    ? null
                    : this.receivePackets.remove(0);
        }
    }

    private class TestConnectionListener implements ConnectionListener {
        private List<Event> events = new ArrayList<>();

        @Override
        public void onConnectSuccess() {
            /* nop */
        }

        @Override
        public void onEvent(Event event) {
            this.events.add(event);
        }
    }

    @Test(timeout = 1000)
    public void 接続と切断を行う() throws Exception {
        TestServerProxy serverProxy = new TestServerProxy();
        Connection connection = new Connection(serverProxy);
        assertThat(connection.isConnected(), is(false));
        connection.connect();
        assertThat(connection.isConnected(), is(true));
        connection.disconnect();
        assertThat(connection.isConnected(), is(false));
    }

    @Test(timeout = 1000)
    public void ラケットを振る() throws Exception {
        TestServerProxy serverProxy = new TestServerProxy();
        Connection connection = new Connection(serverProxy);
        connection.connect();
        connection.swing();
        connection.disconnect();

        /* 結果確認 */
        assertThat(serverProxy.sendPackets, is(contains(
                new Packet(PacketType.SWING)
        )));
    }

    @Test(timeout = 1000)
    public void サーバのパケットがリスナーに伝えらえる() throws Exception {
        TestServerProxy serverProxy = new TestServerProxy();
        Connection connection = new Connection(serverProxy);
        TestConnectionListener listener = new TestConnectionListener();
        connection.setListener(listener);
        connection.connect();
        serverProxy.receivePackets.add(new Packet(PacketType.ME_READY));
        while (serverProxy.receivePackets.size() > 0) {
            Thread.yield();
        }
        connection.disconnect();

        /* 結果確認 */
        assertThat(listener.events, is(contains(
                new Event(EventType.ME_READY)
        )));
    }
}
