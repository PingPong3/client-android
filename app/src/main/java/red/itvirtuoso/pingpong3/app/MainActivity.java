package red.itvirtuoso.pingpong3.app;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;

import java.util.List;

import red.itvirtuoso.pingpong3.R;

public class MainActivity extends Activity implements
        TitleFragment.OnFragmentInteractionListener,
        RacketFragment.OnFragmentInteractionListener,
        SensorEventListener {

    private static final String TAG = MainActivity.class.getName();

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private boolean mSwinging = false;
    private double mGravityZ;
    private float mAccelerationThreshold = (float) 6.0;

    private SoundPool mSoundPool;
    private int mRawFoo;

    private void playSound(int id) {
        mSoundPool.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 加速度センサーの登録 */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensorList.size() > 0) {
            mAccelerometer = sensorList.get(0);
        }

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
        /* 加速度センサーリスナーの登録 */
        if (this.mSensorManager != null) {
            mSensorManager.registerListener(this, mAccelerometer,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        /* サウンドの登録 */
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mRawFoo = mSoundPool.load(this, R.raw.foo, 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /* サウンドの解放 */
        mSoundPool.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /* 加速度センサーリスナーの解放 */
        mSensorManager.unregisterListener(this);
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
            playSound(mRawFoo);
        } else if (mSwinging && acceleration < mAccelerationThreshold) {
            mSwinging = false;
        }
    }
}
