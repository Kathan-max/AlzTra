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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

public class AddPatient extends AppCompatActivity {

    // Method for going back top the dashboard.
    @Override
    public void onBackPressed() {
        Dashboard.activityResumed();
        Dashboard.dontdofirsttime();
        startActivity(new Intent(getBaseContext(),Dashboard.class));
        finish();
//        super.onBackPressed();
    }
    ImageView Profile1;
    EditText name;
    EditText id;
    Uri Profile_Image = null;
    EditText age;
    TextView addphototext;
    Button addpatient;
    Button gallarybtn1;
    Button camerabtn1;
    String Image_URL;
    ActivityResultLauncher<String> launcher;
    private Vector<Integer> aldyexisID = new Vector<Integer>();
    private Vector<String> adminEmails = new Vector<String>();



    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    DatabaseReference DBaddpatient;

    Task<Void> DBref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        name = findViewById(R.id.addpatientname);
        id = findViewById(R.id.addpatientID);
        age = findViewById(R.id.addpatientage);
        addpatient = findViewById(R.id.addbutton);
        gallarybtn1 = findViewById(R.id.gallary);
        camerabtn1 = findViewById(R.id.camera);
        addphototext = findViewById(R.id.addphototext);


        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if(uri==null){
                    startActivity(new Intent(getApplicationContext(),Dashboard.class));
                    finish();
                }else{
                    Profile_Image = uri;
                    Toast.makeText(AddPatient.this, "Image Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });


        DBaddpatient = FirebaseDatabase.getInstance().getReference
                ().child("Patients");
        DBaddpatient.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.exists()){
                     try{
                         for(DataSnapshot s: snapshot.getChildren()){
                             aldyexisID.add(Integer.parseInt(s.getKey().toString()));
                         }
                     }catch (Exception e){}
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference AdminUsers = FirebaseDatabase.getInstance().getReference().child("Admin");
        AdminUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try{
                        for(DataSnapshot s:dataSnapshot.getChildren()){
                            Admin obj = s.getValue(Admin.class);
                            adminEmails.add(obj.AdminEmail);
                        }
                    }catch (Exception e){}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){}
        });

        addpatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
                String userEmail = currentuser.getEmail();
                try {
                    if (adminEmails.contains(userEmail) && (id.getText().toString() != "") && (age.getText().toString() != "") && (name.getText().toString() != "")) {


                        String tname = name.getText().toString();
                        String tage = age.getText().toString();
                        int tage2 = Integer.parseInt(tage);
                        String tid = id.getText().toString();
                        int tid2 = Integer.parseInt(tid);
                        String latitude = "0";
                        String longitude = "0";


                        PatientDetails patient = new PatientDetails(tid2, tage2, tname, userEmail, latitude, longitude, 1);

                        if (!aldyexisID.contains(Integer.parseInt(tid)) && tid != null) {
                            DBaddpatient.child(tid).setValue(patient)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
//                                Profile1.setImageURI(Profile_Image);
                                            StorageReference reference = FirebaseStorage.getInstance().getReference().child("Patients").child(tid);
                                            reference.putFile(Profile_Image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(EditPatient.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
//                                Image_URL = uri.toString();
//                                                DBaddpatient.child(tid).child("Image_URL").setValue(uri.toString());
                                                            DBref = FirebaseDatabase.getInstance().getReference().child("Patients").child(tid).child("Image_URL").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(AddPatient.this, "Data inserted", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                        id.setText("");
                        name.setText("");
                        age.setText("");
                        } else {
                            Toast.makeText(AddPatient.this, "ID already exists please add Unique the ID written on the Tracker or Please Select Photo properly", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(AddPatient.this, "Please Provide Patient Details", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Toast.makeText(AddPatient.this, "Please Provide Valid Patient Details", Toast.LENGTH_SHORT).show();
                }



            }
        });








        gallarybtn1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=22){
                    if(!checkStoragePermission1()){
                        requestStoragePermission1();
                    }else{
                        if(id.getText().toString()!=null) {
                            openGallary1();
                        }
                        else{
                            Toast.makeText(AddPatient.this, "Please Provide the ID first", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    if(id.getText().toString()!=null){
                        openCamera1();
                    }else{
                        Toast.makeText(AddPatient.this, "Please provide the ID first", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        camerabtn1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=22){
                    if(!checkCameraPermission1()){
                        requestCameraPermsission1();
                    }else{
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if(id.getText().toString()!=null){
                            openCamera1();
                        }else{
                            Toast.makeText(AddPatient.this, "Please provide the ID first", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(id.getText().toString()!=null){
                        openCamera1();
                    }else{
                        Toast.makeText(AddPatient.this, "Please provide the ID first", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void openCamera1() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,103);
    }

    private void openGallary1() {
        launcher.launch("image/*");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 103){
            if(data==null){
                startActivity(new Intent(getApplicationContext(),Dashboard.class));
                finish();
            }
            else{
                uploadImg1(data);
            }
        }
    }

    private void uploadImg1(Intent data) {
        Bitmap proimgans = (Bitmap) data.getExtras().get("data");
        Uri proimg = getImageUri1(getApplicationContext(),proimgans);
        Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
        Profile_Image = proimg;
    }




    // Permission for the Storage and the Camera.
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission1() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},100);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermsission1() {
        requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

    }

    private boolean checkStoragePermission1() {
        boolean res2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        boolean res3 = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        return res2 && res3;
    }

    private boolean checkCameraPermission1() {
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        boolean res3 = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        return res1 && res2 && res3;
    }

    public Uri getImageUri1(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

//    public boolean checkInt(String s){
//        try{
//            Integer.parseInt(s);
//            return true;
//        }catch (Exception e){
//            return false;
//        }
//    }
}