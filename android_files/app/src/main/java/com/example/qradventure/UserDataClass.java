// https://www.tutorialspoint.com/java/java_using_singleton.htm
package com.example.qradventure;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;
    private CollectionReference userCodesRef;

    private static final String TAG = "UserDataClass";

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

    public interface tryRegisterCallback {
        void onCallback(boolean isRegistered);
    }

    /**
     * This method tries to register a user given their android_id,
     * and uses a callback interface that takes a boolean saying whether
     * the user is already registered or not
     * @param android_id User's device id
     * @param callback tryRegisterCallback interface called when document
     *                 is retrieved
     */
    public void tryRegister(String android_id, tryRegisterCallback callback) {
        // initialize data
        this.userPhoneID = android_id;
        this.userRef = db.collection("Users").document(android_id);
        this.userCodesRef = this.userRef.collection("Codes");
        // try registering user
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                boolean isRegistered;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // User is already registered, retrieve information from database
                        isRegistered = true;
                        Map<String, Object> userInfo = document.getData();
                        username = userInfo.get("username").toString();
                        emailInfo = userInfo.get("email").toString();
                        phoneInfo = userInfo.get("phone").toString();
                        totalScore = Integer.parseInt(userInfo.get("totalScore").toString());
                    } else {
                        // User is not registered yet, do registry in callback
                        isRegistered = false;
                    }
                    callback.onCallback(isRegistered);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * This method creates a new document in the database for the user
     * and saves the data locally
     * @param data Map containing user data
     */
    public void setData(Map<String, Object> data) {
        userRef
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        this.username = data.get("username").toString();
        this.emailInfo = data.get("email").toString();
        this.phoneInfo = data.get("phone").toString();
        this.totalScore = Integer.parseInt(data.get("totalScore").toString());
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
     * This function gets the user's total score
     * @return
     */
    public int getTotalScore() {
        return this.totalScore;
    }

    public DocumentReference getUserRef() {
        return this.userRef;
    }
    public CollectionReference getUserCodesRef() {
        return this.userCodesRef;
    }

    /**
     * This function sets the user's phone info
     * @param phone
     */
    public void setPhoneInfo(String phone) {
        this.phoneInfo = phone;
        updateField("phone", phone);
    }

    /**
     * This function sets the user's email info
     * @param email
     */
    public void setEmailInfo(String email) {
        this.emailInfo = email;
        updateField("email", email);
    }

    /**
     * This function sets the user's username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
        updateField("username", username);
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

    /**
     * This function sets the user's total score
     * @param score
     */
    public void setTotalScore(int score) {
        this.totalScore = score;
        updateField("totalScore", score);
    }

    /**
     * This function is a helper function that is called
     * whenever a user data changes locally, and automatically
     * updates the database with the changes
     * @param field
     * @param value
     */
    public void updateField(String field, Object value) {
        this.userRef.update(field, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
}
