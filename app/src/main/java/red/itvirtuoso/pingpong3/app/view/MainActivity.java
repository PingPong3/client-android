package red.itvirtuoso.pingpong3.app.view;

import android.app.Activity;
import android.os.Bundle;

import red.itvirtuoso.pingpong3.R;

public class MainActivity extends Activity implements
        TitleFragment.OnFragmentInteractionListener,
        RacketFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_title);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, RacketFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSwing() {
        /* TODO */
    }
}
