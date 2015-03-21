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
    private static final int TEST_UNIT_TIME = 50;

    private class GameEx extends Game {
        private GameEx() {
            super(TEST_UNIT_TIME);
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
            mLogs.add(new GameEventLog(event, System.currentTimeMillis()));
        }
    }

    @Test
    public void リスナーを取り除く() throws Exception {
        GameActionEx listener = new GameActionEx();
        Game game = new GameEx();
        game.addListener(listener);
        game.start();
        game.removeListener(listener);
        game.swing(PlayerType.SELF);
        Thread.sleep(TEST_UNIT_TIME * 2);

        assertEquals("リスナーを取り除いたのにイベントが発生した", 0, listener.mLogs.size());

        game.shutdown();
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
        Game game = new GameEx();
        long tolerance = game.getTolerance();
        game.addListener(listener);
        game.start();
        game.swing(PlayerType.SELF);
        long now = System.currentTimeMillis();
        Thread.sleep(TEST_UNIT_TIME * 3);

        /* イベントの内容とタイミングを確認する */
        assertEquals("発生するイベント数が異なる", 3, listener.mLogs.size());
        GameEventLog log0 = listener.mLogs.get(0);
        assertEquals("サーブイベントが発生していない", GameEvent.SERVE, log0.mEvent);
        assertTrue("サーブイベントの時間がずれている", Math.abs(log0.mTime - (now + TEST_UNIT_TIME * 0)) < tolerance);
        GameEventLog log1 = listener.mLogs.get(1);
        assertEquals("１回目のバウンドのイベントが発生していない", GameEvent.FIRST_BOUND, log1.mEvent);
        assertTrue("１回目のバウンドのイベントの時間がずれている", Math.abs(log1.mTime - (now + TEST_UNIT_TIME * 1)) < tolerance);
        GameEventLog log2 = listener.mLogs.get(2);
        assertEquals("２回目のバウンドのイベントが発生していない", GameEvent.SECOND_BOUND, log2.mEvent);
        assertTrue("２回目のバウンドのイベントの時間がずれている", Math.abs(log2.mTime - (now + TEST_UNIT_TIME * 2)) < tolerance);

        game.shutdown();
    }

    @Test
    public void 相手がリターンに成功() throws Exception {
        /*
         * 最初の３回のイベントは「自分がサーブを打って〜」のテストで実施済み
         * ４回目以降は次の順序でイベントが発生する
         * 150ms ... リターン
         * 250ms ... １回目のバウンド
         */
        GameActionEx listener = new GameActionEx();
        Game game = new GameEx();
        long tolerance = game.getTolerance();
        game.addListener(listener);
        game.start();
        game.swing(PlayerType.SELF);
        long now = System.currentTimeMillis();
        Thread.sleep(TEST_UNIT_TIME * 3);
        game.swing(PlayerType.RIVAL);
        Thread.sleep(TEST_UNIT_TIME * 3);

        /* イベントの内容とタイミングを確認する */
        assertEquals("発生するイベントの数が異なる", 5, listener.mLogs.size());
        GameEventLog log3 = listener.mLogs.get(3);
        assertEquals("リターンイベントが発生していない", GameEvent.RETURN, log3.mEvent);
        assertTrue("リターンイベントの時間がずれている", Math.abs(log3.mTime - (now + TEST_UNIT_TIME * 3)) < tolerance);
        GameEventLog log4 = listener.mLogs.get(4);
        assertEquals("１回目のバウンドのイベントが発生していない", GameEvent.FIRST_BOUND, log4.mEvent);
        assertTrue("１回目のバウンドのイベントの時間がずれている", Math.abs(log4.mTime - (now + TEST_UNIT_TIME * 5)) < tolerance);

        game.shutdown();
    }
}
