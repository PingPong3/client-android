package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/04/12.
 */
public class LocalConnection extends Connection {
    private long unitTime;

    public LocalConnection(long unitTime) {
        this.unitTime = unitTime;
    }

    @Override
    protected boolean onConnect() {
        getListener().onConnectSuccess();
        getListener().onReady();
        return true;
    }

    @Override
    public void serve() {

    }
}
