package com.alejandros.npitoolkit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoiceCompass extends AppCompatActivity {
    private static final int VOICE_RECOGNITION = 0;
    public final static String EXTRA_MESSAGE = "com.alejandros.npitoolkit.VoiceCompass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_compass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public  boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void onClick(View view){
        if(isConnected()){
            Intent recognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // Needed for obtain the string
            recognizer.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    "Say a cardinal direction and an error margin\nDiga un punto cardinal y un margen de error");
            startActivityForResult(recognizer, VOICE_RECOGNITION);
        }
        else{
            Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // Check recognition voice Intent
        if (requestCode == VOICE_RECOGNITION && resultCode == RESULT_OK){
            // Results of the recognition order by the successfull trust
            ArrayList<String> recognitionResult = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String message = recognitionResult.get(0).toString().toLowerCase();

            Toast.makeText(this, "Recognized text: " + message, Toast.LENGTH_LONG).show();

            if(checkMessage(message)){
                Intent intent = new Intent(this, Compass.class);
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }else {
                Toast.makeText(this, "Have to say something like: " + "north 10 or norte 10", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Check that the message looks like: "<direction> <error>"
     */
    protected boolean checkMessage(String message){
        List<String> myList = Arrays.asList("north","south","east","west","norte","sur","este","oeste");
        boolean validMessage;
        for (String point : myList) {
            if (message.contains(point)) {
                return true;
            }
        }
        return false;
    }
}
