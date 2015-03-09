package red.itvirtuoso.pingpong3.app;

import android.app.Activity;
import android.os.Bundle;

import red.itvirtuoso.pingpong3.R;

public class MainActivity extends Activity implements TitleFragment.OnFragmentInteractionListener {

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
}
