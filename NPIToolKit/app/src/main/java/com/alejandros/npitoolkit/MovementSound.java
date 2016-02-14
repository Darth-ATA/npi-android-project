package com.alejandros.npitoolkit;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MovementSound extends AppCompatActivity {
    private AccelerometerData acelerometro;
    private TextView acc_text;
    private RelativeLayout canvas;

    // Player variables
    MediaPlayer sound_lightSaberOn;
    MediaPlayer sound_lightSaberSwing;

    // Boolean to control whether the lightsaber is on
    boolean isLightSaberOn = false;

    // Variables to control the time from the previous accepted movement
    long prevTime;
    long prevTimeSwing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initial settings
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_sound);

        // Toolbar setting
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // GUI objects that will be modified
        acc_text= (TextView) findViewById(R.id.acc_text);
        canvas = (RelativeLayout) findViewById(R.id.canvas);

        // Instance of the AccelerometerData class to control the data from the sensors
        acelerometro = new AccelerometerData((SensorManager) getSystemService(SENSOR_SERVICE), this);

        // Creation of the player for both sounds
        sound_lightSaberOn = MediaPlayer.create(this, R.raw.light_saber_on);
        sound_lightSaberSwing = MediaPlayer.create(this, R.raw.light_saber_swing);

        // Initialisation of the time-control variables
        prevTime = System.currentTimeMillis();
        prevTimeSwing = prevTime;
    }

    // Call accelerometer onResume
    protected void onResume() {
        super.onResume();
        acelerometro.onResume();
    }

    // Call accelerometer onPause
    protected void onPause() {
        super.onPause();
        acelerometro.onPause();
    }

    // Function to be called from AccelerometerData in order to modify the acc_text TextView.
    // It will print the acceleration in all the three axes.
    protected void changeAccText(float[] acc) {
        acc_text.setText(getString(R.string.sound_acc) + Float.toString(acc[0]) + " X, "
                + Float.toString(acc[1]) + " Y, "
                + Float.toString(acc[2]) + " Z");
    }

    // It plays the On sound only if 1 second has passed since the last play.
    // This function should be called from AccelerometerSound only when required.
    public void lightSaberOn(){
        // Compute the time difference from the previous play
        long difference = System.currentTimeMillis() - prevTime;

        // If 1 second has passed, play the sound, change the background color and toggle the
        // boolean isLightSaberOn value.
        if(difference > 1000) {
            prevTime = System.currentTimeMillis();
            if (!isLightSaberOn) {
                isLightSaberOn = true;
                canvas.setBackgroundColor(0xFFC32424);
                sound_lightSaberOn.start();
            } else {
                isLightSaberOn = false;
                canvas.setBackgroundColor(0xFFFFFFFF);
                sound_lightSaberOn.start();
            }
        }
    }

    // It plays the Swing sound only if 0.5 seconds have passed since the last play AND the lightsaber
    // is on.
    // This function should be called from AccelerometerSound only when required.
    public void lightSaberSwing(){
        // Compute the time difference from the previous play
        long difference = System.currentTimeMillis() - prevTimeSwing;

        // If the lightsaber is on AND 0.5 seconds have passed AND the sound has stopped, play the sound.
        if(isLightSaberOn) {
            if (difference > 500) {
                prevTimeSwing = System.currentTimeMillis();
                if(sound_lightSaberSwing.isPlaying()){
                    // Pause the sound and move the pointer to 100ms
                    sound_lightSaberSwing.pause();
                    sound_lightSaberSwing.seekTo(100);
                }
                sound_lightSaberSwing.start();
            }
        }
        else{
            // Move the pointer to 100ms
            sound_lightSaberSwing.seekTo(100);
        }
    }


}
