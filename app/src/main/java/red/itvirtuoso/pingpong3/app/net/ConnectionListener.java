package red.itvirtuoso.pingpong3.app.net;

/**
 * Created by kenji on 15/04/12.
 */
public interface ConnectionListener {
    public void onConnectSuccess();
    public void onEvent(Event event);
}
