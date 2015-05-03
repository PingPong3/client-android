package red.itvirtuoso.pingpong3.app.net;

import org.junit.Test;

import red.itvirtuoso.pingpong3.app.server.Packet;
import red.itvirtuoso.pingpong3.app.server.ServerProxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by kenji on 15/04/21.
 */
public class ConnectionTest {
    private class TestServerProxy implements ServerProxy {
        @Override
        public boolean connect() {
            return true;
        }

        @Override
        public void send(Packet packet) {
            /* nop */
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

    @Test(timeout = 5000)
    public void 接続と切断を行う() throws Exception {
        TestServerProxy server = new TestServerProxy();
        Connection connection = new Connection(server);
        assertThat(connection.isConnected(), is(false));
        connection.connect();
        assertThat(connection.isConnected(), is(true));
        connection.disconnect();
        assertThat(connection.isConnected(), is(false));
    }
}
