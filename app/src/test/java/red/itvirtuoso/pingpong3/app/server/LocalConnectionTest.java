package red.itvirtuoso.pingpong3.app.server;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Created by kenji on 15/04/12.
 */
public class LocalConnectionTest {
    private enum EventType {
        ConnectSuccess, Ready, BoundMyArea, BoundRivalArea, Return,
    }

    private class Event {
        private long time;
        private EventType eventType;

        private Event(EventType eventType) {
            this.time = System.currentTimeMillis();
            this.eventType = eventType;
        }

        public long getTime() {
            return this.time;
        }

        public EventType getEventType() {
            return this.eventType;
        }
    }

    private class TestListener implements ConnectionListener {
        private ArrayList<Event> events = new ArrayList<>();

        private void addEvent(EventType eventType) {
            this.events.add(new Event(eventType));
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
        Event event = null;
        List<Event> events = listener.events;
        Iterator<Event> iterator = events.iterator();

        assertEquals("発生したイベントの数が異なる", 2, events.size());
        event = iterator.next();
        assertSame("LocalServerに接続されなかった", EventType.ConnectSuccess, event.getEventType());
        event = iterator.next();
        assertSame("対戦相手の準備ができなかった", EventType.Ready, event.getEventType());
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
