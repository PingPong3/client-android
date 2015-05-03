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
    private class TestServerProxy implements ServerProxy {
        private List<Packet> sendPackets = new ArrayList<>();

        @Override
        public boolean connect() {
            return true;
        }

        @Override
        public void send(Packet packet) {
            this.sendPackets.add(packet);
        }

        @Override
        public Packet receive() {
            return null;
        }
    }

    private class TestListener implements ConnectionListener {
        @Override
        public void onConnectSuccess() {
            /* nop */
        }

        @Override
        public void onReady() {
            /* nop */
        }

        @Override
        public void onServe(Event event) {
            /* nop */
        }

        @Override
        public void onBoundMyArea(Event event) {
            /* nop */
        }

        @Override
        public void onBoundRivalArea(Event event) {
            /* nop */
        }

        @Override
        public void onReturn(Event event) {
            /* nop */
        }

        @Override
        public void onPointRival() {
            /* nop */
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

    @Test
    public void ラケットを振る() throws Exception {
        TestServerProxy serverProxy = new TestServerProxy();
        Connection connection = new Connection(serverProxy);
        connection.connect();
        connection.swing();

        assertThat(serverProxy.sendPackets, is(contains(
            new Packet(PacketType.SWING)
        )));
    }
}
