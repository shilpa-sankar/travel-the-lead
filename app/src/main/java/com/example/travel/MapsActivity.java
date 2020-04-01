package com.example.travel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.Document;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap groupMap;
    private String groupid;

    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;

    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private DocumentReference groupdoc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        groupid = getIntent().getStringExtra("groupid");

        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        groupdoc = firestore.collection("groups").document(groupid);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        client = new FusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        groupMap = googleMap;
        getPermissions();

    }

    public void getPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        Permissions.check(this, permissions, "Location permissions are required to share location data",
                new Permissions.Options().setSettingsDialogTitle("Warning!").setRationaleDialogTitle("Location permission"), new PermissionHandler() {

                    @Override
                    public void onGranted() {
                        System.out.println("permission granted to get location.");
                        requestLocationUpdates();
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        super.onDenied(context, deniedPermissions);
                        System.out.println("permission denied to get location.");
                        getPermissions();
                    }

                });
    }

    public void requestLocationUpdates() {

        groupdoc = firestore.collection("groups").document(groupid);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            client.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    if(location != null) {
                        String locationString = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
                        System.out.println("location data: " + locationString);
                        groupdoc.update(
                                "users." + user.getUid() , locationString
                        );
                    }

                }
            }, getMainLooper());
        } else {
            getPermissions();
        }
    }

}