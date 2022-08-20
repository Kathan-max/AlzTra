package com.example.finalversion3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {


    EditText RangeDet;
    TextView currRange;
    Button editrangeBt;
    Button Savebtn;
    DatabaseReference rangeRef;
    Task<Void> settingref;
    int rad2;
    int rad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RangeDet = findViewById(R.id.Updatedrange);
        currRange = findViewById(R.id.curretrange);
        editrangeBt = findViewById(R.id.editRange);
        Savebtn = findViewById(R.id.SaveRange);


        rangeRef = FirebaseDatabase.getInstance().getReference().child("Circle");
        rangeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try{
                        CircleDetails circ = dataSnapshot.getValue(CircleDetails.class);
                        rad = circ.Radius;
                        currRange.setText(Integer.toString(rad));
                    }catch (Exception e){}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        editrangeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RangeDet.setVisibility(View.VISIBLE);
            }
        });

        Savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rad2 = Integer.parseInt(String.valueOf(RangeDet.getText()));
                settingref = FirebaseDatabase.getInstance().getReference().child("Circle").child("Radius").setValue(rad2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Settings.this, "Range Successfully changed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    // Method for going back to the dashboard.
    @Override
    public void onBackPressed() {
        Dashboard.activityResumed();
        Dashboard.dontdofirsttime();
        startActivity(new Intent(getBaseContext(),Dashboard.class));
        finish();
//        super.onBackPressed();
    }
}