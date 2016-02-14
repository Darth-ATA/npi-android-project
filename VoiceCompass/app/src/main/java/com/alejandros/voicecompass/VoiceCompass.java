package com.alejandros.voicecompass;

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
    public final static String EXTRA_MESSAGE = "com.alejandros.voicecompass.VoiceCompass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_compass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        // We need internet connection for the RecognizerIntent
        if(isConnected()){
            // Calls the ReconizerIntent activity
            Intent recognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // Will provide the result as string
            recognizer.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.compass_initMessage));
            startActivityForResult(recognizer, VOICE_RECOGNITION);
        }
        else{
            Toast.makeText(getApplicationContext(), getString(R.string.compass_internetError), Toast.LENGTH_LONG).show();
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

            Toast.makeText(this, getString(R.string.text_recognized) + message, Toast.LENGTH_LONG).show();

            int orientation = calculateProvidedOrientation(message);
            int error = calculateErrorMargin(message);
            int[] orientation_error = {orientation, error};

            //Toast.makeText(this,"El error reconocido es:" + Integer.toString(error), Toast.LENGTH_LONG).show();

            if(checkMessage(message)){
                Intent intent = new Intent(this, Compass.class);
                intent.putExtra(EXTRA_MESSAGE, orientation_error);
                startActivity(intent);
            }else {
                Toast.makeText(this, R.string.compass_suggestion, Toast.LENGTH_LONG).show();
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

    // Translate the words in degrees
    protected int calculateProvidedOrientation(String message) {
        if (message.startsWith("north") || message.startsWith("norte")) {
            return 0;
        } else if (message.startsWith("east") || message.startsWith("este")) {
            return 90;
        } else if (message.startsWith("south") || message.startsWith("sur")) {
            return 180;
        } else if (message.startsWith("west") || message.startsWith("oeste")) {
            return 270;
        }
        else {
            return 0;
        }
    }

    // Translate the words in number
    protected int calculateErrorMargin(String message){
        return Integer.parseInt(message.replaceAll("[^0-9]", ""));
    }
}
