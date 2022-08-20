package com.example.finalversion3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    // Method for going back to the Dashboard


    @Override
    public void onBackPressed() {
        Dashboard.activityResumed();
        Dashboard.dontdofirsttime();
        startActivity(new Intent(getBaseContext(),Dashboard.class));
        finish();
//        super.onBackPressed();
    }

    Button editProfilebtn;
    TextView Uname;
    TextView Uemail;
    String username;
    String finalUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        FirebaseAuth fAuth = FirebaseAuth.getInstance();
//        editProfilebtn = findViewById(R.id.profileEditbtn);
        Uname = findViewById(R.id.profileName);
        Uemail = findViewById(R.id.profileEmail);

        username = fAuth.getCurrentUser().getEmail().toString();
        finalUsername = "";
        int i = 0;
        while (username.charAt(i)!='@'){
            if(username.charAt(i)!='.'){
                finalUsername = finalUsername + username.charAt(i);
            }
            i++;
        }
        DatabaseReference Dbref = FirebaseDatabase.getInstance().getReference().child("Hosts").child(finalUsername);
        Dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try {
                        AddHost host = snapshot.getValue(AddHost.class);
                        Uname.setText(host.Hname.toString());
                    }catch (Exception e){
                        Toast.makeText(Profile.this, "Error In fetching user name", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Uemail.setText(fAuth.getCurrentUser().getEmail());


//
//        editProfilebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),EditProfile.class));
//                finish();
//            }
//        });
    }
}