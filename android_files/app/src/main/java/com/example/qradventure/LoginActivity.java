package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import org.w3c.dom.Document;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * This activity checks if a user is new or not. If they are
 * new, they are prompted to make an account. Otherwise, they
 * are logged into their existing account using their android
 * device ID.
 */
public class LoginActivity extends AppCompatActivity {
    private Button signInButton;
    private EditText emailInput;
    private EditText phoneInput;

    // Data
    private EditText usernameInput;
    private static final String TAG = "LoginActivity";
    private UserDataClass user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * This function runs a set of instructions upon activity
     * creation, which includes data instantiation and handling
     * user login/registration
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Handle user registry or login
        // Get device ID for database checking
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        user = user.getInstance(android_id);

        /*db.collection("Users").document(android_id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });*/

        user.checkRegistered(new UserDataClass.checkRegisteredCallback() {
            @Override
            public void onCallback(boolean isRegistered) {
                if (isRegistered) {
                    Intent login = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(login);
                } else {
                    registerUser();
                }
            }
        });
    }


    /**
     * This function registers users based on username
     * and contact information as well as updating
     * singleton data
     */
    private void registerUser() {
        // Display login page
        setContentView(R.layout.activity_login);
        signInButton = findViewById(R.id.login_button);
        emailInput = findViewById(R.id.emailContact);
        phoneInput = findViewById(R.id.phoneContact);
        usernameInput = findViewById(R.id.username);
        Toast nameAlert = Toast.makeText(getApplicationContext(), "Username taken, please try again.", Toast.LENGTH_SHORT);
        // If sign in button clicked, handle user registration
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username;
                if (!usernameInput.getText().toString().isEmpty()) {
                    username = usernameInput.getText().toString();
                } else {
                    // Use phoneID if username not given
                    username = user.getUserPhoneID();
                }

                // Check if given username is available
                checkUsernameAvailable(username, new checkUsernameCallback() {
                    @Override
                    public void onCallback(boolean nameAvailable) {
                        // If available, register user into database
                        if (nameAvailable) {
                            String email = emailInput.getText().toString();
                            String phone = phoneInput.getText().toString();

                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("username", username);
                            newUser.put("email", email);
                            newUser.put("phone", phone);
                            newUser.put("totalScore", 0);
                            // Set user singleton data
                            user.setData(newUser);
                            // Go to main activity
                            Intent registered = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(registered);
                            // If username not available, create alert
                        } else {
                            nameAlert.show();
                        }
                    }
                });
            }
        });
    }

    private interface checkUsernameCallback {
        // Callback interface when username query is finished
        void onCallback(boolean nameAvailable);
    }

    /**
     * This function checks if a username is available in the database
     * for registration
     *
     * @param username User inputted username (android_id if left
     *                 blank)
     * @param callback Callback interface used when username query
     *                 is finished
     */
    private void checkUsernameAvailable(String username, checkUsernameCallback callback) {
        db.collection("Users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean isAvailable = false;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            // If no results, isAvailable == true
                            isAvailable = task.getResult().isEmpty();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        // Call interface when query finished
                        callback.onCallback(isAvailable);
                    }
                });
        }
    }
