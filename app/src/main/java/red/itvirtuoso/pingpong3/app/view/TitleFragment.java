package red.itvirtuoso.pingpong3.app.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.net.InetAddress;
import java.net.UnknownHostException;

import red.itvirtuoso.pingpong3.R;
import red.itvirtuoso.pingpong3.app.net.Connection;
import red.itvirtuoso.pingpong3.app.server.ServerProxy;
import red.itvirtuoso.pingpong3.app.server.local.LocalServerProxy;
import red.itvirtuoso.pingpong3.app.server.socket.SocketServerProxy;

public class TitleFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Button mPlayAsLocalButton;
    private Button mPlayAsInternetButton;

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
        mPlayAsLocalButton = (Button) rootView.findViewById(R.id.play_as_local_button);
        mPlayAsInternetButton = (Button) rootView.findViewById(R.id.play_as_internet_button);

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

    private abstract class PlayButtonClick implements View.OnClickListener {
        protected void begin(ServerProxy serverProxy) {
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
            InetAddress address = null;
            try {
                address = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            int port = Integer.parseInt(getString(R.string.server_port));

            ServerProxy serverProxy = new SocketServerProxy(address, port);
            begin(serverProxy);
        }
    }

    public interface OnFragmentInteractionListener {
        public void begin(Connection connection);
    }
}
