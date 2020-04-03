package com.example.travel;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.firestore.remote.Stream;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap groupMap;
    private Map<String, Marker> markers;

    private String groupid;

    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

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

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if(location != null) {
                    String locationString = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
                    System.out.println("location data: " + locationString);
                    groupdoc.update(
                            "users." + user.getUid() + ".location" , locationString
                    );
                }

            }
        };

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        groupMap = googleMap;
        markers = new HashMap<>();
        getPermissions();
        streamLocations();

    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Exit group?")
                .setMessage("This action will terminate you from the group")
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        client.removeLocationUpdates(locationCallback);

                        Map<String, Object> deleteGroupMap = new HashMap<>();
                        deleteGroupMap.put("users."+user.getUid(), FieldValue.delete());
                        groupdoc.update(deleteGroupMap);

                        groupdoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String, Object> docMap = documentSnapshot.getData();
                                if(docMap.get("users") == null) {
                                    groupdoc.delete();
                                }
                            }
                        });

                        Map<String, Object> deleteMap = new HashMap<>();
                        deleteMap.put("group", FieldValue.delete());
                        firestore.collection("users").document(user.getUid()).update(deleteMap);
                        finish();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();

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
            client.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        } else {
            getPermissions();
        }
    }

    public void streamLocations() {
        groupdoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if(e != null) {
                    System.out.println("error: " + e.toString());
                    return;
                }

                if(documentSnapshot != null && documentSnapshot.exists()) {
                    Map<String, Object> documentData = documentSnapshot.getData();
                    Map<String, Map<String, String>> groupmembers = (Map<String, Map<String, String>>) documentData.get("users");
                    for(String id: markers.keySet()) {
                        if(groupmembers.containsKey(id) == false) {
                            Marker marker = markers.get(id);
                            if(marker != null) {
                                marker.remove();
                                markers.remove(id);
                            }
                        }
                    }
                    System.out.println("\nFIRESTORE DATA");
                    for(Map.Entry<String, Map<String, String>> member : groupmembers.entrySet()) {

                        String member_id = member.getKey();
                        String member_name = member.getValue().get("name");
                        String member_location = member.getValue().get("location");

                        if(member_location.equals("null") == false) {
                            LatLng member_latlng = new LatLng(Double.valueOf(member_location.split(",")[0]), Double.valueOf(member_location.split(",")[1]));
                            if(markers.containsKey(member_id)) {
                                markers.get(member_id).setPosition(member_latlng);
                            } else {
                                Marker userMarker = groupMap.addMarker(new MarkerOptions()
                                        .position(member_latlng)
                                        .title(member_name)
                                        .visible(true));
                                markers.put(member_id, userMarker);
                            }
                        }

                    }
                }

            }
        });
    }

}