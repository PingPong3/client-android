package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/05/03.
 */
public class LocalServerProxy {
    private long stepTime;

    public LocalServerProxy(long stepTime) {
        this.stepTime = stepTime;
    }

    public void send(Packet packet) {

    }

    public Packet receive() {
        return null;
    }
}
