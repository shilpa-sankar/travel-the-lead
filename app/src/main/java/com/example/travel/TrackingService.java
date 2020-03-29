package com.example.travel;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {

        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setFastestInterval(2000);
        request.setSmallestDisplacement(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if(location != null) {

                        String locationString = String.valueOf(location.getLatitude()) + '/' + String.valueOf(location.getLongitude());
                        // TODO: Push locationString to Firestore

                    }
                }
            }, null);
        }

    }

}
