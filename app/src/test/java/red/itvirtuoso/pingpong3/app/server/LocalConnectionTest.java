package red.itvirtuoso.pingpong3.app.server;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by kenji on 15/04/12.
 */
public class LocalConnectionTest {
    private static final long UNIT_TIME = 10;

    private enum EventType {
        ConnectSuccess, Ready, Serve, BoundMyArea, BoundRivalArea, Return, PointRival,
    }

    private class EventBuilder {
        private long time;

        private EventBuilder() {
            this.time = System.currentTimeMillis();
        }

        public Event create(EventType eventType) {
            return new Event(System.currentTimeMillis() - time, eventType);
        }
    }

    private class Event {
        private long delta;
        private EventType eventType;

        private Event(long time, EventType eventType) {
            /* 10ミリ秒以内は誤差として同一視する */
            this.delta = time / 10;
            this.eventType = eventType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Event event = (Event) o;

            if (delta != event.delta) return false;
            if (eventType != event.eventType) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (delta ^ (delta >>> 32));
            result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
            return result;
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
        Connection connection = new LocalConnection();
        TestListener listener = new TestListener();
        connection.connect(listener);

        assertThat(listener.events, hasItems(
                new Event(0, EventType.ConnectSuccess),
                new Event(0, EventType.Ready)
        ));
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
        final Connection connection = new LocalConnection();
        TestListener listener = new TestListener() {
            @Override
            public void onPointRival() {
                super.onPointRival();
                connection.disconnect();
            }
        };
        connection.connect(listener);
        connection.serve();
        while (connection.isConnected()) {
            Thread.yield();
        }

        List<Event> events = listener.events.subList(2, listener.events.size());
        assertThat(events, hasItems(
                new Event(0 * UNIT_TIME, EventType.Serve),
                new Event(1 * UNIT_TIME, EventType.BoundMyArea),
                new Event(2 * UNIT_TIME, EventType.BoundRivalArea),
                new Event(3 * UNIT_TIME, EventType.Return),
                new Event(5 * UNIT_TIME, EventType.BoundMyArea),
                new Event(7 * UNIT_TIME, EventType.PointRival)
        ));
    }
}
