package red.itvirtuoso.pingpong3.app.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by kenji on 15/03/12.
 */
public abstract class Game {
    private static final int DEFAULT_UNIT_TIME = 500;
    private static final int FRAME_RATE = 60;
    private static final int WAIT_TIME = 1000 / FRAME_RATE;
    private static final int TOLERANCE = WAIT_TIME * 2;

    /* 状態が遷移する単位時間。大きいほど遅い */
    private int mUnitTime;

    private Set<GameAction> mListeners = new HashSet<>();
    private ScheduledExecutorService mService;
    private List<Reserve> mReserves = new ArrayList<>();

    private class Reserve {
        private long mTime;
        private GameEvent mEvent;

        private Reserve(long delayTime, GameEvent event) {
            mTime = delayTime + System.currentTimeMillis();
            mEvent = event;
        }
    }

    public Game() {
        this(DEFAULT_UNIT_TIME);
    }

    public Game(int unitTime) {
        mUnitTime = unitTime;
    }

    public long getTolerance() {
        return TOLERANCE;
    }

    public void start() {
        mService = Executors.newSingleThreadScheduledExecutor();
        mService.scheduleAtFixedRate(new Loop(), 0, WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        mService.shutdownNow();
    }

    public void swing(PlayerType type) {
        synchronized (mReserves) {
            mReserves.add(new Reserve(0, GameEvent.SERVE));
            mReserves.add(new Reserve(mUnitTime * 1, GameEvent.FIRST_BOUND));
            mReserves.add(new Reserve(mUnitTime * 2, GameEvent.SECOND_BOUND));
        }
    }

    public void addListener(GameAction listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameAction listener) {
        mListeners.remove(listener);
    }

    private class Loop implements Runnable {

        @Override
        public void run() {
            long now = System.currentTimeMillis();
            ArrayList<Reserve> actionedList = new ArrayList<>();
            synchronized (mReserves) {
                for (Reserve reserve : mReserves) {
                    if (now < reserve.mTime) {
                        continue;
                    }
                    for (GameAction listener : mListeners) {
                        listener.onGameAction(reserve.mEvent);
                    }
                    actionedList.add(reserve);
                }
                mReserves.removeAll(actionedList);
            }
        }
    }
}
