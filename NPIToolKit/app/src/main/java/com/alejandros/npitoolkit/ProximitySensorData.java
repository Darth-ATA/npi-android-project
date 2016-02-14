package com.alejandros.npitoolkit;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

// Inspired by http://bit.ly/1oBpGDI

public class ProximitySensorData implements SensorEventListener {
    private final SensorManager sensorManager;
    private final Sensor proxSensor;
    private final Surprise mainActivity;

    private float[] proxData;

    // Sensor initialization. To be called from Surprise
    public ProximitySensorData(SensorManager sensorManager, Surprise mainActivity) {
        this.sensorManager = sensorManager;
        proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.mainActivity = mainActivity;
    }

    protected void onResume() {
        // Listener to receive the data from the device sensor
        sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        // If the app is not active, stop listening to the sensors
        sensorManager.unregisterListener(this);
    }

    // Function called every moment the sensor changes
    public void onSensorChanged(SensorEvent event) {
        // Sensor filter: we only want the proxSensor sensor data
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            proxData = event.values;
        }

        // Only if the data comes from the proxSensor sensor:
        if ((proxData != null)) {
            // Update the GUI text with the filtered proxSensor data
            mainActivity.showText(proxData[0]);
            mainActivity.manageData(proxData[0]);
        }
    }

    // Need to be defined, but not necessary to implement
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}