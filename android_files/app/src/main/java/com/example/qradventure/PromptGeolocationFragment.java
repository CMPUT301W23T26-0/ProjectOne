package com.example.qradventure;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PromptGeolocationFragment extends DialogFragment {
    String toastMessage = "Skipping geolocation...";
    private UserDataClass user;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    CollectionReference dbCodes;

    private QRCode code;

    public PromptGeolocationFragment(QRCode code) {
        this.code = code;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        user = user.getInstance();

        dbCodes = db.collection("QRCodes");

        String promptTitle = "Would you like to record the QR code's geolocation?";

        // Return dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(promptTitle)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Record geolocation to QRCode
                                Location geolocation = user.getCurrentLocation();
                                geolocation = null;
                                saveGeolocation(geolocation);
                                toastMessage = "Saving geolocation...";
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Don't record geolocation
//                                saveGeolocation(null);
                            }
                        });

        return builder.create();
    }

    private void saveGeolocation(Location geolocation) {
        // ScanFragment already ensures the code exists, don't have to do a check
        DocumentReference docRef;

        // Update code fields
        Map<String, Object> newCode = new HashMap<>();
        newCode.put("location", geolocation);
        newCode.put("name", code.getName());
        newCode.put("score", code.getScore());
        newCode.put("hash", code.getHashValue());

        // Update code in QRCode Collection
        docRef = dbCodes.document(code.getHashValue());

        docRef.set(newCode)
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
    }

    // In case user doesn't pick Yes or No, assume No for safety
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }
}
