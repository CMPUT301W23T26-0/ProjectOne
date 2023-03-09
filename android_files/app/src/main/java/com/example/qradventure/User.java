package com.example.qradventure;

import android.location.Location;

public class User {


    private Location currentLocation;

    public User(){

    }


    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}
