package com.example.finalversion3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class EditProfile extends AppCompatActivity {

    TextView currPassword;
    TextView newEmail;
    TextView newPassword;

    @Override
    public void onBackPressed() {
        Dashboard.activityResumed();
        Dashboard.dontdofirsttime();
        startActivity(new Intent(getBaseContext(),Dashboard.class));
        finish();
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        currPassword = findViewById(R.id.currPassword);
//        newEmail = findViewById(R.id.new)

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
//        fAuth.signInWithEmailAndPassword(fAuth.getCurrentUser().getEmail(),)

    }


    // Method to go back to the dashboard.

}