package red.itvirtuoso.pingpong3.app.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kenji on 15/03/12.
 */
public abstract class Game {
    private Set<GameEventListener> mListeners = new HashSet<>();

    public void swing(PlayerType type) {
        for(GameEventListener listener : mListeners) {
            listener.onEvent(GameEvent.SERVE);
            listener.onEvent(GameEvent.FIRST_BOUND);
            listener.onEvent(GameEvent.SECOND_BOUND);
        }
    }

    public void addListener(GameEventListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameEventListener listener) {
        mListeners.remove(listener);
    }
}
