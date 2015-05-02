package red.itvirtuoso.pingpong3.app.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import red.itvirtuoso.pingpong3.R;

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

    public interface OnFragmentInteractionListener {
    }

    private class PlayAsLocalButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment racketFragment = RacketFragment.newInstance();
            transaction.replace(R.id.container, racketFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private class PlayAsInternetButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "インターネット対戦は作成中", Toast.LENGTH_SHORT).show();
        }
    }
}
