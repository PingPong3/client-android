package red.itvirtuoso.pingpong3.app.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import red.itvirtuoso.pingpong3.R;
import red.itvirtuoso.pingpong3.app.net.Connection;
import red.itvirtuoso.pingpong3.app.server.ServerProxy;
import red.itvirtuoso.pingpong3.app.server.local.LocalServerProxy;
import red.itvirtuoso.pingpong3.app.server.socket.SocketServerProxy;

public class TitleFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Button mHowToPlayButton;
    private Button mPlayAsLocalButton;
    private Button mPlayAsInternetButton;
    private TextView mInfoText;

    public enum Status {
        INIT, CONNECTING, WAIT, ERROR,
    }

    public static TitleFragment newInstance() {
        TitleFragment fragment = new TitleFragment();
        return fragment;
    }

    public TitleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_title, container, false);
        mHowToPlayButton = (Button) rootView.findViewById(R.id.how_to_play_button);
        mPlayAsLocalButton = (Button) rootView.findViewById(R.id.play_as_local_button);
        mPlayAsInternetButton = (Button) rootView.findViewById(R.id.play_as_internet_button);
        mInfoText = (TextView) rootView.findViewById(R.id.info_text);

        mHowToPlayButton.setOnClickListener(new HowToPlayButtonClick());
        mPlayAsLocalButton.setOnClickListener(new PlayAsLocalButtonClick());
        mPlayAsInternetButton.setOnClickListener(new PlayAsInternetButtonClick());
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void changeStatus(Status status) {
        boolean isButtonEnabled = false;
        String message = "";

        switch (status) {
            case INIT:
                isButtonEnabled = true;
                message = "";
                break;
            case CONNECTING:
                isButtonEnabled = false;
                message = "サーバに接続中";
                break;
            case WAIT:
                isButtonEnabled = false;
                message = "対戦相手を待っています";
                break;
            case ERROR:
                isButtonEnabled = true;
                message = "接続できませんでした";
                break;
            default:
                /* NOP  */
        }
        mHowToPlayButton.setEnabled(isButtonEnabled);
        mPlayAsLocalButton.setEnabled(isButtonEnabled);
        mPlayAsInternetButton.setEnabled(isButtonEnabled);
        mInfoText.setText(message);
    }

    private class HowToPlayButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mListener.howToPlay();
        }
    }

    private abstract class PlayButtonClick implements View.OnClickListener {
        protected void begin(ServerProxy serverProxy) {
            changeStatus(Status.CONNECTING);
            Connection connection = new Connection(serverProxy);
            mListener.begin(connection);
        }
    }

    private class PlayAsLocalButtonClick extends PlayButtonClick {
        @Override
        public void onClick(View v) {
            ServerProxy serverProxy = new LocalServerProxy(MainActivity.STEP_TIME);
            begin(serverProxy);
        }
    }

    private class PlayAsInternetButtonClick extends PlayButtonClick {
        @Override
        public void onClick(View v) {
            String host = getString(R.string.server_host);
            int port = Integer.parseInt(getString(R.string.server_port));

            ServerProxy serverProxy = new SocketServerProxy(host, port);
            begin(serverProxy);
        }
    }

    public interface OnFragmentInteractionListener {
        public void begin(Connection connection);
        public void howToPlay();
    }
}
