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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "LoginActivity";
    private UserDataClass user;

    /**
     * This function runs a set of instructions upon activity
     * creation, which includes data instantiation and activity
     * set up.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize singleton userdata
        user = user.getInstance();

        // Get device ID for database checking
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        user.setUserPhoneID(android_id);
        // Handle user registry or login
        handleUser(android_id);
    }

    private void registerUser(String id) {
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
                String username = usernameInput.getText().toString();
                // Use phoneID if username not given
                if (username.isEmpty()) {
                    username = user.getUserPhoneID();
                }

                // Check if given username is available
                checkUsernameAvailable(username, new checkUsernameCallback() {
                    @Override
                    public void onCallback(boolean nameAvailable, String username) {
                        // If available, register user into database
                        if (nameAvailable) {
                            String email = emailInput.getText().toString();
                            String phone = phoneInput.getText().toString();
                            // Create hashmap for database entry
                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("username", username);
                            newUser.put("email", email);
                            newUser.put("phone", phone);

                            // Add user to database
                            db.collection("Users").document(id)
                                    .set(newUser)
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
                            // Set user singleton data
                            user.setUsername(username);
                            user.setEmailInfo(email);
                            user.setPhoneInfo(phone);
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
        void onCallback(boolean nameAvailable, String username);
    }

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
                    callback.onCallback(isAvailable, username);
                }
            });
    }


    private void handleUser(String id) {
        // Check database for user
        DocumentReference userRef = db.collection("Users").document(id);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // If new user, display login page and add user to database
                    if (!document.exists()) {
                       registerUser(id);
                    // User already has account, update user singleton and go to main activity
                    } else {
                        Map<String, Object> userInfo = document.getData();
                        user.setUsername(userInfo.get("username").toString());
                        user.setEmailInfo(userInfo.get("email").toString());
                        user.setPhoneInfo(userInfo.get("phone").toString());

                        Intent login = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(login);
                    }

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}