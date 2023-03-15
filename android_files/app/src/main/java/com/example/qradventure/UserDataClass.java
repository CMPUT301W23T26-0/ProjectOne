// https://www.tutorialspoint.com/java/java_using_singleton.htm
package com.example.qradventure;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

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
    private int totalScore;
    private String userPhoneID;
    private final UserDataManager dataManager;
    private static final String TAG = "UserDataClass";

    /**
     * Constructor for the UserDataClass. It is set to
     * private to prevent instantiation of the class.
     */
    private UserDataClass(String android_id) {
        this.userPhoneID = android_id;
        this.dataManager = new UserDataManager(android_id);
        if (!this.dataManager.isNewUser()) {
            DocumentReference userRef = this.dataManager.getDocument();
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Map<String, Object> userInfo = document.getData();
                            username = userInfo.get("username").toString();
                            emailInfo = userInfo.get("email").toString();
                            phoneInfo = userInfo.get("phone").toString();
                            totalScore = (Integer) userInfo.get("totalScore");
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    /**
     * This function retrieves and returns the user data instance,
     * which is a singleton instance
     * @return
     */
    public static UserDataClass getInstance(String android_id) {
        // Check if the instance is already created
        if(INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (UserDataClass.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = new UserDataClass(android_id);
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

    public int getTotalScore() {
        return this.totalScore;
    }

    /**
     * This function sets the user's phone info
     * @param phone
     */
    public void setPhoneInfo(String phone) {
        this.phoneInfo = phone;
        dataManager.updateField("phone", phone);
    }

    /**
     * This function sets the user's email info
     * @param email
     */
    public void setEmailInfo(String email) {
        this.emailInfo = email;
        dataManager.updateField("email", email);
    }

    /**
     * This function sets the user's username
     * @param username
     */
    public void setUsername(String username) {
        if (username.isEmpty()) {
            username = this.userPhoneID;
        }
        this.username = username;
        dataManager.updateField("username", username);
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
        dataManager.updateField("id", userPhoneID);
    }

    public void setTotalScore(int s) {
        this.totalScore = s;
        dataManager.updateScore(this.totalScore);
    }

}
