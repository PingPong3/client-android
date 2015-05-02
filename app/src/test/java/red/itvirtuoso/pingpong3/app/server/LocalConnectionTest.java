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

    private enum EventType {
        ConnectSuccess, Ready, Serve, BoundMyArea, BoundRivalArea, Return, PointRival,
    }

    private class EventBuilder {
        private long beginTime;

        private EventBuilder() {
            this.beginTime = System.currentTimeMillis();
        }

        private Event create(EventType eventType) {
            return new Event(System.currentTimeMillis() - beginTime, eventType);
        }

        private Event create(int step, EventType eventType) {
            return new Event(step * STEP_TIME, eventType);
        }
    }

    private class Event {
        private long step;
        private EventType eventType;

        private Event(long time, EventType eventType) {
            this.step = time / STEP_TIME;
            this.eventType = eventType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Event event = (Event) o;

            if (step != event.step) return false;
            if (eventType != event.eventType) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (step ^ (step >>> 32));
            result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return step + ", " + eventType;
        }
    }

    private class TestListener implements ConnectionListener {
        private ArrayList<Event> events = new ArrayList<>();
        private EventBuilder builder = new EventBuilder();

        private void addEvent(EventType eventType) {
            this.events.add(builder.create(eventType));
        }

        @Override
        public void onConnectSuccess() {
            addEvent(EventType.ConnectSuccess);
        }

        @Override
        public void onReady() {
            addEvent(EventType.Ready);
        }

        @Override
        public void onServe() {
            addEvent(EventType.Serve);
        }

        @Override
        public void onBoundMyArea() {
            addEvent(EventType.BoundMyArea);
        }

        @Override
        public void onBoundRivalArea() {
            addEvent(EventType.BoundRivalArea);
        }

        @Override
        public void onReturn() {
            addEvent(EventType.Return);
        }

        @Override
        public void onPointRival() {
            addEvent(EventType.PointRival);
        }
    }

    @Test
    public void ローカルのゲームサーバに接続する() throws Exception {
        /*
         * ローカルのゲームサーバに接続すると、次の順に瞬時にイベントが発生する
         * <ul>
         *     <li>0, 接続に成功する</li>
         *     <li>0, 相手の準備ができる</li>
         * </ul>
         */
        Connection connection = new LocalConnection(STEP_TIME);
        TestListener listener = new TestListener();
        connection.setListener(listener);
        connection.connect();

        EventBuilder builder = new EventBuilder();
        assertThat(listener.events, is(contains(
                builder.create(0, EventType.ConnectSuccess),
                builder.create(0, EventType.Ready)
        )));
    }

    @Test(timeout = 5000)
    public void ローカルサーバでサーブを行う() throws Exception {
        /*
         * ローカルサーバに対してサーブを行うと、次のイベントが順に発生する
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

        List<Event> events = listener.events.subList(2, listener.events.size());
        EventBuilder builder = new EventBuilder();
        assertThat(events, is(contains(
                builder.create(0, EventType.Serve),
                builder.create(1, EventType.BoundMyArea),
                builder.create(2, EventType.BoundRivalArea),
                builder.create(3, EventType.Return),
                builder.create(5, EventType.BoundMyArea),
                builder.create(7, EventType.PointRival)
        )));
    }
}
