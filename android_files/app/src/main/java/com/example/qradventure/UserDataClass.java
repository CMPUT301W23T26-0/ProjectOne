// https://www.tutorialspoint.com/java/java_using_singleton.htm
package com.example.qradventure;

import android.location.Location;

/**
 * This class is used to store user data. It follows
 * a singleton design pattern.
 */
public class UserDataClass {
    // private static instance variable to hold the singleton instance
    private static volatile UserDataClass INSTANCE = null;

    // user data
    private Location currentLocation;
    private String phoneInfo;
    private String emailInfo;
    private String username;
    private String userPhoneID;

    /**
     * Constructor for the UserDataClass. It is set to
     * private to prevent instantiation of the class.
     */
    private UserDataClass() {}

    /**
     * This function retrieves and returns the user data instance,
     * which is a singleton instance
     * @return
     */
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

    /**
     * This function gets the user's current location
     * @return
     */
    public Location getCurrentLocation() {
        return currentLocation;
    }

    /**
     * This function sets the user's current location
     * @param currentLocation
     */
    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    /**
     * This function gets the user's phone info
     * @return
     */
    public String getPhoneInfo() {
        return this.phoneInfo;
    }

    /**
     * This function gets the user's email info
     * @return
     */
    public String getEmailInfo() {
        return this.emailInfo;
    }

    /**
     * This function gets the user's username
     * @return
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * This function sets the user's phone info
     * @param phone
     */
    public void setPhoneInfo(String phone) {
        this.phoneInfo = phone;
    }

    /**
     * This function sets the user's email info
     * @param email
     */
    public void setEmailInfo(String email) {
        this.emailInfo = email;
    }

    /**
     * This function sets the user's username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * This function gets the user's phone ID
     * @return
     */
    public String getUserPhoneID() {
        return userPhoneID;
    }

    /**
     * This function sets the user's phone ID
     * @param userPhoneID
     */
    public void setUserPhoneID(String userPhoneID) {
        this.userPhoneID = userPhoneID;
    }

}
