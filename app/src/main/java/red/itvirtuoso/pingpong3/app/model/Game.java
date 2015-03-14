package red.itvirtuoso.pingpong3.app.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kenji on 15/03/12.
 */
public abstract class Game implements Runnable {
    private Set<GameEventListener> mListeners = new HashSet<>();
    private ExecutorService mService;
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
        mService = Executors.newSingleThreadExecutor();
        mService.execute(this);
        mService.shutdown();
    }

    public void shutdown() {
        mService.shutdownNow();
    }

    public void swing(PlayerType type) {
        synchronized (mReserves) {
            mReserves.add(new Reserve(0, GameEvent.SERVE));
            mReserves.add(new Reserve(500, GameEvent.FIRST_BOUND));
            mReserves.add(new Reserve(1000, GameEvent.SECOND_BOUND));
        }
    }

    public void addListener(GameEventListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameEventListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        while (!Thread.interrupted()) {
            sendEvent();
            now += 16;
            waitNextFrame(now);
        }
    }

    private void sendEvent() {
        long now = System.currentTimeMillis();
        ArrayList<Reserve> actionedList = new ArrayList<>();
        synchronized (mReserves) {
            for (Reserve reserve : mReserves) {
                if (now < reserve.mTime) {
                    continue;
                }
                for (GameEventListener listener : mListeners) {
                    listener.onGameAction(reserve.mEvent);
                }
                actionedList.add(reserve);
            }
            mReserves.removeAll(actionedList);
        }
    }

    private void waitNextFrame(long nextTime) {
        long elapsed = nextTime - System.currentTimeMillis();
        if (elapsed <= 0) {
            return;
        }
        try {
            Thread.sleep(elapsed);
        } catch (InterruptedException e) {
            /* nop */
        }
        return;
    }
}
