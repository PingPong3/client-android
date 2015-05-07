package red.itvirtuoso.pingpong3.app.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

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
    public void start(final Connection connection) {
        final RacketFragment racketFragment = RacketFragment.newInstance();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mConnection = connection;
                mConnection.setListener(racketFragment);
                try {
                    mConnection.connect();
                } catch (IOException e) {
                    Log.w(TAG, "接続に失敗しました", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!mConnection.isConnected()) {
                    return;
                }
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, racketFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        };
        task.execute();
    }

    @Override
    public void onSwing() {
        try {
            mConnection.swing();
        } catch (IOException e) {
            Log.e(TAG, "通信に失敗しました", e);
        }
    }
}
