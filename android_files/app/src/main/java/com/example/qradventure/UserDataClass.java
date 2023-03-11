// https://www.tutorialspoint.com/java/java_using_singleton.htm
package com.example.qradventure;

import android.location.Location;

public class UserDataClass {
    // private static instance variable to hold the singleton instance
    private static volatile UserDataClass INSTANCE = null;
    private Location currentLocation;
    private String phoneInfo;
    private String emailInfo;
    private String username;

    private String userPhoneID;

    // private constructor to prevent instantiation of the class
    private UserDataClass() {}

    // public static method to retrieve the singleton instance
    public static UserDataClass getInstance() {
        // Check if the instance is already created
        if(INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (UserDataClass.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = new UserDataClass();
                }
            }
        }
        // return the singleton instance
        return INSTANCE;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getPhoneInfo() {
        return this.phoneInfo;
    }

    public String getEmailInfo() {
        return this.emailInfo;
    }

    public String getUsername() {
        return this.username;
    }

    public void setPhoneInfo(String p) {
        this.phoneInfo = p;
    }

    public void setEmailInfo(String e) {
        this.emailInfo = e;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public String getUserPhoneID() {
        return userPhoneID;
    }

    public void setUserPhoneID(String userPhoneID) {
        this.userPhoneID = userPhoneID;
    }

}
