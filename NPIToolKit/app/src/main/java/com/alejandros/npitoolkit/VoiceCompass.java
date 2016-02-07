package com.alejandros.npitoolkit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class VoiceCompass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_compass);
    }

    public void onClick(View view){
        Intent intent = new Intent(this, Compass.class);
        startActivity(intent);
    }
}
