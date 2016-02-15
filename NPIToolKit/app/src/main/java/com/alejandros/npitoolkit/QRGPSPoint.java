package com.alejandros.npitoolkit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRGPSPoint extends AppCompatActivity implements View.OnClickListener {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    private TextView formatTxt, contentTxt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgpspoint);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Nos hace volver atrás

        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);
    }

    //respond to clicks
    public void onClick(View view){
        // claim to zxing app that scan the Qr code
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null){
            //we have a result and change the TextView with it
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            formatTxt.setText(getString(R.string.QR_format) + scanFormat);
            contentTxt.setText(getString(R.string.QR_content) + scanContent);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.QR_errorScan, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
