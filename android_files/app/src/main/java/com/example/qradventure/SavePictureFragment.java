package com.example.qradventure;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class SavePictureFragment extends DialogFragment {
//    Bitmap picture;
    String toastMessage;
    Uri cam_uri;

    SavePictureFragment(Uri picture) {
        this.cam_uri = picture;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_save_picture, null);

        // Initialize variables to be changed))
        TextView saveText = (TextView) view.findViewById(R.id.save_picture_text);
        ImageView savePictureView = (ImageView) view.findViewById(R.id.save_picture_view);

        saveText.setText("Would you like to save the picture?");
//        savePictureView.setImageBitmap(picture);
//        Uri cam = null;
        savePictureView.setImageURI(cam_uri);
//        savePictureView.setImageURI(cam);

        // Return dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Save picture
                                toastMessage = "Saving picture...";
                            }
                        })
                .setNegativeButton("Discard",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Discard picture
                                toastMessage = "Discarding picture...";
                            }
                        });

        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
        PromptGeolocationFragment frag = new PromptGeolocationFragment();
        frag.show(getActivity().getSupportFragmentManager(), "Prompt Geolocation");
    }

}
