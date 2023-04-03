// https://www.tutorialspoint.com/java/java_using_singleton.htm
package com.example.qradventure.users;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.qradventure.qrcode.QRCode;
import com.example.qradventure.qrcode.QRController;
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
    private Location currentLocation;
    private String phoneInfo;
    private String emailInfo;
    private String username;
    private int totalScore;
    private String userPhoneID;
    private int highestQrScore;
    private String highestQrHash;
    private final QRController controller = new QRController();
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
     * @return The user data instance
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
                        highestQrScore = Integer.parseInt(document.get("highestQrScore").toString());
                        highestQrHash = document.get("highestQrHash").toString();
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
        this.highestQrScore = Integer.parseInt(data.get("highestQrScore").toString());
        this.highestQrHash = data.get("highestQrHash").toString();

    }

    /**
     * This function gets the user's current location
     * @return The user's current location
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
     * @return The user's phone info
     */
    public String getPhoneInfo() {
        return this.phoneInfo;
    }

    /**
     * This function gets the user's email info
     * @return The user's email info
     */
    public String getEmailInfo() {
        return this.emailInfo;
    }

    /**
     * This function gets the user's username
     * @return The user's username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * This function gets the user's total score
     * @return The user's total score
     */
    public int getTotalScore() {
        return this.totalScore;
    }

    /**
     * This function gets the user's document reference
     * @return The user's document reference
     */
    public DocumentReference getUserRef() {
        return this.userRef;
    }

    /**
     * This function gets the user's QR code collection reference
     * @return The user's QR code collection reference
     */
    public CollectionReference getUserCodesRef() {
        return this.userCodesRef;
    }

    /**
     * This function gets the user's highest scoring QR code
     * @return The user's highest QR score
     */
    public int getHighestQrScore() {
        return this.highestQrScore;
    }

    /**
     * This function gets the hash of the user's highest scoring QR code
     * @return The user's highest scoring QR code
     */
    public String getHighestQrHash() {
        return this.highestQrHash;
    }

    /**
     * This function sets the user's phone info
     * and updates the database
     * @param phone The user's phone info
     */
    public void setPhoneInfo(String phone) {
        this.phoneInfo = phone;
        updateField("phone", phone);
    }

    /**
     * This function sets the user's email info
     * and updates the database
     * @param email The user's email info
     */
    public void setEmailInfo(String email) {
        this.emailInfo = email;
        updateField("email", email);
    }

    /**
     * This function sets the user's username
     * and updates the database
     * @param username The user's username
     */
    public void setUsername(String username) {
        this.username = username;
        updateField("username", username);
    }

    /**
     * This function gets the user's phone ID
     * @return The user's phone ID
     */
    public String getUserPhoneID() {
        return this.userPhoneID;
    }

    /**
     * This function sets the user's phone ID
     * @param userPhoneID The user's phone ID
     */
    public void setUserPhoneID(String userPhoneID) {
        this.userPhoneID = userPhoneID;
    }

    /**
     * This function sets the user's total score
     * and updates the database
     * @param score The user's total score
     */
    public void setTotalScore(int score) {
        this.totalScore = score;
        updateField("totalScore", score);
    }

    /**
     * This function sets the user's highest QR code
     * @param hash The hash of the QR code with the highest score
     * @param score The score of the QR code associated with the hash
     */
    public void setHighestQr(String hash, int score) {
        this.highestQrScore = score;
        this.highestQrHash = hash;
        updateField("highestQrScore", score);
        updateField("highestQrHash", hash);
    }

    public Drawable generateUserIcon(Context ctx, String uName) {
        // turn username into a hex hash
        byte[] bytes = (uName + "lauremepsumlauremepsum").getBytes();
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02X", b));
        }
        String profileHash = hex.toString();
        Log.d(TAG, profileHash);

        return controller.generateImage(ctx, profileHash);
    }

    /**
     * This function adds a code to the user's Code collection,
     * and updates the user's total score and highest scoring QR code as needed
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
     * This function deletes a user's QR code
     * @param code The QR code to be deleted from the user's account
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
     * This function refreshes the highest QR code
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
     * This function is a helper function that is called
     * whenever a user data changes locally, and automatically
     * updates the database with the changes
     * @param field The field to be updated
     * @param value The value that the field must be updated with
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
