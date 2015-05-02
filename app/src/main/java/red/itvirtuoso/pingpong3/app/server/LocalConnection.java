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

    private void sleepStep(int step) throws InterruptedException {
        Thread.sleep(step + this.unitTime);
    }

    @Override
    public void serve() {
        try {
            serveImpl();
        } catch (InterruptedException e) {
            /* nop */
        }
    }

    private void serveImpl() throws InterruptedException {
        ConnectionListener listener = getListener();
        listener.onServe();
        sleepStep(1);
        listener.onBoundMyArea();
        sleepStep(1);
        listener.onBoundRivalArea();
        sleepStep(1);
        listener.onReturn();
        sleepStep(2);
        listener.onBoundMyArea();
        sleepStep(2);
        listener.onPointRival();
    }
}
