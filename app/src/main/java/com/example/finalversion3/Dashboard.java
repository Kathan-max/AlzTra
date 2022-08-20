package com.example.finalversion3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

//import com.espressif.esptouch.android.v1.EspTouchActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class Dashboard extends AppCompatActivity implements LifecycleObserver {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView userNameDisp;
    CardView trackbtn;
    CardView addpatient;
    CardView PatientProfile;
    CardView removeP;
    ActionBarDrawerToggle toggle;
    DatabaseReference dumyRefernce;
    MenuItem profileBtn;
    DatabaseReference Dref;
    int indicate;
    MenuItem settingsBtn;
    HashMap<String,String> FarFi = new HashMap<String,String>();
    HashMap<Integer, String> Patients = new HashMap<Integer, String>();
    Vector<String> adminEmails = new Vector<String>();
    int nameLength;
    MenuItem Logout;
    FirebaseAuth firebaseAuth;
    String username;
    Integer checkforadmin;
    String finalUsername;
    private AlertDialog mResultDialog;
    private MediaPlayer mediaPlayer;
    static boolean activityVisible;
    static boolean startFirstime = true;


//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    public void initializeCamera() {
//        Dashboard.activityResumed();
//    }

    // Method for Back pressed.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

//    @Override
//    protected void onStop() {
//        Dashboard.activityPaused();
//        super.onStop();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        checkforadmin = 0;
        // Hooking all the buttons and views.
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        trackbtn = findViewById(R.id.trackPatient);
        addpatient = findViewById(R.id.addPatient);
        PatientProfile = findViewById(R.id.PatientProfile);
        removeP = findViewById(R.id.removePatient);
        profileBtn = findViewById(R.id.profile);
        settingsBtn = findViewById(R.id.settings);
        Logout = findViewById(R.id.logout);
        userNameDisp = findViewById(R.id.userNameDashB);
        mediaPlayer = MediaPlayer.create(Dashboard.this,R.raw.notification_calm);


//        Dashboard.activityResumed();

//        activityVisible = getIntent().getExtras("activiyVisible");
        DatabaseReference Patientsrefe = FirebaseDatabase.getInstance().getReference().child("Patients");
        Patientsrefe.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    for(DataSnapshot s: snapshot.getChildren()){
                        PatientDetails patobj = s.getValue(PatientDetails.class);
                        Patients.put(Integer.parseInt(s.getKey().toString()),patobj.Name);
                    }
                }catch (Exception e){}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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
                            if(obj.AdminEmail.equals(firebaseAuth.getCurrentUser().getEmail())){
                                checkforadmin=1;
                                break;
                            }
                        }
                    }catch (Exception e){
                        Toast.makeText(Dashboard.this, "Error in getting the Data", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){}
        });
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        trackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dashboard.activityPaused();
                startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                finish();
            }
        });


        if(adminEmails.contains(firebaseUser.getEmail())){
            Toast.makeText(this, "Admin detected", Toast.LENGTH_SHORT).show();
            checkforadmin = 1;
        }

        addpatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(checkforadmin==1){
                    Dashboard.activityPaused();
                    startActivity(new Intent(getApplicationContext(),AddPatient.class));
                    finish();
                }else{
                    Toast.makeText(Dashboard.this, "You are not authorized", Toast.LENGTH_SHORT).show();
                }
            }
        });


        removeP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkforadmin==1){
                    Dashboard.activityPaused();
                    startActivity(new Intent(getApplicationContext(),RemovePatient.class));
                    finish();
                }else{
                    Toast.makeText(Dashboard.this, "You are not authorized", Toast.LENGTH_SHORT).show();
                }
            }
        });
        PatientProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dashboard.activityPaused();
                startActivity(new Intent(getApplicationContext(),PatientProfile_det.class));
                finish();
            }
        });





        // Code to open and close toolbar
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigration_open,R.string.navigration_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        // On click methods for Dashboard

        // User name display in tools bar
        String username = firebaseAuth.getCurrentUser().getEmail().toString();
        finalUsername = "";
        int i = 0;
        while (username.charAt(i)!='@'){
            if(username.charAt(i)!='.'){
                finalUsername = finalUsername + username.charAt(i);
            }
            i++;
        }
//        userNameDisp.setText(finalUsername);
        // Getting the Actual User name of the User;

        DatabaseReference DBuserName = FirebaseDatabase.getInstance().getReference().child("Hosts").child(finalUsername);
        DBuserName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try {
                        AddHost host = snapshot.getValue(AddHost.class);
                        userNameDisp.setText(host.Hname);
//                        userNameDisp.setTextSize(f);

                    }catch (Exception e){}
                }else {
                    Toast.makeText(Dashboard.this, "Error in fetching UserName!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile:
//                        Toast.makeText(Dashboard.this,"Profile selected",Toast.LENGTH_SHORT).show();
                        Dashboard.activityPaused();
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        finish();
                        break;
                    case R.id.settings:
//                        Toast.makeText(Dashboard.this,"Settings selected",Toast.LENGTH_SHORT).show();
                        Dashboard.activityPaused();
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        finish();
                        break;

                    case R.id.esptouch:
                        Intent i = new Intent(getPackageManager().getLaunchIntentForPackage("com.espressif.esptouch.android"));
                        Dashboard.activityPaused();
                        i.setAction(Intent.ACTION_SEND);
                        i.putExtra(Intent.EXTRA_TEXT, "alztra");
                        i.setType("text/plain");
                        startActivity(i);
                        finish();
//                        startActivity(new Intent(getPackageManager().getLaunchIntentForPackage("com.espressif.esptouch.android")));
//                        Intent i = new Intent(getPackageManager().getLaunchIntentForPackage("com.espressif.esptouch.android"));
//                        if(i.resolveActivity(getPackageManager())!=null){
//                            startActivity(new Intent(getPackageManager().getLaunchIntentForPackage("com.espressif.esptouch.android")));
//                            finish();
//                        }
//                        else{
//                            Toast.makeText(Dashboard.this, "ESP Touch is not install in your phone, Please Install ESP Touch", Toast.LENGTH_SHORT).show();
//                        }
                        break;

                    case R.id.logout:
                        firebaseAuth.signOut();
                        Dashboard.activityPaused();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        break;

                    case R.id.help:
                        Dashboard.activityPaused();
                        startActivity(new Intent(getApplicationContext(), Helpcls.class));
                        finish();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        // For the First time read -->

//        DatabaseReference outofrange = FirebaseDatabase.getInstance().getReference().child("FarFi");
//        DatabaseReference firsttimeread = FirebaseDatabase.getInstance().getReference().child("FarFi");
//        firsttimeread.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                try{
//                    for(DataSnapshot s: snapshot.getChildren()){
//                        try {
//                            if (snapshot.getKey().toString() != "ID") {
//                                DialogCaller.showDialog(Dashboard.this, "This student is out side the Wifi Range", "ID: " + snapshot.getKey().toString() + ",\nName: " + Patients.get(Integer.parseInt(snapshot.getKey().toString())), new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dumyRefernce = FirebaseDatabase.getInstance().getReference().child("FarFi").child(snapshot.getKey().toString());
//                                        dumyRefernce.removeValue();
//                                    }
//                                });
//                                mediaPlayer.start();
//
//                            }
//                        }catch (Exception e){}
//                    }
//                }catch (Exception e){}
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });

        try {
            if (true) {
                DatabaseReference outofrange = FirebaseDatabase.getInstance().getReference().child("FarFi");
                outofrange.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(snapshot.exists()){
                            try {
                                if (snapshot.getKey().toString() != "ID") {
                                    DialogCaller.showDialog(Dashboard.this, "This student is out side the Wifi Range",
                                            "ID: " + snapshot.getKey().toString() + ",\nName: " + Patients.get(Integer.parseInt(snapshot.getKey().toString())),
                                            new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dumyRefernce = FirebaseDatabase.getInstance().getReference().child("FarFi").child(snapshot.getKey().toString());
                                            dumyRefernce.removeValue();
                                        }
                                    });
                                    mediaPlayer.start();

                                }
                            }catch (Exception e){}
                        }
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });



            }
//            else if(startFirstTime()){
//                outofrange.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try{
//                            for(DataSnapshot s: snapshot.getChildren()){
//                                try {
//                                    if (snapshot.getKey().toString() != "ID") {
//                                        DialogCaller.showDialog(Dashboard.this, "This student is out side the Wifi Range", "ID: " + snapshot.getKey().toString() + ",\nName: " + Patients.get(Integer.parseInt(snapshot.getKey().toString())), new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dumyRefernce = FirebaseDatabase.getInstance().getReference().child("FarFi").child(snapshot.getKey().toString());
//                                                dumyRefernce.removeValue();
//                                            }
//                                        });
//                                        mediaPlayer.start();
//
//                                    }
//                                }catch (Exception e){}
//                            }
//                        }catch (Exception e){}
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {}
//                });
//            }
        }catch (Exception e){}





    }

//    public void popupchild(HashMap<String,String> FarFi){
//        DatabaseReference dispoutrangestudent = FirebaseDatabase.getInstance().getReference().child("Patients");
//        dispoutrangestudent.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for( DataSnapshot s:dataSnapshot.getChildren()) {
//                    PatientDetails Patobj = s.getValue(PatientDetails.class);
//                    if (FarFi.containsKey(Integer.toString(Patobj.id)) && FarFi.get(Integer.toString(Patobj.id))=="1") {
//                        FarFi.put(s.getKey().toString(),"0");
//                        DialogCaller.showDialog(Dashboard.this, "This student is out side the Wifi Range", "ID: " + Integer.toString(Patobj.id) + ",\nName: " + Patobj.Name, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dumyRefernce = FirebaseDatabase.getInstance().getReference().child("FarFi").child(Integer.toString(Patobj.id));
//                                dumyRefernce.removeValue();
//                            }
//                        });
//                        mediaPlayer.start();
//                        return;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



//    }


    public static boolean startFirstTime(){
        return startFirstime;
    }

    public static void dofirstTime(){
        startFirstime = true;
    }
    public static void dontdofirsttime(){
        startFirstime = false;
    }


    // For child;
    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }
}