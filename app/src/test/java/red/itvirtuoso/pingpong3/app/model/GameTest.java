package red.itvirtuoso.pingpong3.app.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by kenji on 15/03/13.
 */
public class GameTest {
    private static final int BALL_SPEED = 50;

    private class GameEx extends Game {
        private GameEx() {
            super();
        }

        private GameEx(int ballSpeed) {
            super(ballSpeed);
        }
    }

    private class GameEventLog {
        private GameEvent mEvent;
        private long mTime;

        private GameEventLog(GameEvent event, long time) {
            mEvent = event;
            mTime = time;
        }
    }

    private class GameActionEx implements GameAction {
        private List<GameEventLog> mLogs = new ArrayList<>();

        @Override
        public void onGameAction(GameEvent event) {
            System.out.println(event);
            mLogs.add(new GameEventLog(event, System.currentTimeMillis()));
        }
    }

    @Test
    public void 自分がサーブを打って相手がリターンに失敗() throws Exception {
        /*
         * 次の順序でイベントが発生する
         *   0ms ... サーブ
         *  50ms ... １回目のバウンド
         * 100ms ... ２回目のバウンド
         */
        GameActionEx listener = new GameActionEx();
        Game game = new GameEx(BALL_SPEED);
        long tolerance = game.getTolerance();
        game.addListener(listener);
        game.swing(PlayerType.SELF);
        long now = System.currentTimeMillis();
        Thread.sleep(BALL_SPEED * 3);

        /* イベントの内容とタイミングを確認する */
        assertEquals("発生するイベント数が異なる", 3, listener.mLogs.size());
        GameEventLog log0 = listener.mLogs.get(0);
        assertEquals("サーブイベントが発生していない", GameEvent.SERVE, log0.mEvent);
        assertTrue("サーブイベントの時間がずれている", Math.abs(log0.mTime - (now + 0)) < tolerance);
        GameEventLog log1 = listener.mLogs.get(1);
        assertEquals("１回目のバウンドのイベントが発生していない", GameEvent.FIRST_BOUND, log1.mEvent);
        assertTrue("１回目のバウンドのイベントの時間がずれている", Math.abs(log1.mTime - (now + BALL_SPEED)) < tolerance);
        GameEventLog log2 = listener.mLogs.get(2);
        assertEquals("２回目のバウンドのイベントが発生していない", GameEvent.SECOND_BOUND, log2.mEvent);
        assertTrue("２回目のバウンドのイベントの時間がずれている", Math.abs(log2.mTime - (now + BALL_SPEED * 2)) < tolerance);

        game.shutdown();
    }

    @Test
    public void リスナーを取り除く() throws Exception {
        GameActionEx listener = new GameActionEx();
        Game game = new GameEx();
        game.addListener(listener);
        game.removeListener(listener);
        game.swing(PlayerType.SELF);
        Thread.sleep(100);
        assertEquals("リスナーを取り除いたのにイベントが発生した", 0, listener.mLogs.size());

        game.shutdown();
    }
}
