package red.itvirtuoso.pingpong3.app.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by kenji on 15/03/13.
 */
public class GameTest {
    private class GameEx extends Game {
    }

    private class GameEventListenerEx implements GameEventListener {
        private List<GameEvent> mEvents = new ArrayList<>();

        @Override
        public void onEvent(GameEvent event) {
            mEvents.add(event);
        }
    }

    @Test
    public void サーブを打ってリターンに失敗() throws Exception {
        GameEventListenerEx listener = new GameEventListenerEx();
        Game game = new GameEx();
        game.addListener(listener);
        game.swing(PlayerType.SELF);
        Thread.sleep(100);
        assertEquals("発生するイベンント数が異なる", 3, listener.mEvents.size());
        assertEquals("サーブイベントが発生していない", GameEvent.SERVE, listener.mEvents.get(0));
        assertEquals("ファーストバウンドイベントが発生していない", GameEvent.FIRST_BOUND, listener.mEvents.get(1));
        assertEquals("セカンドバウンドイベントが発生していない", GameEvent.SECOND_BOUND, listener.mEvents.get(2));
    }

    @Test
    public void リスナーを取り除く() throws Exception {
        GameEventListenerEx listener = new GameEventListenerEx();
        Game game = new GameEx();
        game.addListener(listener);
        game.removeListener(listener);
        game.swing(PlayerType.SELF);
        Thread.sleep(100);
        assertEquals("リスナーを取り除いたのにイベントが発生した", 0, listener.mEvents.size());
    }
}
