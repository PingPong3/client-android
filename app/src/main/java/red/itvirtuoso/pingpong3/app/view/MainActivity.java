package red.itvirtuoso.pingpong3.app.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import java.io.IOException;

import red.itvirtuoso.pingpong3.R;
import red.itvirtuoso.pingpong3.app.net.Connection;
import red.itvirtuoso.pingpong3.app.net.ConnectionListener;
import red.itvirtuoso.pingpong3.app.net.Event;

public class MainActivity extends Activity implements
        ConnectionListener,
        HowToPlayFragment.OnFragmentInteractionListener,
        TitleFragment.OnFragmentInteractionListener,
        RacketFragment.OnFragmentInteractionListener {

    public static final long STEP_TIME = 650;
    private static final String TAG = MainActivity.class.getName();

    private Handler mHandler = new Handler();
    private Connection mConnection;
    private HowToPlayFragment mHowToPlayFragment;
    private TitleFragment mTitleFragment;
    private RacketFragment mRacketFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mTitleFragment = TitleFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mTitleFragment)
                    .commit();
        }

        mHowToPlayFragment = HowToPlayFragment.newInstance();
        mRacketFragment = RacketFragment.newInstance();
    }

    @Override
    public void howToPlay() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, mHowToPlayFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void begin(final Connection connection) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mConnection = connection;
                mConnection.setListener(MainActivity.this);
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
                    mTitleFragment.changeStatus(TitleFragment.Status.ERROR);
                    return;
                }
                mTitleFragment.changeStatus(TitleFragment.Status.WAIT);
            }
        };
        task.execute();
    }

    @Override
    public void onEvent(final Event event) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onEventImpl(event);
            }
        });
    }

    private void onEventImpl(Event event) {
        switch (event.getType()) {
            case BEGIN:
                onEventBegin();
                break;
            default:
                mRacketFragment.executeEvent(event);
        }
    }

    private void onEventBegin() {
        mTitleFragment.changeStatus(TitleFragment.Status.INIT);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, mRacketFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onSwing() {
        try {
            mConnection.swing();
        } catch (IOException e) {
            Log.e(TAG, "通信に失敗しました", e);
        }
    }

    @Override
    public void onBackPressed() {
        if (mConnection != null) {
            mConnection.disconnect();
            mConnection = null;
        }
        super.onBackPressed();
    }
}
