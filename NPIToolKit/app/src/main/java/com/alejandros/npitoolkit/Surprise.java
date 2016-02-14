package com.alejandros.npitoolkit;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Surprise extends AppCompatActivity {
    private ProximitySensorData proxSensor;
    private AccelerometerData accSensor;

    private SensorManager mSensorManager;
    private Sensor mProximitySensor;

    private TextView proxText;

    private float prevData;

    MediaPlayer sound_lightSaberOn;

    private boolean isLightSaberOn;
    private boolean firstTime;

//    private RelativeLayout canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surprise);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        canvas = (RelativeLayout) findViewById(R.id.surpriseCanvas);
        proxText = (TextView) findViewById(R.id.proximityText);

        // Instance of the AccelerometerData class to control the data from the sensors
        proxSensor = new ProximitySensorData((SensorManager) getSystemService(Context.SENSOR_SERVICE), this);

        prevData = 9999999;

        sound_lightSaberOn = MediaPlayer.create(this, R.raw.light_saber_on);

        isLightSaberOn = false;
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

    // It plays the On sound only if 1 second has passed since the last play.
    // This function should be called from ProximitySensorData only when required.
    public void manageData(float data) {
        if (prevData < 5.0 && data > 10.0) {
            prevData = data;
            isLightSaberOn = true;
            sound_lightSaberOn.start();
        } else if (prevData > 10.0 && data < 5.0){
            prevData = data;
            isLightSaberOn = false;

            if(firstTime) {
                firstTime = false;
            }
            else{
                sound_lightSaberOn.start();
            }
        }
    }
}