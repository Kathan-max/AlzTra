package com.example.finalversion3;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class EditPatient extends AppCompatActivity {

    TextView patientID;
    EditText patientname;
    EditText patientage;
    ImageView profilePp;
    Button save;
    Button editProfile;
    Button gallaryBtn;
    Button cameraBtn;
    StorageReference storageReference;
    Task<Void> DBref;
    Uri pickedImgUri;
    String id;
    ActivityResultLauncher<String> launcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient);

        patientID = findViewById(R.id.defpatID);
        patientname = findViewById(R.id.editpatientname);
        patientage = findViewById(R.id.editpatientage);
        save = findViewById(R.id.savebutton);
        profilePp = findViewById(R.id.profilepic);
        editProfile = findViewById(R.id.editprofilepic);
        gallaryBtn = findViewById(R.id.gallarybtn);
        cameraBtn = findViewById(R.id.camerabtn);
        // Setting the initial values of the patients.

        String PID = getIntent().getStringExtra("PatitntId");
        id = PID;
        patientID.setText(PID);


        // For the info
        DatabaseReference DBref2 = FirebaseDatabase.getInstance().getReference().child("Patients").child(PID);
        DBref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try {
                        PatientDetails pat = dataSnapshot.getValue(PatientDetails.class);
                        Imageuri imageuri = dataSnapshot.getValue(Imageuri.class);
                        Picasso.with(getApplicationContext()).load(imageuri.Image_URL).into(profilePp);
                        patientname.setText(pat.Name);
                        patientage.setText(Integer.toString(pat.Age));
                    }catch (Exception e){}
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if(uri == null){
                    startActivity(new Intent(getApplicationContext(),PatientProfile_det.class));
                    finish();
                }else {
                    pickedImgUri = uri;
                    profilePp.setImageURI(pickedImgUri);
                    StorageReference reference = FirebaseStorage.getInstance().getReference().child("Patients").child(getIntent().getStringExtra("PatitntId"));
                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(EditPatient.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    DBref = FirebaseDatabase.getInstance().getReference().child("Patients").child(getIntent().getStringExtra("PatitntId")).child("Image_URL").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(EditPatient.this, "Image Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String Pname = patientname.getText().toString();
//                String Page = patientage.getText().toString();

                String Pname = patientname.getText().toString();
                String Page = patientage.getText().toString();
                DBref2.child("Age").setValue(Double.parseDouble(Page));
                DBref2.child("Name").setValue(Pname);
                Toast.makeText(EditPatient.this, "Data updated", Toast.LENGTH_SHORT).show();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                cameraBtn.setVisibility(View.VISIBLE);
                gallaryBtn.setVisibility(View.VISIBLE);
            }
        });

        gallaryBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=22){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        openGallary();
                    }
                }else{
                    openGallary();
                }
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=22){
                    if(!checkCameraPermission()){
                        requestCameraPermsission();
                    }else{
                        openCamera();
                    }
                }else{
                    openCamera();
                }
            }
        });


    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,102);
    }

    private void openGallary() {
        launcher.launch("image/*");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==102){
            if(data==null){
                startActivity(new Intent(getApplicationContext(),PatientProfile_det.class));
                finish();
            }else{
                uploadImg(data);
            }
        }
    }

    private void uploadImg(Intent data) {
        Bitmap proimgans = (Bitmap) data.getExtras().get("data");
        Uri proimg = getImageUri(getApplicationContext(),proimgans);
        profilePp.setImageURI(proimg);
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("Patients").child(getIntent().getStringExtra("PatitntId"));
        reference.putFile(proimg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(EditPatient.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        DBref = FirebaseDatabase.getInstance().getReference().child("Patients").child(getIntent().getStringExtra("PatitntId")).child("Image_URL").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditPatient.this, "Image Updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }




    // Permission for the Storage and the Camera.
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},100);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermsission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

    }

    private boolean checkStoragePermission() {
        boolean res2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        boolean res3 = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        return res2 && res3;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        boolean res3 = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        return res1 && res2 && res3;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onBackPressed() {
        Dashboard.dontdofirsttime();
        Dashboard.activityResumed();
        startActivity(new Intent(getBaseContext(),PatientProfile_det.class));
        finish();
//        super.onBackPressed();
    }
}