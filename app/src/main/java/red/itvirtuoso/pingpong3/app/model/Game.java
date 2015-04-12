package red.itvirtuoso.pingpong3.app.model;

import android.util.Log;

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
    private static final String TAG = Game.class.getName();

    private static final int DEFAULT_UNIT_TIME = 500;
    private static final int FRAME_RATE = 60;
    private static final int WAIT_TIME = 1000 / FRAME_RATE;
    private static final int TOLERANCE = WAIT_TIME * 2;

    /* 状態が遷移する単位時間。大きいほど遅い */
    private int mUnitTime;

    private Set<GameAction> mListeners = new HashSet<>();
    private ScheduledExecutorService mService;
    private List<Reserve> mReserves = new ArrayList<>();
    private GameEvent mLastEvent = GameEvent.READY;

    private class Reserve {
        private long mTime;
        private GameEvent mEvent;
        private PlayerType mType;

        private Reserve(long delayTime, GameEvent event, PlayerType type) {
            mTime = delayTime + System.currentTimeMillis();
            mEvent = event;
            mType = type;
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
            switch (type) {
                case SELF:
                    swingAsSelf();
                    break;
                case RIVAL:
                    swingAsRival();
                    break;
                default:
                    /* nop */
            }
        }
    }

    private void swingAsSelf() {
        Log.d(TAG, "lastEvent = " + mLastEvent);
        if (mLastEvent == GameEvent.READY) {
            mReserves.add(new Reserve(0, GameEvent.SERVE, PlayerType.SELF));
            mReserves.add(new Reserve(mUnitTime * 1, GameEvent.FIRST_BOUND, PlayerType.SELF));
            mReserves.add(new Reserve(mUnitTime * 2, GameEvent.SECOND_BOUND, PlayerType.SELF));
        } else if (mLastEvent == GameEvent.SECOND_BOUND) {
            mReserves.add(new Reserve(0, GameEvent.RETURN, PlayerType.SELF));
            mReserves.add(new Reserve(mUnitTime * 2, GameEvent.SECOND_BOUND, PlayerType.SELF));
        }
    }

    private void swingAsRival() {
        if (mLastEvent == GameEvent.SECOND_BOUND) {
            mReserves.add(new Reserve(0, GameEvent.RETURN, PlayerType.RIVAL));
            mReserves.add(new Reserve(mUnitTime * 2, GameEvent.SECOND_BOUND, PlayerType.RIVAL));
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
                        listener.onGameAction(reserve.mEvent, reserve.mType);
                    }
                    actionedList.add(reserve);
                }
                if (actionedList.size() > 0) {
                    mReserves.removeAll(actionedList);
                    mLastEvent = actionedList.get(actionedList.size() - 1).mEvent;
                }
            }
        }
    }
}
