package red.itvirtuoso.pingpong3.app.net;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
    public void 接続するとisConnectedプロパティがtrueになる() throws Exception {
        Connection connection = new TestConnection();
        assertThat("接続していないのにステータスがconnectedになっている", connection.isConnected(), is(false));
        TestListener listener = new TestListener();
        connection.setListener(listener);
        connection.connect();
        assertThat("接続しているのにステータスがconnectedになっていない", connection.isConnected(), is(true));
    }

    @Test(timeout = 5000)
    public void 切断するとisConnectedプロパティがfalseになる() throws Exception {
        Connection connection = new TestConnection();
        TestListener listener = new TestListener();
        connection.setListener(listener);
        connection.connect();
        connection.disconnect();
        assertThat("切断したのにステータスがconnectedになっている", connection.isConnected(), is(false));
    }
}
