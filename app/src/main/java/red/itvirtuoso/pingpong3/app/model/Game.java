package red.itvirtuoso.pingpong3.app.model;

/**
 * Created by kenji on 15/03/12.
 */
public abstract class Game {
    private GameEventListener mListener;

    public Game(GameEventListener listener) {
        mListener = listener;
    }

    public void swing(PlayerType type) {

    }
}
