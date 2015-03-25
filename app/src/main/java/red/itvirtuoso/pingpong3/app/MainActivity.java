package red.itvirtuoso.pingpong3.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import red.itvirtuoso.pingpong3.R;
import red.itvirtuoso.pingpong3.app.model.Game;
import red.itvirtuoso.pingpong3.app.model.GameAction;
import red.itvirtuoso.pingpong3.app.model.PlayerType;
import red.itvirtuoso.pingpong3.app.model.SingleGame;

public class MainActivity extends Activity implements
        TitleFragment.OnFragmentInteractionListener,
        RacketFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getName();

    private Game mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_title);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, RacketFragment.newInstance())
                    .commit();
        }

        /* TODO: プレイスタイルは変更できるようにする */
        mGame = new SingleGame();
        mGame.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRacketAdd(GameAction listener) {
        mGame.addListener(listener);
    }

    @Override
    public void onRacketRemove(GameAction listener) {
        mGame.removeListener(listener);
    }

    @Override
    public void onSwing() {
        Log.d(TAG, "onSwing");
        mGame.swing(PlayerType.SELF);
    }
}
