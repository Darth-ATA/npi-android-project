package com.alejandros.npitoolkit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class ToolKitMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_kit_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_tool_kit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Differents activities to execute
    public void goToVoiceCompass(View view){
        Intent intent = new Intent(this, VoiceCompass.class);
        startActivity(intent);
    }
    public void goToQrGpsPoint(View view){
        Intent intent = new Intent(this, QRGPSPoint.class);
        startActivity(intent);
    }
    public void goToGesturePhoto(View view){
        Intent intent = new Intent(this, GesturePhoto.class);
        startActivity(intent);
    }
    public void goToMovementSound(View view){
        Intent intent = new Intent(this, MovementSound.class);
        startActivity(intent);
    }
    public void goToSurprise(View view){
        Intent intent = new Intent(this, Surprise.class);
        startActivity(intent);
    }
}
