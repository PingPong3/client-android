package red.itvirtuoso.pingpong3.app.server;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by kenji on 15/04/12.
 */
public class LocalConnectionTest {
    private enum EventType {
        ConnectSuccess, Ready, BoundMyArea, BoundRivalArea, Return,
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

        assertThat(listener.events, hasItems(
                        new Event(0, EventType.ConnectSuccess),
                        new Event(0, EventType.Ready))
        );
    }

    @Test
    public void ローカルサーバでサーブを行う() throws Exception {
        /*
         * ローカルサーバに対してサーブを行うと、次のイベントが順に発生する
         * <ul>
         *     <li>サーブ</li>
         *     <li>自陣でのバウンド</li>
         *     <li>相手陣でのバウンド</li>
         *     <li>相手のリターン</li>
         *     <li>自陣でのバウンド</li>
         * </ul>
         */
        TestListener listener = new TestListener();
        Connection connection = new LocalConnection();
        connection.connect(listener);
        connection.serve();
    }
}
