package com.example.finalversion3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class PatientProfile_det extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Dashboard.activityResumed();
        Dashboard.dontdofirsttime();
        startActivity(new Intent(getApplicationContext(),Dashboard.class));
        finish();
    }

    TextView editId;
    TextView PatientName;
    TextView PatientAge;
    TextView PatientHost;
    TextView PatNameT;
    TextView PatAgeT;
    TextView PatHostT;
    FirebaseAuth fauth;
    EditText PatientID;
    private Vector<String> adminEmails = new Vector<String>();
    ImageView Profile;
    Button srhBtn;
    Button EditBtn;
    DatabaseReference search_refe;
    DatabaseReference admin_refe;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile_det);
        PatientName = findViewById(R.id.PatientName);
        PatientAge = findViewById(R.id.PatientAge);
        PatientHost = findViewById(R.id.PatHost);
        PatientID = findViewById(R.id.PatID);
        Profile = findViewById(R.id.ProfileIMG);
        EditBtn = findViewById(R.id.EditBtn);
        srhBtn = findViewById(R.id.SearchBTN);
        PatAgeT = findViewById(R.id.age);
        PatHostT = findViewById(R.id.Hostemail);
        PatNameT = findViewById(R.id.textView12);

        fauth = FirebaseAuth.getInstance();
        admin_refe = FirebaseDatabase.getInstance().getReference().child("Admin");
        admin_refe.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot s:dataSnapshot.getChildren()){
                    Admin obj = s.getValue(Admin.class);
                    adminEmails.add(obj.AdminEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        srhBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PatAgeT.setVisibility(View.VISIBLE);
                    PatHostT.setVisibility(View.VISIBLE);
                    PatNameT.setVisibility(View.VISIBLE);
                    Integer.parseInt(PatientID.getText().toString());
                    String id = PatientID.getText().toString();

                    // Getting the The Profile Picture of the ID.






                    search_refe = FirebaseDatabase.getInstance().getReference().child("Patients").child(PatientID.getText().toString());
                    search_refe.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                try {
                                    PatientDetails pat = dataSnapshot.getValue(PatientDetails.class);
                                    Imageuri ImageUri1 = dataSnapshot.getValue(Imageuri.class);
                                    Picasso.with(getApplicationContext()).load(ImageUri1.Image_URL).into(Profile);

                                    PatientName.setText(pat.Name);
                                    PatientName.setVisibility(View.VISIBLE);

                                    PatientAge.setText(Integer.toString(pat.Age));
                                    PatientAge.setVisibility(View.VISIBLE);

                                    PatientHost.setText(pat.host_email);
                                    PatientHost.setVisibility(View.VISIBLE);
                                    String currUser = fauth.getCurrentUser().getEmail();
                                    EditBtn.setVisibility(View.VISIBLE);
                                    if(adminEmails.contains(currUser)){
                                        EditBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getApplicationContext(),EditPatient.class);
                                                String ppid = Integer.toString(pat.id);
                                                intent.putExtra("PatitntId",ppid);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }else{
                                        EditBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(PatientProfile_det.this, "You are not authorized to edit Patient Details", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }


                                }catch (Exception e){}
                            }else{
                                Toast.makeText(PatientProfile_det.this, "User doesn't exists", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }catch (NumberFormatException e){
                    Toast.makeText(PatientProfile_det.this, "Please Enter a valid ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}