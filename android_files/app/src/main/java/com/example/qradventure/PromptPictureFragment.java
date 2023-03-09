package com.example.qradventure;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.DialogFragment;

public class PromptPictureFragment extends DialogFragment {
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
                                // Prompt geolocation after
                                PromptGeolocationFragment frag = new PromptGeolocationFragment();
                                frag.show(getActivity().getSupportFragmentManager(), "Prompt Geolocation");
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Don't let user take picture
                                // Prompt geolocation after
                                PromptGeolocationFragment frag = new PromptGeolocationFragment();
                                frag.show(getActivity().getSupportFragmentManager(), "Prompt Geolocation");
                            }
                        });

        return builder.create();
    }
}
