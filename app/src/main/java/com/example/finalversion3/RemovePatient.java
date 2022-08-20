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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Vector;

public class RemovePatient extends AppCompatActivity {

    // Back Pressed Method


    @Override
    public void onBackPressed() {
        Dashboard.activityResumed();
        Dashboard.dontdofirsttime();
        startActivity(new Intent(getApplicationContext(),Dashboard.class));
        finish();
    }

    EditText patientID;
    Button remvBtn;
    DatabaseReference remove_reference;
    Vector<String> adminEmails = new Vector<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_patient);

        remvBtn = findViewById(R.id.RemoveBTN);
        patientID = findViewById(R.id.PatientID);

        DatabaseReference DBref = FirebaseDatabase.getInstance().getReference().child("Admin");
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userEmail = fAuth.getCurrentUser().getEmail();

        remvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    try {
                        Integer.parseInt(patientID.getText().toString());
                        remove_reference = FirebaseDatabase.getInstance().getReference().child("Patients").child(patientID.getText().toString());
                        remove_reference.removeValue();
                        StorageReference remove_reference_storage;
                        remove_reference_storage = FirebaseStorage.getInstance().getReference().child("Patients").child(patientID.getText().toString());
                        remove_reference_storage.delete();
                        Toast.makeText(RemovePatient.this, "Patient was removed successfully", Toast.LENGTH_SHORT).show();
                        patientID.setText("");
                    }catch (NumberFormatException e){
                        Toast.makeText(RemovePatient.this, "Please Enter a valid ID", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }
}