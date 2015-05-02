package red.itvirtuoso.pingpong3.app.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import red.itvirtuoso.pingpong3.R;
import red.itvirtuoso.pingpong3.app.server.Connection;
import red.itvirtuoso.pingpong3.app.server.ConnectionListener;

public class MainActivity extends Activity implements
        TitleFragment.OnFragmentInteractionListener,
        RacketFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getName();

    private Connection mConnection;
    private ConnectionListener mListener;

    private class MyConnectionListener implements ConnectionListener {
        private RacketFragment mFragment;

        public MyConnectionListener(RacketFragment fragment) {
            mFragment = fragment;
        }

        @Override
        public void onConnectSuccess() {
            /* TODO */
        }

        @Override
        public void onReady() {
            /* TODO */
        }

        @Override
        public void onServe() {
            /* TODO */
        }

        @Override
        public void onBoundMyArea() {
            /* TODO */
        }

        @Override
        public void onBoundRivalArea() {
            /* TODO */
        }

        @Override
        public void onReturn() {
            /* TODO */
        }

        @Override
        public void onPointRival() {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_title);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, TitleFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void start(Connection connection) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        RacketFragment racketFragment = RacketFragment.newInstance();
        transaction.replace(R.id.container, racketFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        mListener = new MyConnectionListener(racketFragment);
        connection.setListener(mListener);
        connection.connect();
    }

    @Override
    public void onSwing() {
        /* TODO */
    }
}
