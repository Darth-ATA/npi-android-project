package com.alejandros.npitoolkit;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class VoiceCompass extends AppCompatActivity implements SensorEventListener {
    // define the display assembly compass picture
    private ImageView image;

    // device sensor manager
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] mGravity;
    private float[] mGeomagnetic;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    private float previusOrientation = 180f;
    private float providedOrientation = 0;

    private int errorMargin;

    TextView tvHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_compass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // our compass image
        image = (ImageView) findViewById(R.id.imageViewCompass);

        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) findViewById(R.id.tvHeading);

        // initialize android device sensor capabilites
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

        // for the system's orientation sensor registered listener
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    // It executes when the sensor change
    public void onSensorChanged(SensorEvent event) {
        // Can be change the accelerometer or the magnetometer
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values.clone();
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values.clone();

        // Need the data of both sensors
        if (mGravity != null && mGeomagnetic != null) {
            SensorManager.getRotationMatrix(rotationMatrix, null, mGravity, mGeomagnetic);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimut = orientation[0]; // orientation contains: azimut, pitch and roll

            // Azimut to degrees
            float deviceOrientation = (float)(Math.toDegrees(azimut)+360)%360;

            RotateAnimation animation = new RotateAnimation(
                    previusOrientation,
                    -(deviceOrientation+providedOrientation),
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            tvHeading.setText("Heading: " + Float.toString(providedOrientation) + "degrees");

            // how long the animation will take place
            animation.setDuration(210);

            // set the animation after the end of the reservation status
            animation.setFillAfter(true);

            // Start the animation
            image.startAnimation(animation);

            previusOrientation = -(deviceOrientation+providedOrientation);
        }
    }
}
