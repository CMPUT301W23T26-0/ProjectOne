package com.example.qradventure;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class PromptPictureFragment extends DialogFragment {
    String toastMessage = "Skipping picture...";

    public PromptPictureFragment() {
        // empty
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String promptTitle = "Would you like to take a picture of the QR code's location?";

        // Return dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(promptTitle)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Allow user to take picture
                                toastMessage = "Saving picture...";
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Skip picture taking
                            }
                        });

        return builder.create();
    }

    // In case user doesn't pick Yes or No, assume No for safety
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
        PromptGeolocationFragment frag = new PromptGeolocationFragment();
        frag.show(getActivity().getSupportFragmentManager(), "Prompt Geolocation");
    }
}
