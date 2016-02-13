package com.alejandros.npitoolkit;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MovementSound extends AppCompatActivity {
    private AccelerometerData acelerometro;
    private TextView acc_text;
    private RelativeLayout canvas;

    MediaPlayer sound_lightSaberOn;
    MediaPlayer sound_lightSaberSwing;
    MediaPlayer sound_lightSaberIdle;
    SoundPool sp;

//    int sound_lightSaberOn;
//    int sound_lightSaberSwing;

    boolean isLightSaberOn = false;

    long prevTime;
    long prevTimeSwing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_sound);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        acc_text= (TextView) findViewById(R.id.acc_text);
        canvas = (RelativeLayout) findViewById(R.id.canvas);

        // Instaciamos la clase AccelerometerData que se encargará de tomar el dato del sensor
        acelerometro = new AccelerometerData((SensorManager) getSystemService(SENSOR_SERVICE), this);

//        // http://stackoverflow.com/a/17070454/3248221
//        SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
//
//        /** sound_lightSaberOn for Later handling of sound pool **/
//        sound_lightSaberOn = sp.load(this, R.raw.light_saber_on, 1); // in 2nd param u have to pass your desire ringtone
//        sound_lightSaberSwing = sp.load(this, R.raw.light_saber_swing, 1); // in 2nd param u have to pass your desire ringtone

        sound_lightSaberOn = MediaPlayer.create(this, R.raw.light_saber_on);
        sound_lightSaberSwing = MediaPlayer.create(this, R.raw.light_saber_swing);
//        sound_lightSaberIdle = MediaPlayer.create(this, R.raw.light_saber_idle);

        prevTime = System.currentTimeMillis();
        prevTimeSwing = prevTime;
    }

    protected void onResume() {
        super.onResume();
        acelerometro.onResume();
    }

    protected void onPause() {
        super.onPause();
        acelerometro.onPause();
    }

    // Modifica el TextView que contiene la aceleración lineal en el eje X
    // que posee el dispositivo en el momento. Se llama desde AcelerometroData
    protected void fijarTextoAceleracion(float[] aceleracion) {
        acc_text.setText("Aceleración:\n" + Float.toString(aceleracion[0]) + " X, "
                + Float.toString(aceleracion[1]) + " Y, "
                + Float.toString(aceleracion[2]) + " Z");
    }

    // Reproduce el sonido y realiza la animación de agitar el icono del móvil
    // Se llama desde AccelerometerData cuando la aceleración lineal
    // en el eje X supera un mínimo.
    public void lightSaberOn(){
        long difference = System.currentTimeMillis() - prevTime;

        if(difference > 1000) {
            prevTime = System.currentTimeMillis();
            if (!isLightSaberOn) {
                isLightSaberOn = true;
                canvas.setBackgroundColor(0xFFC32424);
                sound_lightSaberOn.start();
//                sound_lightSaberIdle.setLooping(true);
//                sound_lightSaberIdle.start();
            } else {
                isLightSaberOn = false;
                canvas.setBackgroundColor(0xFFFFFFFF);
                sound_lightSaberOn.start();
//                sound_lightSaberIdle.setLooping(false);
//                sound_lightSaberIdle.stop();
            }
        }
    }

    public void lightSaberSwing(){
        long difference = System.currentTimeMillis() - prevTimeSwing;

        if(isLightSaberOn) {
            if (difference > 500) {
                prevTimeSwing = System.currentTimeMillis();
                sound_lightSaberSwing.start();
            }
        }
    }


}
