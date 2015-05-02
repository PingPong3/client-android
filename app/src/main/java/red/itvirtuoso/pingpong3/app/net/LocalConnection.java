package red.itvirtuoso.pingpong3.app.net;

import red.itvirtuoso.pingpong3.app.server.Event;
import red.itvirtuoso.pingpong3.app.server.Turn;

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
        Thread.sleep(step * this.unitTime);
    }

    @Override
    public void swing() {
        try {
            swingImpl();
        } catch (InterruptedException e) {
            /* nop */
        }
    }

    private void swingImpl() throws InterruptedException {
        ConnectionListener listener = getListener();
        listener.onServe(new Event(Turn.RIVAL));
        sleepStep(1);
        listener.onBoundMyArea(new Event(Turn.RIVAL));
        sleepStep(1);
        listener.onBoundRivalArea(new Event(Turn.RIVAL));
        sleepStep(1);
        listener.onReturn(new Event(Turn.ME));
        sleepStep(2);
        listener.onBoundMyArea(new Event(Turn.ME));
        sleepStep(2);
        listener.onPointRival();
    }
}
