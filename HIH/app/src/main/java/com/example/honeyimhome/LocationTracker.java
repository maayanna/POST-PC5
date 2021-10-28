package com.example.honeyimhome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;


public class LocationTracker {

    private Context myContext;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private MyLocation myLocation;


    LocationTracker(Context context){
        myContext = context;
        locationManager = (LocationManager) this.myContext.getSystemService(Context.LOCATION_SERVICE);
    }


    /**
     * start tracking the location and send a "started" broadcast intent
     */
    public void startTracking(){

        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {


            locationListener = new LocationListener() {
                @Override
                // Change of location after first location found
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        myLocation = new MyLocation(location.getLatitude(), location.getLongitude(), location.getAccuracy());
                        Gson gson = new Gson();
                        Intent track = new Intent("track");
                        String locationStr = gson.toJson(myLocation);
                        track.putExtra("locGson", locationStr);
                        myContext.sendBroadcast(track);
                    }
                }

                // all override needed

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }
            };

            // First Location

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.1f, locationListener);
            Location location = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Gson gson = new Gson();
            Intent track = new Intent("track");

            if (location != null){
                myLocation = new MyLocation(location.getLatitude(), location.getLongitude(), location.getAccuracy());
                String locationStr = gson.toJson(myLocation);
                track.putExtra("locGson", locationStr);
            }

            myContext.sendBroadcast(track);
        }
    }

    /**
     * stop tracking and send a "stopped" broadcast intent
     */
    public void stopTracking(){

        locationManager.removeUpdates(locationListener);
        Intent stop = new Intent("stop");
        myContext.sendBroadcast(stop);
    }

}
