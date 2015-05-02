package red.itvirtuoso.pingpong3.app.server;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by kenji on 15/04/21.
 */
public class ConnectionTest {
    private class AbstractConnection extends Connection {

        @Override
        protected boolean onConnect() {
            /* nop */
            return false;
        }

        @Override
        public void swing() {
            /* nop */
        }
    }

    private class TestConnection extends AbstractConnection {

        @Override
        protected boolean onConnect() {
            getListener().onConnectSuccess();
            return true;
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
        public void onServe() {
            /* nop */
        }

        @Override
        public void onBoundMyArea() {
            /* nop */
        }

        @Override
        public void onBoundRivalArea() {
            /* nop */
        }

        @Override
        public void onReturn() {
            /* nop */
        }

        @Override
        public void onPointRival() {
            /* nop */
        }
    }

    @Test(timeout = 5000)
    public void 接続するとisConnectedプロパティがtrueになる() throws Exception {
        Connection connection = new TestConnection();
        assertFalse("接続していないのにステータスがconnectedになっている", connection.isConnected());
        TestListener listener = new TestListener();
        connection.setListener(listener);
        connection.connect();
        assertTrue("接続しているのにステータスがconnectedになっていない", connection.isConnected());
    }

    @Test(timeout = 5000)
    public void 切断するとisConnectedプロパティがfalseになる() throws Exception {
        Connection connection = new TestConnection();
        TestListener listener = new TestListener();
        connection.setListener(listener);
        connection.connect();
        connection.disconnect();
        assertFalse("切断したのにステータスがconnectedになっている", connection.isConnected());
    }
}
