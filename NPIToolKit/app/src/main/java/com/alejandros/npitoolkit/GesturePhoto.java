package com.alejandros.npitoolkit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import haibison.android.lockpattern.LockPatternActivity;
import haibison.android.lockpattern.utils.AlpSettings;

public class GesturePhoto extends AppCompatActivity{

    // App flags
    private static final int REQ_CREATE_PATTERN = 1;
    private static final int REQ_ENTER_PATTERN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // haibison.android.lockpattern.utils
        // Save the pattern that the user introduces
        AlpSettings.Security.setAutoSavePattern(this, true);

        // Button that ables the user to create his own pattern
        Button creationPatternButton = (Button) findViewById(R.id.createPattern);
        creationPatternButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LockPatternActivity.IntentBuilder
                        .newPatternCreator(GesturePhoto.this)
                        .startForResult(GesturePhoto.this, REQ_CREATE_PATTERN);
            }
        });

        // Button that waits to the previously defined pattern
        Button insertPatternButton = (Button) findViewById(R.id.insertPattern);
        insertPatternButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LockPatternActivity.IntentBuilder
                        .newPatternComparator(GesturePhoto.this)
                        .startForResult(GesturePhoto.this, REQ_ENTER_PATTERN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case REQ_CREATE_PATTERN: {
                if (resultCode == RESULT_OK) {
                    char[] pattern = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
                    Toast.makeText(this, getString(R.string.photo_pattern) + pattern, Toast.LENGTH_LONG).show();
                }
                break;
            }// REQ_CREATE_PATTERN
            case REQ_ENTER_PATTERN: {
                // NOTE that there are 4 possible result codes!!!
                switch(resultCode){
                    case RESULT_OK:
                        // The user passed
                        Toast.makeText(this, R.string.photo_RES_OK, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, CameraActivity.class);
                        startActivity(intent);
                        break;
                    case RESULT_CANCELED:
                        // The user cancelled the task
                        Toast.makeText(this, R.string.photo_RES_CANCEL, Toast.LENGTH_LONG).show();
                        break;
                    case LockPatternActivity.RESULT_FAILED:
                        // The user failed to enter the pattern
                        Toast.makeText(this, R.string.photo_RES_FAILED, Toast.LENGTH_LONG).show();
                        break;
                    case LockPatternActivity.RESULT_FORGOT_PATTERN:
                        // The user forgot the pattern and invoked your recovery Activity
                        Toast.makeText(this, R.string.photo_RES_FORGOT, Toast.LENGTH_LONG).show();
                        break;
                }

                // In any case, there's always a key EXTRA_RETRY_COUNT, which holds the number
                // of tries that the user did
                int retryCount = data.getIntExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 0);

                break;
            }// REQ_ENTER_PATTERN
        }
    }
}
