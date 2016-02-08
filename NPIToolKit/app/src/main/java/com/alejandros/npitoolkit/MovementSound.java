package com.alejandros.npitoolkit;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.alejandros.npitoolkit.AcelerometroData;

public class MovementSound extends AppCompatActivity {
    private AcelerometroData acelerometro;

    SoundPool sp;
    int soundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_sound);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Instaciamos la clase AcelerometroData que se encargará de tomar el dato del sensor
        acelerometro = new AcelerometroData((SensorManager) getSystemService(SENSOR_SERVICE), this);

        // http://stackoverflow.com/a/17070454/3248221
        SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        /** soundId for Later handling of sound pool **/
        soundId = sp.load(this, R.raw.light_saber_on, 1); // in 2nd param u have to pass your desire ringtone
    }

    protected void onResume() {
        super.onResume();
        acelerometro.onResume();
    }

    protected void onPause() {
        super.onPause();
        acelerometro.onPause();
    }

    // Reproduce el sonido y realiza la animación de agitar el icono del móvil
    // Se llama desde AcelerometroData cuando la aceleración lineal
    // en el eje X supera un mínimo.
    public void reproducirSonidoYAnimacion(){
        sp.play(soundId, 1, 1, 0, 0, 1);
    }

}
