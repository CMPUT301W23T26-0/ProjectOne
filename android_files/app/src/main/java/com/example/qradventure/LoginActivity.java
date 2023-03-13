package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import org.w3c.dom.Document;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button signInButton;
    private EditText emailInput;
    private EditText phoneInput;

    private EditText usernameInput;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "LoginActivity";
    private UserDataClass user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize singleton userdata
        user = user.getInstance();

        // Get device ID for database checking
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        user.setUserPhoneID(android_id);

        // Check database for user
        DocumentReference userRef = db.collection("Users").document(android_id);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // If new user, display login page and user to database
                    if (!document.exists()) {
                        // Display login page
                        setContentView(R.layout.activity_login);
                        signInButton = findViewById(R.id.login_button);
                        emailInput = findViewById(R.id.emailContact);
                        phoneInput = findViewById(R.id.phoneContact);
                        usernameInput = findViewById(R.id.username);
                        // If sign in button clicked, add user, email, phone fields
                        signInButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // fill user data in singleton
                                String email = emailInput.getText().toString();
                                String phone = phoneInput.getText().toString();
                                String username = usernameInput.getText().toString();
                                user.setEmailInfo(email);
                                user.setPhoneInfo(phone);
                                // If user didn't give a username, default user is their device name
                                if (username.isEmpty()) {
                                    username = user.getUserPhoneID();
                                }
                                user.setUsername(username);
                                // create hashmap for database entry
                                Map<String, Object> newUser = new HashMap<>();
                                newUser.put("username", username);
                                newUser.put("email", email);
                                newUser.put("phone", phone);
                                db.collection("Users").document(android_id)
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
                                // User now registered, go to main
                                Intent registered = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(registered);
                            }
                        });
                    // User already has account, update user singleton and go to main
                    } else {
                        Map<String, Object> userInfo = document.getData();
                        String username = (String) userInfo.get("username");

                        user.setUsername(username);
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