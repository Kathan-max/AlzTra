package com.example.finalversion3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Helpcls extends AppCompatActivity {
    TextView devyash;
    TextView dev;
    TextView kathan;
    TextView meet;
    TextView pratham;
    TextView manthan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpcls);

        devyash = (TextView) this.findViewById(R.id.devyash_call);
        dev = findViewById(R.id.dev_call);
        kathan = findViewById(R.id.kathan_call);
        meet = findViewById(R.id.meet_call);
        pratham = findViewById(R.id.lalu_call);
        manthan = findViewById(R.id.man_call);
    }



    // Method to go back to the dashboard

    @Override
    public void onBackPressed() {
        Dashboard.activityResumed();
        startActivity(new Intent(getApplicationContext(),Dashboard.class));
        finish();
//        super.onBackPressed();
    }
}