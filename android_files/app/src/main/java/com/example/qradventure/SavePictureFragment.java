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
    Bitmap picture;
    String toastMessage = "Discarding picture...";
    Boolean state = false;

    public interface PictureInScanFrag {
        void savePictureInScanFrag (Bitmap picture, Boolean state);
    }

    PictureInScanFrag scanFrag;

    public void setScanFragment(PictureInScanFrag scanFrag) {
        this.scanFrag = scanFrag;
    }

    SavePictureFragment(Bitmap picture) {
        this.picture = picture;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_save_picture, null);

        // Initialize variables to be changed))
        TextView saveText = (TextView) view.findViewById(R.id.save_picture_text);
        ImageView savePictureView = (ImageView) view.findViewById(R.id.save_picture_view);

        saveText.setText("Would you like to save the picture?");
        savePictureView.setImageBitmap(picture);
//        savePictureView.setImageURI(picture);

        // Return dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Save picture
                                toastMessage = "Saving picture...";
                                state = true;
                            }
                        })
                .setNegativeButton("Discard",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Discard picture
                            }
                        });

        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
        scanFrag.savePictureInScanFrag(picture, state);
    }

}
