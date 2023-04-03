// https://www.tutorialspoint.com/java/java_using_singleton.htm
package com.example.qradventure.users;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Objects;

/**
 * This class is used to store user data. It follows
 * a singleton design pattern.
 */
public class UserDataClass {
    // private static instance variable to hold the singleton instance
    private static volatile UserDataClass INSTANCE = null;

    // user data
    private String phoneInfo;
    private String emailInfo;
    private String username;
    private int totalScore;
    private String userPhoneID;
    private int highestQrScore;
    private String highestQrHash;
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
     * Retrieves and returns the user data instance,
     * which is a singleton instance
     * @return  Singleton instance
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
     * Listener called when checkRegister task completes successfully
     */
    public interface checkRegisteredCallback {
        void onCallback(boolean isRegistered);
    }

    /**
     * Checks if the singleton is already registered
     * and uses a callback interface that takes a boolean saying whether
     * the user is already registered or not
     * If the user is registered, then it retrieves user data
     * @param callback checkRegisteredCallback interface that is called when
     *                 document is retrieved
     */
    public void checkRegistered(checkRegisteredCallback callback) {
        this.userRef = db.collection("Users").document(this.userPhoneID);
        this.userCodesRef = db.collection("Users").document(userPhoneID).collection("Codes");
        // try registering user
        this.userRef.get().addOnCompleteListener(task -> {
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
                    highestQrScore = Integer.parseInt(document.get("highestQrScore").toString());
                    highestQrHash = document.get("highestQrHash").toString();
                } else {
                    isRegistered = false;
                }
                callback.onCallback(isRegistered);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Creates a new document in the database for the user
     * and sets the data locally
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
        this.highestQrScore = Integer.parseInt(data.get("highestQrScore").toString());
        this.highestQrHash = data.get("highestQrHash").toString();

    }

    /**
     * Gets the user's phone info
     * @return String representing user's phone number
     */
    public String getPhoneInfo() {
        return this.phoneInfo;
    }

    /**
     * Gets the user's email info
     * @return String representing user's email
     */
    public String getEmailInfo() {
        return this.emailInfo;
    }

    /**
     * Gets the user's username
     * @return String representing user's username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gets the user's total score
     * @return Integer representing user's total score
     */
    public int getTotalScore() {
        return this.totalScore;
    }

    /**
     * Gets the user's document reference
     * @return DocumentReference of user's data
     */
    public DocumentReference getUserRef() {
        return this.userRef;
    }

    /**
     * Gets the user's QR code collection reference
     * @return CollectionReference of user's code data
     */
    public CollectionReference getUserCodesRef() {
        return this.userCodesRef;
    }

    /**
     * Gets the score of the user's highest scoring QR code
     * @return Integer representing score of user's highest scoring QR code
     */
    public int getHighestQrScore() {
        return this.highestQrScore;
    }

    /**
     * Gets the hash of the user's highest scoring QR code
     * @return String representing the hash of user's highest scoring QR code
     */
    public String getHighestQrHash() {
        return this.highestQrHash;
    }

    /**
     * Sets the user's phone info
     * and updates the database
     * @param phone Phone information to be set
     */
    public void setPhoneInfo(String phone) {
        this.phoneInfo = phone;
        updateField("phone", phone);
    }

    /**
     * Sets the user's email info
     * and updates the database
     * @param email Email information to be set
     */
    public void setEmailInfo(String email) {
        this.emailInfo = email;
        updateField("email", email);
    }

    /**
     * Sets the user's username
     * and updates the database
     * @param username Username to be set
     */
    public void setUsername(String username) {
        this.username = username;
        updateField("username", username);
    }

    /**
     * Gets the user's phone ID
     * @return String representing user's androidID
     */
    public String getUserPhoneID() {
        return this.userPhoneID;
    }

    /**
     * Sets the user's phone ID
     * @param userPhoneID androidID to be set
     */
    public void setUserPhoneID(String userPhoneID) {
        this.userPhoneID = userPhoneID;
    }

    /**
     * Sets the user's total score
     * and updates the database
     * @param score score to be set
     */
    public void setTotalScore(int score) {
        this.totalScore = score;
        updateField("totalScore", score);
    }

    /**
     * Sets information for the user's highest scoring QR code
     * and updates the database
     * @param hash hash to be set
     * @param score score to be set
     */
    public void setHighestQr(String hash, int score) {
        this.highestQrScore = score;
        this.highestQrHash = hash;
        updateField("highestQrScore", score);
        updateField("highestQrHash", hash);
    }

    /**
     * Adds a code to the user's Code collection and updates the user's
     * total score and highest scoring QR code if the score to be added is
     * higher than current score
     * @param codeID The code's hash value that acts as the documentID
     * @param code A map containing the code data to be inputted to the database
     */
    public void addUserCode(String codeID, Map<String, Object> code) {
        userCodesRef.document(codeID)
                .set(code)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        int newScore = (int) code.get("score");
                        if (newScore > highestQrScore) {
                            setHighestQr(codeID, newScore);
                        }
                        setTotalScore(totalScore + newScore);
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
     * Deletes a code from the user's Code collection and updates the user's
     * total score and highest scoring QR code if it was the current highest
     * scoring QR code
     * @param code QRCode to be removed
     */
    public void deleteUserCode(QRCode code) {
        String hash = code.getHashValue();
        userCodesRef.document(hash)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        int score = code.getScore();
                        if (totalScore < score) {
                            setTotalScore(0);
                        } else {
                            setTotalScore(totalScore - score);
                        }
                        if (Objects.equals(highestQrHash, hash)) {
                            refreshHighestQr();
                        }
                    }
                });
    }

    /**
     * Queries for the user's highest scoring QR code
     * in the database and sets local data for the highest QR code
     */
    public void refreshHighestQr() {
        userCodesRef.orderBy("score", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Map<String, Object> QRInfo = document.getData();
                                    String hash = QRInfo.get("hash").toString();
                                    int score = Integer.parseInt(QRInfo.get("score").toString());
                                    setHighestQr(hash, score);
                                }
                            } else {
                                setHighestQr("", 0);
                            }
                        }
                    }
                });
    }

    /**
     * A helper function that is called whenever user data changes locally,
     * and updates the database with the changes
     * @param field Field to be updated
     * @param value Value to update the field with
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
