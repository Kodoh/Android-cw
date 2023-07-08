package com.example.cob155cw;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.time.LocalDate;

public class LocationTrackingService extends Service {

    private static final String TAG = "LocationTrackingService";        // Name
    private static final long MIN_TIME_INTERVAL = 1000; // Minimum time interval between location updates (in milliseconds)
    private static final float MIN_DISTANCE = 10f; // Minimum distance interval between location updates (in meters)

    private LocationManager locationManager;
    private final IBinder binder = new LocalBinder();
    private LocationListener locationListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");              // For debugging

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {      // All I need for checking date relative to timezone
                // Retrieve the user's location and update the local date and time
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LocalDate localDateTime = getLocalDateTime();

                Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude + ", Local Date and Time: " + localDateTime);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
    }

    public class LocalBinder extends Binder {
        public LocationTrackingService getService() {
            return LocationTrackingService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        // Check for location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Request location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL, MIN_DISTANCE, locationListener);
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");

        // Stop location updates
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public LocalDate getLocalDateTime() {
        // Get the current local date and time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDate.now();
        }
        return null;
    }
}
