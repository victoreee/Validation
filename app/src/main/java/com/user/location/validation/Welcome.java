package com.user.location.validation;

import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Welcome extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference acces;

    Marker mDriverMarker;

    int radios= 1; // kilometros
    int distance = 1; // kilometros
    private static final int LIMIT = 1;

    Button report;

    String driver_on_service_tbl = "Location_base";
    Location mLastLocationUniteOnService = null;
    DatabaseReference driversUniteOnService;
    GeoFire geoFireUniteOnService;
    FusedLocationProviderClient mServiclocationUnite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mServiclocationUnite = LocationServices.getFusedLocationProviderClient(Welcome.this);
        driversUniteOnService= FirebaseDatabase.getInstance().getReference(driver_on_service_tbl /** Common.driver_on_service_tbl */);
        geoFireUniteOnService = new GeoFire(driversUniteOnService);

        report = findViewById(R.id.report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
            }
        });

        loadAllAvailableDriver();
        loadAllAvailableUsers();
        countDownTimer();
    }

    private void countDownTimer() {

        new CountDownTimer(1000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                Toast.makeText(Welcome.this, "actualizado", Toast.LENGTH_LONG).show();

            }
        }.start();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng( 25.7622736, -102.9845281), 14.0f ));
    }

    private void loadAllAvailableDriver() {

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference("Drivers");
        GeoFire gf = new GeoFire(driverLocation);
        GeoQuery geoQuery =gf.queryAtLocation
                (new GeoLocation(25.757950,-102.984002),radios);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {

                FirebaseDatabase.getInstance().getReference("DriversInformation")
                        .child(key)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Users rider = dataSnapshot.getValue(Users.class);

                                mMap.clear();

                                        Toast.makeText(Welcome.this, "actualizado", Toast.LENGTH_LONG);

                                mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(location.latitude, location.longitude))
                                            .flat(true)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                            .title("UNIUDAD " + rider.getNombre()));


                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });


            }
            @Override
            public void onKeyExited(String key) {
            }
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }
            @Override
            public void onGeoQueryReady() {

                countDownTimer();

                loadAllAvailableDriver();
            }
            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });

    }

    private void loadAllAvailableUsers() {


        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference(Common.pickup_request_tbl);
        GeoFire gf = new GeoFire(driverLocation);
        GeoQuery geoQuery =gf.queryAtLocation
                (new GeoLocation(25.757950,-102.984002),radios);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {

                FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl)
                        .child(key)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Users user = dataSnapshot.getValue(Users.class);

                                mMap.clear();
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .flat(true)
                                        .title(user.getNombre())
                                        .snippet(user.getTelefono()));
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });


            }
            @Override
            public void onKeyExited(String key) {
            }
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }
            @Override
            public void onGeoQueryReady() {

                if(distance <= LIMIT){
                    distance++;
                    loadAllAvailableUsers();
                }

            }
            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });

    }


}
