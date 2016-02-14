package com.alejandros.voicecompass;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class Compass extends AppCompatActivity implements SensorEventListener {

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
    private float providedOrientation;

    private int errorMargin;

    TextView tvHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        // Receives the message of VoiceCompass
        Intent intent = getIntent();
        int[] orientation_error = intent.getIntArrayExtra(VoiceCompass.EXTRA_MESSAGE);

        // Splits the data of the message
        providedOrientation = orientation_error[0];
        errorMargin = orientation_error[1];

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

    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    // Not used but must be implemented
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // It executes when the sensor change
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Can be change the accelerometer or the magnetometer
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values.clone();
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values.clone();

        // Need the data of both sensors for the compass
        if ((mGravity != null) && (mGeomagnetic != null)) {
            /*
             * For the orientation of the device, it calculates the azimut:
             * "angle of the reference (north) and a line in the middle of the observer
             * and the interest point in the same direction field" - Wikipedia
             * If the azimut is 0º, the device is oriented to the North, 90º to the East,
             * 180º to the South and 270º to the West
             *
             * For calculate the azimut, first calculate the rotation matrix (using the
             * accelerometer and magnetometer data) and after it uses this matrix obtaining
             * a vector that his first coordinate is de azimut
             */
            SensorManager.getRotationMatrix(rotationMatrix, null, mGravity, mGeomagnetic);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimut = orientation[0]; // orientation contains: azimut, pitch and roll

            // Azimut to degrees
            float deviceOrientation = (float)(Math.toDegrees(azimut)+360)%360;

            boolean rightHeading = isInTheRightDirection(deviceOrientation);

            if(rightHeading) {
                int green = Color.parseColor("#008000"); //Green colour
                image.setColorFilter(green);
            }
            else{
                image.clearColorFilter();
            }

            RotateAnimation animation = new RotateAnimation(
                    previusOrientation,
                    -(deviceOrientation-providedOrientation),
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            // how long the animation will take place
            animation.setDuration(210);

            // set the animation after the end of the reservation status
            animation.setFillAfter(true);

            tvHeading.setText(getString(R.string.compass_heading) + Float.toString((deviceOrientation-providedOrientation+360)%360) + getString(R.string.compass_degrees));

            // Start the animation
            image.startAnimation(animation);

            previusOrientation = -(deviceOrientation-providedOrientation);
        }
    }

    public boolean isInTheRightDirection(float deviceOrientation){
        // When you have to point the north has to change the conditions
        if(providedOrientation == 0){
            if((deviceOrientation >= (360 - errorMargin)) ||
                    (deviceOrientation <= errorMargin)){
                return true;
            }
            else{
                return false;
            }
        }
        else {
            if (((deviceOrientation >= (providedOrientation - errorMargin + 360) % 360)) &&
                    (deviceOrientation <= (providedOrientation + errorMargin + 360) % 360)) {//Math.abs(deviceOrientation - providedOrientation) <= errorMargin){
                return true;
            } else {
                return false;
            }
        }
    }
}
