package red.itvirtuoso.pingpong3.app.server;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by kenji on 15/04/12.
 */
public class LocalConnectionTest {
    private class TestListener implements ConnectionListener {
        private boolean mOnConnectSuccessCalled = false;
        private boolean mOnReadyCalled = false;

        @Override
        public void onConnectSuccess() {
            mOnConnectSuccessCalled = true;
        }

        @Override
        public void onReady() {
            mOnReadyCalled = true;
        }
    }

    @Test
    public void ローカルのゲームサーバに接続する() throws Exception {
        /*
         * ローカルのゲームサーバに接続すると、次の順に瞬時にイベントが発生する
         * <ul>
         *     <li>接続に成功する</li>
         *     <li>相手の準備ができる</li>
         * </ul>
         */
        TestListener listener = new TestListener();
        Connection connection = new LocalConnection();
        connection.connect(listener);
        assertTrue("LocalServerに接続されなかった", listener.mOnConnectSuccessCalled);
        assertTrue("対戦相手の準備ができなかった", listener.mOnReadyCalled);
    }
}
