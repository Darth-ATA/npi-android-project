package com.alejandros.npitoolkit;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

// Inspired by http://bit.ly/1oBpGDI

public class AccelerometerData implements SensorEventListener {
    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final MovementSound mainActivity;

    // Variables to store accelerometer data
    private float[] accelerometerData;
    private float[] gravity;
    private float[] linealAcc;

    // Alpha filter to analyze sensor signal
    private final float alphaFilter = 0.8f;

    // Threshold to play the sound
    private final float minAcc = 5;

    // Sensor initialization. To be called from MovementSound
    public AccelerometerData(SensorManager sensorManager, MovementSound mainActivity) {
        this.sensorManager = sensorManager;
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mainActivity = mainActivity;

        accelerometerData = null;
        gravity = new float[3];
        linealAcc = new float[3];
    }

    protected void onResume() {
        // Listener to receive the data from the device sensor
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        // If the app is not active, stop listening to the sensors
        sensorManager.unregisterListener(this);
    }

    // Function called every moment the sensor changes
    public void onSensorChanged(SensorEvent event) {
        // Sensor filter: we only want the accelerometer sensor data
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerometerData = event.values;
        }

        // Only if the data comes from the accelerometer sensor:
        if ((accelerometerData != null)) {
            // Isolate the gravity force by using the alpha low-pass filter.
            gravity[0] = alphaFilter * gravity[0] + (1 - alphaFilter) * event.values[0];
            gravity[1] = alphaFilter * gravity[1] + (1 - alphaFilter) * event.values[1];
            gravity[2] = alphaFilter * gravity[2] + (1 - alphaFilter) * event.values[2];

            // Remove the gravity contribution to the data with a high-pass filter
            linealAcc[0] = event.values[0] - gravity[0];
            linealAcc[1] = event.values[1] - gravity[1];
            linealAcc[2] = event.values[2] - gravity[2];

            // Update the GUI text with the filtered accelerometer data
            mainActivity.changeAccText(linealAcc);

            // If the force in the Y axe is greater than a threshold, turn the lightsaber on
            if (Math.abs(linealAcc[1]) > minAcc){
                mainActivity.lightSaberOn();
            }

            // If the force in the X or Z axes is grater than a threshold, play the Swing sound
            // (the lightSaberSwing() function will check whether the lightsaber is on)
            if(Math.abs(linealAcc[0]) > minAcc*0.5 ||
               Math.abs(linealAcc[2]) > minAcc*0.5 ){
                mainActivity.lightSaberSwing();
            }
        }
    }

    // Need to be defined, but not necessary to implement
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}