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

    // Threshold to play the sound
    private final float minAcc = 3;

    // Player variable
    MediaPlayer sound_lightSaberSwing;

    // Boolean to control whether the lightsaber is on
    boolean isLightSaberOn = false;

    // Variables to control the time from the previous accepted movement
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

        // Instance of the AccelerometerData class to control the data from the sensors
        acelerometro = new AccelerometerData((SensorManager) getSystemService(SENSOR_SERVICE), this);

        // Creation of the player for the sound
        sound_lightSaberSwing = MediaPlayer.create(this, R.raw.light_saber_swing);

        // Initialisation of the time-control variables
        prevTimeSwing = System.currentTimeMillis();
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

    public void manageData(float[] data){
        changeAccText(data);

        // If the force in the X or Z axes is grater than a threshold, play the Swing sound
        // (the lightSaberSwing() function will check whether the lightsaber is on)
        if(Math.abs(data[0]) > minAcc ){
            lightSaberSwing();
        }

    }

    // Function to be called in order to modify the acc_text TextView.
    // It will print the acceleration in all the three axes.
    protected void changeAccText(float[] acc) {
        acc_text.setText(getString(R.string.sound_acc) + Float.toString(acc[0]) + " X, "
                + Float.toString(acc[1]) + " Y, "
                + Float.toString(acc[2]) + " Z");
    }

    // It plays the Swing sound only if 0.5 seconds have passed since the last play AND the lightsaber
    // is on.
    protected void lightSaberSwing(){
        // Compute the time difference from the previous play
        long difference = System.currentTimeMillis() - prevTimeSwing;

        // If the 0.5 seconds have passed, play the sound.
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
}
