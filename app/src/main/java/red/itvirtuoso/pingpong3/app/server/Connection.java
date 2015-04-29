package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/04/12.
 */
public abstract class Connection {
    private ConnectionListener mListener;
    private boolean mIsConnected = false;

    protected abstract boolean onConnect();

    public final void connect(ConnectionListener listener) {
        mListener = listener;
        mIsConnected = onConnect();
    }

    public final void disconnect() {
        mIsConnected = false;
    }

    public final boolean isConnected() {
        return mIsConnected;
    }

    protected final ConnectionListener getListener() {
        return mListener;
    }

    public abstract void serve();
}
