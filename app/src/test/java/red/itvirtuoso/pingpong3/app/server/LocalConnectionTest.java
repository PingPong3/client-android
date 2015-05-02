package red.itvirtuoso.pingpong3.app.server;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * Created by kenji on 15/04/12.
 */
public class LocalConnectionTest {
    /* ゲームループの単位時間。テスト用に短くしている */
    private static final long STEP_TIME = 50;

    private enum TestEventType {
        ConnectSuccess, Ready, Serve, BoundMyArea, BoundRivalArea, Return, PointRival,
    }

    private class TestEventBuilder {
        private long beginTime;

        private TestEventBuilder() {
            this.beginTime = System.currentTimeMillis();
        }

        private TestEvent create(TestEventType testEventType) {
            return new TestEvent(System.currentTimeMillis() - beginTime, testEventType);
        }

        private TestEvent create(int step, TestEventType testEventType) {
            return new TestEvent(step * STEP_TIME, testEventType);
        }
    }

    private class TestEvent {
        private long step;
        private TestEventType testEventType;

        private TestEvent(long time, TestEventType testEventType) {
            this.step = time / STEP_TIME;
            this.testEventType = testEventType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestEvent testEvent = (TestEvent) o;

            if (step != testEvent.step) return false;
            if (testEventType != testEvent.testEventType) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (step ^ (step >>> 32));
            result = 31 * result + (testEventType != null ? testEventType.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return step + ", " + testEventType;
        }
    }

    private class TestListener implements ConnectionListener {
        private ArrayList<TestEvent> testEvents = new ArrayList<>();
        private TestEventBuilder builder = new TestEventBuilder();

        private void addEvent(TestEventType testEventType) {
            this.testEvents.add(builder.create(testEventType));
        }

        @Override
        public void onConnectSuccess() {
            addEvent(TestEventType.ConnectSuccess);
        }

        @Override
        public void onReady() {
            addEvent(TestEventType.Ready);
        }

        @Override
        public void onServe(Event event) {
            addEvent(TestEventType.Serve);
        }

        @Override
        public void onBoundMyArea(Event event) {
            addEvent(TestEventType.BoundMyArea);
        }

        @Override
        public void onBoundRivalArea(Event event) {
            addEvent(TestEventType.BoundRivalArea);
        }

        @Override
        public void onReturn(Event event) {
            addEvent(TestEventType.Return);
        }

        @Override
        public void onPointRival() {
            addEvent(TestEventType.PointRival);
        }
    }

    @Test
    public void ゲームサーバに接続する() throws Exception {
        /*
         * 次のイベントが順に発生する
         * <ul>
         *     <li>0, 接続に成功する</li>
         *     <li>0, 相手の準備ができる</li>
         * </ul>
         */
        Connection connection = new LocalConnection(STEP_TIME);
        TestListener listener = new TestListener();
        connection.setListener(listener);
        connection.connect();

        TestEventBuilder builder = new TestEventBuilder();
        assertThat(listener.testEvents, is(contains(
                builder.create(0, TestEventType.ConnectSuccess),
                builder.create(0, TestEventType.Ready)
        )));
    }

    @Test(timeout = 5000)
    public void サーブを行う() throws Exception {
        /*
         * 接続のイベント後に、次のイベントが順に発生する
         * <ul>
         *     <li>0, サーブ</li>
         *     <li>1, 自陣でのバウンド</li>
         *     <li>2, 相手陣でのバウンド</li>
         *     <li>3, 相手のリターン</li>
         *     <li>5, 自陣でのバウンド</li>
         *     <li>7, 相手の得点</li>
         * </ul>
         */
        final Connection connection = new LocalConnection(STEP_TIME);
        TestListener listener = new TestListener() {
            @Override
            public void onPointRival() {
                super.onPointRival();
                connection.disconnect();
            }
        };
        connection.setListener(listener);
        connection.connect();
        connection.swing();
        while (connection.isConnected()) {
            Thread.yield();
        }

        List<TestEvent> testEvents = listener.testEvents.subList(2, listener.testEvents.size());
        TestEventBuilder builder = new TestEventBuilder();
        assertThat(testEvents, is(contains(
                builder.create(0, TestEventType.Serve),
                builder.create(1, TestEventType.BoundMyArea),
                builder.create(2, TestEventType.BoundRivalArea),
                builder.create(3, TestEventType.Return),
                builder.create(5, TestEventType.BoundMyArea),
                builder.create(7, TestEventType.PointRival)
        )));
    }

    @Test(timeout = 5000)
    public void サーブしてから１回リターンを行う() throws Exception {
        /*
         * 相手のリターンが自陣でバウンドした後、次のイベントが順に発生する
         * <ul>
         *     <li>6, リターン</li>
         *     <li>8, 相手陣でのバウンド</li>
         *     <li>9, 相手のリターン</li>
         *     <li>11, 自陣でのバウンド</li>
         *     <li>13, 相手の得点</li>
         * </ul>
         */
        final Connection connection = new LocalConnection(STEP_TIME);
        TestListener listener = new TestListener() {
            private int count = 0;

            @Override
            public void onBoundMyArea(Event event) {
                if (event.getTurn() != Turn.ME || count != 1) {
                    return;
                }
                try {
                    Thread.sleep(1 * STEP_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                connection.swing();
            }

            @Override
            public void onPointRival() {
                super.onPointRival();
                connection.disconnect();
            }
        };
        connection.setListener(listener);
        connection.connect();
        connection.swing();
        while (connection.isConnected()) {
            Thread.yield();
        }

        List<TestEvent> testEvents = listener.testEvents.subList(7, listener.testEvents.size());
        TestEventBuilder builder = new TestEventBuilder();
        assertThat(testEvents, is(contains(
                builder.create(6, TestEventType.Return),
                builder.create(8, TestEventType.BoundRivalArea),
                builder.create(9, TestEventType.Return),
                builder.create(11, TestEventType.BoundMyArea),
                builder.create(13, TestEventType.PointRival)
        )));
    }
}
