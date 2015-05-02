package red.itvirtuoso.pingpong3.app.net;

import red.itvirtuoso.pingpong3.app.server.Event;

/**
 * Created by kenji on 15/04/12.
 */
public interface ConnectionListener {
    public void onConnectSuccess();
    public void onReady();
    public void onServe(Event event);
    public void onBoundMyArea(Event event);
    public void onBoundRivalArea(Event event);
    public void onReturn(Event event);
    public void onPointRival();
}
