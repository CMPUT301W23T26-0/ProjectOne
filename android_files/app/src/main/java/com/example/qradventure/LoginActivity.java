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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button signInButton;
    private EditText emailInfo;
    private EditText phoneInfo;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get device ID for database checking
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // deletes your device from the database,
        // uncomment if you want to see the sign in page when running
        /*
        db.collection("Users").document(android_id)
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
                });
         */

        DocumentReference userRef = db.collection("Users").document(android_id);
        // Check database for user
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
                        emailInfo = findViewById(R.id.emailContact);
                        phoneInfo = findViewById(R.id.phoneContact);
                        // If sign in button clicked, add user fields, currently just username
                        // TO ADD: optional contact information field(s)
                        signInButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Map<String, Object> newUser = new HashMap<>();
                                newUser.put("username", android_id);
                                newUser.put("email", emailInfo.getText().toString());
                                newUser.put("phone", phoneInfo.getText().toString());
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
                    // User already has account, go to main
                    } else {
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