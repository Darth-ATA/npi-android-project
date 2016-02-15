package com.alejandros.surprise;

import android.content.Context;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Surprise extends AppCompatActivity {
    private ProximitySensorData proxSensor;

    private TextView proxText;

    private float prevData;

    MediaPlayer sound_lightSaberOn;

    private boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surprise);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        proxText = (TextView) findViewById(R.id.proximityText);

        // Instance of the ProximitySensorData class to control the data from the sensors
        proxSensor = new ProximitySensorData((SensorManager) getSystemService(Context.SENSOR_SERVICE), this);

        // Previuos data initialisation
        prevData = 9999999;

        // Sound initialisation
        sound_lightSaberOn = MediaPlayer.create(this, R.raw.light_saber_on);

        // Variable to distinguish between the first toggle and the other ones.
        firstTime = true;
    }


    // Call proxSensor onResume
    protected void onResume() {
        super.onResume();
        proxSensor.onResume();
    }

    // Call proxSensor onPause
    protected void onPause() {
        super.onPause();
        proxSensor.onPause();
    }


    protected void showText(float data){
        proxText.setText("Proximity sensor data: " + Float.toString(data));
    }

    // It plays the On sound whenever the sensor changes from near to far or viceversa.
    public void manageData(float data) {
        if (prevData < 5.0 && data > 10.0) {
            prevData = data;
            sound_lightSaberOn.start();
        } else if (prevData > 10.0 && data < 5.0){
            prevData = data;

            // Changing from far to near => saving phone in pocket => play the sound unless it is
            // the first time
            if(firstTime) {
                firstTime = false;
            }
            else{
                sound_lightSaberOn.start();
            }
        }
    }
}
