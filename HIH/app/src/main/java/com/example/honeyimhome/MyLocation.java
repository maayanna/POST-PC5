package com.example.honeyimhome;

public class MyLocation {


    private double latitude;
    private double longitude;
    private float accuracy;

    MyLocation(double latitude, double longitude, float accuracy){

        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;

    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public void setAccuracy(float accuracy){
        this.accuracy = accuracy;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public float getAccuracy(){
        return accuracy;
    }
}
