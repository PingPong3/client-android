package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/05/03.
 */
public abstract class ModeAction extends Action {
    private Mode mode;

    public ModeAction(long time, Mode mode) {
        super(time);
        this.mode = mode;
    }
}
