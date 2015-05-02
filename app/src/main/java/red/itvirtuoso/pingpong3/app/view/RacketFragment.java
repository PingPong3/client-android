package red.itvirtuoso.pingpong3.app.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import red.itvirtuoso.pingpong3.R;
import red.itvirtuoso.pingpong3.app.server.ConnectionListener;
import red.itvirtuoso.pingpong3.app.server.Event;

public class RacketFragment extends Fragment implements SensorEventListener, ConnectionListener {
    private OnFragmentInteractionListener mListener;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private boolean mSwinging = false;
    private double mGravityZ;
    private float mAccelerationThreshold = (float) 6.0;

    private SoundPool mSoundPool;
    private int mRawFoo;
    private int mRawKa;
    private int mRawKo;
    private int mRawWhistle;

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
        return inflater.inflate(R.layout.fragment_racket, container, false);
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
        if (!mSwinging && acceleration > mAccelerationThreshold * 3) {
            mSwinging = true;
            mListener.onSwing();
        } else if (mSwinging && acceleration < mAccelerationThreshold) {
            mSwinging = false;
        }
    }

    @Override
    public void onConnectSuccess() {
        /* TODO */
    }

    @Override
    public void onReady() {
        /* TODO */
    }

    @Override
    public void onServe(Event event) {
        playSound(mRawFoo);
    }

    @Override
    public void onBoundMyArea(Event event) {
        playSound(mRawKo);
    }

    @Override
    public void onBoundRivalArea(Event event) {
        playSound(mRawKo);
    }

    @Override
    public void onReturn(Event event) {
        playSound(mRawKa);
    }

    @Override
    public void onPointRival() {
        playSound(mRawWhistle);
    }

    public interface OnFragmentInteractionListener {
        public void onSwing();
    }

}
