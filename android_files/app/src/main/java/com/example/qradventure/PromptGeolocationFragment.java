package com.example.qradventure;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class PromptGeolocationFragment extends DialogFragment {
    String toastMessage = "Skipping geolocation...";
    private UserDataClass user;

    private QRCode code;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PromptGeolocationFragment(QRCode code) {
        this.code = code;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        user = user.getInstance();

        String promptTitle = "Would you like to record the QR code's geolocation?";

        // Return dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(promptTitle)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Record geolocation to QRCode
                                Location currLocation = user.getCurrentLocation();
                                toastMessage = "Saving geolocation...";
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Don't record geolocation
                            }
                        });

        return builder.create();
    }

    // In case user doesn't pick Yes or No, assume No for safety
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
    }
}
