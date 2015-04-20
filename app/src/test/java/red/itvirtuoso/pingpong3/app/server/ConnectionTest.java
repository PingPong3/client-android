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

    private class TestListener implements ConnectionListener {
        private boolean mIsCallOnConnectSuccess = false;

        @Override
        public void onConnectSuccess() {
            mIsCallOnConnectSuccess = true;
        }

        @Override
        public void onReady() {
            /* nop */
        }
    }

    @Test(timeout = 5)
    public void 接続するとisConnectedプロパティがtrueになる() throws Exception {
        Connection connection = new TestConnection();
        assertFalse("接続していないのにステータスがconnectedになっている", connection.isConnected());
        TestListener listener = new TestListener();
        connection.connect(listener);
        while (!listener.mIsCallOnConnectSuccess) {
            Thread.yield();
        }
        assertTrue("接続しているのにステータスがconnectedになっていない", connection.isConnected());
    }

    @Test(timeout = 5)
    public void 切断するとisConnectedプロパティがfalseになる() throws Exception {
        Connection connection = new TestConnection();
        TestListener listener = new TestListener();
        connection.connect(listener);
        while (!listener.mIsCallOnConnectSuccess) {
            Thread.yield();
        }
        connection.disconnect();
        assertFalse("切断したのにステータスがconnectedになっている", connection.isConnected());
    }
}
