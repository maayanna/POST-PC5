package com.example.honeyimhome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


public class MainActivity extends AppCompatActivity {

    Button trackButton;
    Button setHomeButon;
    Button clearHomeButton;

    TextView longitude;
    TextView latitude;
    TextView accuracy;
    TextView homePostition;

    LocationTracker myTracker;
    SharedPreferences sp;
    LocationBroadcast myBroadcast;


    final MainActivity main = this;
    final Context context = this;
    MyLocation myLoc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trackButton = (Button) findViewById(R.id.trackButton);
        setHomeButon = (Button) findViewById(R.id.setHome);
        clearHomeButton = (Button) findViewById(R.id.clearHome);

        longitude = (TextView) findViewById(R.id.longitudeView);
        latitude = (TextView) findViewById(R.id.latitudeView);
        accuracy = (TextView) findViewById(R.id.accuracyView);
        homePostition = (TextView) findViewById(R.id.homeLocationView);

        myTracker = new LocationTracker(this);
        myBroadcast = new LocationBroadcast();

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        Gson gson = new Gson();
        Type type = new TypeToken<MyLocation>() {}.getType();
        String home = sp.getString("home", null);
        MyLocation homeLocation = gson.fromJson(home, type);

        // If already in sp
        if (homeLocation != null){

            String homeLoc = "YOUR HOME LOCATION IS : " + homeLocation.getLatitude() + ", " + homeLocation.getLongitude();
            homePostition.setText(homeLoc);
            clearHomeButton.setVisibility(View.VISIBLE);

        }

        else{
            clearHomeButton.setVisibility(View.INVISIBLE);
        }

        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (trackButton.getText().equals("STOP TRACKING LOCATION")){
                    myTracker.stopTracking();
                    trackButton.setText("START TRACKING LOCATION");

                }

                else{
                    if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        myTracker.startTracking();
                        trackButton.setText("STOP TRACKING LOCATION");
                    }
                    else{
                        ActivityCompat.requestPermissions(main, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 34);
                    }
                }
            }
        });


        setHomeButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String homeLoc = "YOUR HOME LOCATION IS : " + myLoc.getLatitude() + ", " + myLoc.getLongitude();
                homePostition.setText(homeLoc);
                clearHomeButton.setVisibility(View.VISIBLE);

                // Keep it in sp for next time
                Gson gson = new Gson();
                String homegson = gson.toJson(myLoc);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("home", homegson)
                        .apply();

            }
        });

        clearHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("home")
                        .apply();
                homePostition.setText("");
                clearHomeButton.setVisibility(View.INVISIBLE);
            }
        });

        // thx brahan
        IntentFilter myFilter = new IntentFilter();
        // For on receive

        myFilter.addAction("stop");
        myFilter.addAction("track");
        context.registerReceiver(myBroadcast, myFilter);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTracker.stopTracking();
        unregisterReceiver(myBroadcast);
        Gson gson = new Gson();
        String homegson = gson.toJson(myLoc);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("home", homegson)
                .apply();
    }


    class LocationBroadcast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            setHomeButon.setVisibility(View.INVISIBLE);
            String todo = intent.getAction();

            if (todo.equals("track")){

                String loc = intent.getStringExtra("locGson");
                Gson gson = new Gson();
                Type type = new TypeToken<MyLocation>(){}.getType();
                myLoc = gson.fromJson(loc, type);


                // when tracking user's location and the accuracy is smaller than 50 meters,
                // the user can see a new button "set location as home".
                if (myLoc.getAccuracy() < 50 && trackButton.getText().equals("STOP TRACKING LOCATION")){
                    setHomeButon.setVisibility(View.VISIBLE);
                    // When clear home ? just when clear ?
                }

                String setLatitude = "LATITUDE : " + myLoc.getLatitude();
                latitude.setText(setLatitude);

                String setLongitude = "LONGITUDE : " + myLoc.getLongitude();
                longitude.setText(setLongitude);

                String setAccuracy = "ACCURACY : " + myLoc.getAccuracy();
                accuracy.setText(setAccuracy);


            }
            else{

                // action = stop

                setHomeButon.setVisibility(View.INVISIBLE);
                // apparement pas besoin
//                homePostition.setText("");

                // When clear home ? just when clear ?
//                clearHomeButton.setVisibility(View.INVISIBLE);
                latitude.setText("");
                longitude.setText("");
                accuracy.setText("");
            }

        }
    }
}
