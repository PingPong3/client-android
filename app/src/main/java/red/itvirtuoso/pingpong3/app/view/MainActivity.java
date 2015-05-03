package red.itvirtuoso.pingpong3.app.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import red.itvirtuoso.pingpong3.R;
import red.itvirtuoso.pingpong3.app.net.Connection;

public class MainActivity extends Activity implements
        TitleFragment.OnFragmentInteractionListener,
        RacketFragment.OnFragmentInteractionListener {

    public static final long STEP_TIME = 750;
    private static final String TAG = MainActivity.class.getName();

    private Connection mConnection;

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

        mConnection = connection;
        mConnection.setListener(racketFragment);
        mConnection.connect();
    }

    @Override
    public void onSwing() {
        mConnection.swing();
    }
}
