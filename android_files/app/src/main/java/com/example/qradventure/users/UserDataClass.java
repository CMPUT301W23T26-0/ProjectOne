// https://www.tutorialspoint.com/java/java_using_singleton.htm
package com.example.qradventure.users;

import static android.content.ContentValues.TAG;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.qradventure.qrcode.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.K;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
    private Map<Integer, ArrayList<String>> qrScores;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
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

    public interface checkRegisteredCallback {
        void onCallback(boolean isRegistered);
    }

    /**
     * This method checks if the singleton is already registered
     * and uses a callback interface that takes a boolean saying whether
     * the user is already registered or not
     * If the user is registered, then it retrieves user data
     * @param callback tryRegisterCallback interface called when document
     *                 is retrieved
     */
    public void checkRegistered(checkRegisteredCallback callback) {
        this.userRef = db.collection("Users").document(this.userPhoneID);
        this.userCodesRef = db.collection("Users").document(userPhoneID).collection("Codes");
        // try registering user
        this.userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                boolean isRegistered;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // User is already registered, retrieve information from database
                    // User is not registered yet, do registry in callback
                    // Get data from existing user
                    if (document.exists()) {
                        isRegistered = true;
                        username = document.get("username").toString();
                        emailInfo = document.get("email").toString();
                        phoneInfo = document.get("phone").toString();
                        totalScore = Integer.parseInt(document.get("totalScore").toString());
                        qrScores = (Map<Integer, ArrayList<String>>) document.get("QrScores");
                    } else {
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
        this.userRef
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
        this.qrScores = (Map<Integer, ArrayList<String>>) data.get("qrScores");
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

    /**
     * This function gets the user's document reference
     * @return
     */
    public DocumentReference getUserRef() {
        return this.userRef;
    }

    /**
     * This function gets the user's QR code collection reference
     * @return
     */
    public CollectionReference getUserCodesRef() {
        return this.userCodesRef;
    }

    /**
     * This function gets the user's highest scoring QR code
     * @return
     */
    public Map<Integer, ArrayList<String>> getQrScores() {
        return this.qrScores;
    }

    public int getHighestQrScore() {
        int max = 0;
        for (Integer score: this.qrScores.keySet()) {
            if (score > max) {
                max = score;
            }
        }
        return max;
    }

    public String getHighestQrHash() {
        ArrayList<String> topQrs;
        topQrs = this.qrScores.get(getHighestQrScore());
        if (!topQrs.isEmpty()) {
            return topQrs.get(-1);
        } else {
            return "";
        }
    }

    /**
     * This function sets the user's phone info
     * and updates the database
     * @param phone
     */
    public void setPhoneInfo(String phone) {
        this.phoneInfo = phone;
        updateField("phone", phone);
    }

    /**
     * This function sets the user's email info
     * and updates the database
     * @param email
     */
    public void setEmailInfo(String email) {
        this.emailInfo = email;
        updateField("email", email);
    }

    /**
     * This function sets the user's username
     * and updates the database
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
        return this.userPhoneID;
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
     * and updates the database
     * @param score
     */
    public void setTotalScore(int score) {
        this.totalScore = score;
        updateField("totalScore", score);
    }

    public boolean checkNewHighestQr(String hash, int score) {
        return score > getHighestQrScore();
    }

    /**
     * This function adds a code to the user's Code collection,
     * and updates the user's total score and highest scoring QR code as needed
     * @param codeID The code's hash value that acts as the documentID
     * @param QrCode A map containing the code data to be inputted to the database
     */
    public void addUserCode(String codeID, Map<String, Object> QrCode) {
        userCodesRef.document(codeID)
                .set(QrCode)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        int score = (int) QrCode.get("score");
                        String hash = QrCode.get("hash").toString();
                        if (qrScores.containsKey(score)) {
                            ArrayList<String> newEntry = new ArrayList<String>();
                            newEntry.add(hash);
                            qrScores.put(score, newEntry);
                        } else {
                            ArrayList<String> currentEntries = qrScores.get(score);
                            currentEntries.add(hash);
                            qrScores.put(score, currentEntries);
                        }
                        setTotalScore(getTotalScore() + score);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
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

    public void deleteCode(QRCode QrCode) {
        String hash = QrCode.getHashValue();
        this.userCodesRef.document(hash)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        int score = QrCode.getScore();
                        String hash = QrCode.getHashValue();

                        ArrayList<String> currentHashes = qrScores.get(score);
                        currentHashes.remove(hash);

                        if (currentHashes.isEmpty()) {
                            qrScores.remove(score);
                        }

                        setTotalScore(getTotalScore() - score);
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
