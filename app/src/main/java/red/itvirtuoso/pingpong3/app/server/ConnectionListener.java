package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/04/12.
 */
public interface ConnectionListener {
    public void onConnectSuccess();
    public void onReady();
    public void onBoundMyArea();
    public void onBoundRivalArea();
    public void onReturn();
}
