package red.itvirtuoso.pingpong3.app.server;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by kenji on 15/04/21.
 */
public class ConnectionTest {
    private class TestConnection extends Connection {

        @Override
        protected boolean onConnect() {
            getListener().onConnectSuccess();
            return true;
        }
    }

    private class AbstractConnectionListener implements ConnectionListener {

        @Override
        public void onConnectSuccess() {
            /* nop */
        }

        @Override
        public void onReady() {
            /* nop */
        }
    }

    @Test(timeout = 1)
    public void 接続するとisConnectedプロパティがtrueになる() throws Exception {
        Connection connection = new TestConnection();
        assertFalse("接続していないのにステータスがconnectedになっている", connection.isConnected());
        class TestListener extends AbstractConnectionListener {
            private boolean mIsCallOnConnectSuccess = false;

            @Override
            public void onConnectSuccess() {
                mIsCallOnConnectSuccess = true;
            }
        };
        TestListener listener = new TestListener();
        connection.connect(listener);
        while (!listener.mIsCallOnConnectSuccess) {
            Thread.yield();
        }
        assertTrue("接続しているのにステータスがconnectedになっていない", connection.isConnected());
    }
}
