package com.example.qradventure;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class PromptGeolocationFragment extends DialogFragment {
    public PromptGeolocationFragment() {
        // empty
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String promptTitle = "Would you like to record the QR code's geolocation?";

        // Return dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(promptTitle)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //something
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //nothing
                            }
                        });

        return builder.create();
    }
}
