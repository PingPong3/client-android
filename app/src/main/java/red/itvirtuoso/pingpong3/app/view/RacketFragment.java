package red.itvirtuoso.pingpong3.app.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import red.itvirtuoso.pingpong3.R;
import red.itvirtuoso.pingpong3.app.net.Event;

public class RacketFragment extends Fragment implements SensorEventListener {
    private static final String SP_SENSITIVITY = "SENSITIVITY";

    private OnFragmentInteractionListener mListener;

    private TextView mScoreText;
    private SeekBar mSensitivitySeek;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private boolean mSwinging = false;
    private long mLastSwinging;
    private double mGravityZ;

    private SoundPool mSoundPool;
    private int mRawFoo;
    private int mRawKa;
    private int mRawKo;
    private int mRawWhistle;

    private Handler mHandler = new Handler();

    public static RacketFragment newInstance() {
        RacketFragment fragment = new RacketFragment();
        return fragment;
    }

    public RacketFragment() {
        // Required empty public constructor
    }

    private void playSound(int id) {
        mSoundPool.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* 加速度センサーの登録 */
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensorList.size() > 0) {
            mAccelerometer = sensorList.get(0);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_racket, container, false);
        mScoreText = (TextView) rootView.findViewById(R.id.score_text);
        mSensitivitySeek = (SeekBar) rootView.findViewById(R.id.sensitivity_seek);
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
    public void onResume() {
        super.onResume();
        /* 感度シークバーの設定 */
        mSensitivitySeek.setProgress(getSensitivity());

        /* 加速度センサーリスナーの登録 */
        if (this.mSensorManager != null) {
            mSensorManager.registerListener(this, mAccelerometer,
                    SensorManager.SENSOR_DELAY_GAME);
        }

        /* サウンドの登録 */
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        mRawFoo = mSoundPool.load(getActivity(), R.raw.foo, 1);
        mRawKa = mSoundPool.load(getActivity(), R.raw.ka, 1);
        mRawKo = mSoundPool.load(getActivity(), R.raw.ko, 1);
        mRawWhistle = mSoundPool.load(getActivity(), R.raw.whistle, 1);
    }

    @Override
    public void onPause() {
        super.onPause();
        /* サウンドの解放 */
        mSoundPool.release();

        /* 感度シークバーの保存 */
        setSensitivity(mSensitivitySeek.getProgress());
    }

    @Override
    public void onStop() {
        super.onStop();
        /* 加速度センサーリスナーの解放 */
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        /* nop */
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        /* 重力の影響を取り除いた加速度を取得する */
        /* http://developer.android.com/intl/ja/reference/android/hardware/SensorEvent.html */
        final float alpha = 0.8f;
        mGravityZ = alpha * mGravityZ + (1 - alpha) * event.values[2];
        double acceleration = Math.abs(event.values[2] - mGravityZ);
        double min = 8.0d;   /* for small phone */
        double max = 26.0d;  /* for big phone */
        double sensitivity = ((max + min) / 2 - (mSensitivitySeek.getProgress() - 50) / 50.0d * (max - min) / 2);
        if (!mSwinging && acceleration > sensitivity && mLastSwinging < System.currentTimeMillis()) {
            mSwinging = true;
            mListener.onSwing();
        } else if (mSwinging && acceleration < 2.0d) {
            mSwinging = false;
            mLastSwinging = System.currentTimeMillis() + 2000;
        }
    }

    public void executeEvent(final Event event) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int[] data = event.getData();
                switch (event.getType()) {
                    case ME_SERVE:
                    case RIVAL_SERVE:
                        playSound(mRawKa);
                        break;
                    case ME_BOUND_MY_AREA:
                    case ME_BOUND_RIVAL_AREA:
                    case RIVAL_BOUND_MY_AREA:
                    case RIVAL_BOUND_RIVAL_AREA:
                        playSound(mRawKo);
                        break;
                    case ME_RETURN:
                    case RIVAL_RETURN:
                        playSound(mRawKa);
                        break;
                    case ME_POINT:
                    case RIVAL_POINT:
                        mScoreText.setText(data[0] + " - " + data[1]);
                        playSound(mRawWhistle);
                        break;
                    default:
                        /* nop */
                }
            }
        };
        mHandler.post(runnable);
    }

    private int getSensitivity() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        return sp.getInt(SP_SENSITIVITY, 50);
    }

    private void setSensitivity(int sensitivity) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        sp.edit().putInt(SP_SENSITIVITY, sensitivity).commit();
    }

    public interface OnFragmentInteractionListener {
        public void onSwing();
    }
}
