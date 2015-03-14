package red.itvirtuoso.pingpong3.app;

import android.app.Activity;
import android.os.Bundle;

import red.itvirtuoso.pingpong3.R;
import red.itvirtuoso.pingpong3.app.model.Game;
import red.itvirtuoso.pingpong3.app.model.GameEventListener;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRacketAdd(GameEventListener listener) {
        mGame.addListener(listener);
    }

    @Override
    public void onRacketRemove(GameEventListener listener) {
        mGame.removeListener(listener);
    }

    @Override
    public void onSwing() {
        mGame.swing(PlayerType.SELF);
    }
}
