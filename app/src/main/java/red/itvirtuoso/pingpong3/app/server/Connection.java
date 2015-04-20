package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/04/12.
 */
public abstract class Connection {
    private ConnectionListener mListener;
    private boolean mIsConnected = false;

    public final void connect(ConnectionListener listener) {
        mListener = listener;
        mIsConnected = onConnect();
    }

    public final void disconnect() {

    }

    public final boolean isConnected() {
        return mIsConnected;
    }

    protected final ConnectionListener getListener() {
        return mListener;
    }

    protected abstract boolean onConnect();
}
