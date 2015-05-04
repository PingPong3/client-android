package red.itvirtuoso.pingpong3.app.server.local;

/**
 * Created by kenji on 15/05/03.
 */
abstract class ModeAction extends Action {
    private Mode mode;

    public ModeAction(long time, Mode mode) {
        super(time);
        this.mode = mode;
    }
}
