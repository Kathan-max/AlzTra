// To do
// readChange(); --> to detect the realtime change in the location
// error --> Can't convert object of type com.example.finalversion3.PatientDetails

package com.example.finalversion3;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest.permission;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.finalversion3.databinding.ActivityMapsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private DatabaseReference reference;
    private DatabaseReference refeChild;
    private DatabaseReference circref;
    private LocationManager manager;
    DatabaseReference dumyRefernce;
    private LocationManager locationManager;
    private int patientID;
    private MediaPlayer mediaPlayer;
//    private int beepchecker13;
    private int[] beepArray = new int[50];
    HashMap<String,String> FarFi = new HashMap<String,String>();
    private Marker centerMarker;
    private Circle circle;
    private FirebaseAuth fAuth;
    private boolean permissionDenied;
    Marker m;
    private Marker[] markers = new Marker[50];
    private DatabaseReference[] refeArray = new DatabaseReference[1000];
    //    private Map<String,Marker> markerMap = Collection.emptyMap();
    private ChildEventListener mChildEventListner;
    private Vibrator vibe;
    private LatLng defPos;
    private int defrad;
    double dist;

    Marker marker;
    private final int MIN_TIME = 1000; // 1 sec
    private final int MIN_DISTANCE = 1; // 1 meter
//    private final double defLat = 23.03718;
//    private final double defLog = 72.5528;
    private double defLat = 23.0385;
    private double defLog = 72.5537;

//    23.03718,72.5528


    // Method to go back to the Dashboard.
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Dashboard.activityResumed();
        Dashboard.dontdofirsttime();
        startActivity(new Intent(getApplicationContext(), Dashboard.class));
        finish();
    }



    // Marker move method

    public boolean onMarkerMoved(Marker marker){
        if(marker.equals(centerMarker)){
            circle.setCenter(marker.getPosition());
            return true;
        }
        return false;
    }
    // Asking for permissions


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        reference = FirebaseDatabase.getInstance().getReference().child("Patients");
//        refeChild = FirebaseDatabase.getInstance().getReference().child("Patients").child("12");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        getLocationUpdates();

        readChanges();

        for(int i=0;i<beepArray.length;i++){
            beepArray[i] = 0;
        }
        permissionDenied = false;
        mediaPlayer = MediaPlayer.create(MapsActivity.this,R.raw.beep);



        // Alert code.............

//        DatabaseReference outofrange = FirebaseDatabase.getInstance().getReference().child("FarFi");
//
//        outofrange.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot s : dataSnapshot.getChildren()) {
//                    FarFi.put(s.getKey().toString(), "1");
//                    popupchild(FarFi);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//        getLocationUpdates();
    }


    // START of User location code..................
    private void showAlertMessageLocationDisabled() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Device Location is Turned off, Turn on the device location, Do you want to turn on Location?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            if(grantResults.length==1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLocation();
            }else{
                Toast.makeText(MapsActivity.this,"Location is Required please Enable Location from settings",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }else{
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{permission.ACCESS_FINE_LOCATION},101);
    }



    // END of Location.......


    // Alert code



//    private void getLocationUpdates() {
//        if(manager.isProviderEnabled()){
//            manager.requestLocationUpdates();
//        }
//    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    private double measureDist(double lat1,double log1){
        double R = 6378.137;
        double dLat = lat1*Math.PI/180 - (defLat)*Math.PI/180;
        double dLon = log1*Math.PI/180 - (defLog)*Math.PI/180;
        double a = Math.sin(dLat/2)*Math.sin(dLat/2) + Math.cos(lat1*Math.PI/180)*Math.cos(defLat*Math.PI/180)* Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
        double d = R*c;
        return d*1000;
    }
    private boolean askViberatePermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE}, 101);
            return true;
        }
    }



//    private void readChild(String id){
//        DatabaseReference refeChild = FirebaseDatabase.getInstance().getReference().child("Patients");
//    }


    private void readChanges() {
        // Vibrator method


        // Parent references

        // reference to get the id of the changed patient.

        DatabaseReference circrefe2 = FirebaseDatabase.getInstance().getReference().child("Circle");
        circrefe2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try {
                        CircleDetails cirobj = dataSnapshot.getValue(CircleDetails.class);
                        defPos = new LatLng(Double.parseDouble(cirobj.latitude),Double.parseDouble(cirobj.longitude));
                        circle.setCenter(defPos);
                        defLat = defPos.latitude;
                        defLog = defPos.longitude;
                        defrad = cirobj.Radius;
                        circle.setRadius(defrad);
                        centerMarker.setPosition(defPos);
                    }
                    catch (Exception e){}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        //
        refeChild = FirebaseDatabase.getInstance().getReference("Patients");
        refeChild.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                if(dataSnapshot.exists()){
                    try {
                        String id = dataSnapshot.getKey();
                        readChanges2(id);
                    }catch (Exception e){
                        Toast.makeText(MapsActivity.this, "Error in fetching data", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MapsActivity.this, "No Data exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    // Alert




    // Generalized Read Changes for all the children
    public void readChanges2(String id){
        DatabaseReference childref = FirebaseDatabase.getInstance().getReference().child("Patients").child(id);
        childref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    try {
                        PatientDetails patLoc = dataSnapshot.getValue(PatientDetails.class);
                        double lat = Double.parseDouble(patLoc.latitude);
                        double log = Double.parseDouble(patLoc.longitude);
                        if(lat!=0 || log!=0) {
                            dist = measureDist(lat, log);
                            LatLng location = new LatLng(Double.parseDouble(patLoc.latitude), Double.parseDouble(patLoc.longitude));
                            if (patLoc != null) {
                                if (markers[patLoc.id] != null) {
                                    markers[patLoc.id].setPosition(new LatLng(lat, log));
                                }
                            }
//
//
                            if (askViberatePermission()) {
                                if (defrad > 0) {
                                    if (dist > defrad) {
                                        if (Build.VERSION.SDK_INT >= 26) {
//                                    refeChild13.child("num_b").setValue(1);
                                            refeChild.child(Integer.toString(patLoc.id)).child("num_b").setValue(1);
                                            if (markers[patLoc.id] != null) {
                                                markers[patLoc.id].setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                            }
                                            if (beepArray[patLoc.id] != 1) {
                                                mediaPlayer.start();
//                                        beepchecker13 = 1;
                                                beepArray[patLoc.id] = 1;
                                            }
                                        }
                                    } else {
//                                refeChild13.child("num_b").setValue(0);
                                        refeChild.child(Integer.toString(patLoc.id)).child("num_b").setValue(0);
//                                mediaPlayer.stop();
                                        if (markers[patLoc.id] != null) {
                                            markers[patLoc.id].setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                        }
                                        beepArray[patLoc.id] = 0;
                                    }
                                }

                            }
                        }
                        return;

                    } catch (Exception e) {
                        Toast.makeText(MapsActivity.this, "Error in fetching data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(MapsActivity.this, "No data Exists", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;
            }
        });
    }



    // onMapReady
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)){
            getLocation();
        }else{
            showAlertMessageLocationDisabled();
        }

        googleMap.setOnMarkerClickListener(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);





        circref = FirebaseDatabase.getInstance().getReference().child("Circle");
        circref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try {
                        CircleDetails cirobj = dataSnapshot.getValue(CircleDetails.class);
                        defPos = new LatLng(Double.parseDouble(cirobj.latitude),Double.parseDouble(cirobj.longitude));
                        defrad = cirobj.Radius;
                    }catch (Exception e){}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });



        // Circle Details...
        centerMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(defLat,defLog)).draggable(true).title("Circle Center"));

        centerMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        circle = mMap.addCircle(new CircleOptions().center(new LatLng(defLat,defLog)).radius(50).strokeColor(Color.RED));

        if(defPos!= null && defrad!=0){
            centerMarker = mMap.addMarker(new MarkerOptions().position(defPos).draggable(true).title("Circle Center"));

            centerMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

            circle = mMap.addCircle(new CircleOptions().center(defPos).radius(defrad).strokeColor(Color.RED));
        }



        // For dragging the circle

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
                marker = centerMarker;
                LatLng pos = marker.getPosition();
                defPos = pos;
                circref.child("latitude").setValue(Double.toString(pos.latitude));
                circref.child("longitude").setValue(Double.toString(pos.longitude));
                circle.setCenter(pos);
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {

            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {

            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(marker.getPosition()).zoom(19).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                marker.showInfoWindow();
                return true;
            }

        });

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for( DataSnapshot s:dataSnapshot.getChildren()){
                    PatientDetails patient = s.getValue(PatientDetails.class);
                    numInt num_b = s.getValue(numInt.class);
                    double lat = Double.parseDouble(patient.latitude);
                    double log = Double.parseDouble(patient.longitude);
                    LatLng location = new LatLng(lat,log);
                    double dist2 = measureDist(lat,log);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));


                    fAuth = FirebaseAuth.getInstance();
                    markers[patient.id] = mMap.addMarker(new MarkerOptions().position(location).title(patient.Name));
                    if(defrad>=0) {
                        if (dist2 < defrad) {
                            markers[patient.id].setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        } else {
                            markers[patient.id].setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            mediaPlayer.start();
                        }
                    }
//                    mMap.addMarker(new MarkerOptions().position(location).title(patient.Name)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
//        mMap.setMinZoomPreference(120000);
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }










    @Override
    public void onLocationChanged(Location location) {}

    private void saveLoaction(Location location){
//        reference.setValue(location);
    }
//
    @Override
    public void onStatusChanged(String provider,int status,Bundle extras){

    }

    @Override
    public void onProviderEnabled(String provider){

    }
    @Override
    public void onProviderDisabled(String provider){

    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
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
//                        DialogCaller.showDialog(MapsActivity.this, "This student is out side the Wifi Range", "ID: " + Integer.toString(Patobj.id) + ",\nName: " + Patobj.Name, new DialogInterface.OnClickListener() {
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
//
//
//
//    }
}