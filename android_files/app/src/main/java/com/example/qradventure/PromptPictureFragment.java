package com.example.qradventure;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class PromptPictureFragment extends DialogFragment {
    String toastMessage = "Skipping picture...";
    Uri image_uri;

    private final ActivityResultLauncher<Intent> fragmentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        Intent data = result.getData();
//                        Bitmap photo = (Bitmap) data.getExtras().get("data");
//
//                        View view = getActivity().getLayoutInflater()
//                                .inflate(R.layout.fragment_scan, null);
//
//                        ImageView imageTest = (ImageView) view.findViewById(R.id.testCamera);
//                        imageTest.setImageBitmap(photo);
//                    }
//                }
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                    View view = getActivity().getLayoutInflater()
//                            .inflate(R.layout.fragment_scan, null);
//
//                    ImageView imageTest = (ImageView) view.findViewById(R.id.testCamera);
//                    imageTest.setImageBitmap(photo);
                }
                Intent data = result.getData();
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                View view = getActivity().getLayoutInflater()
                        .inflate(R.layout.fragment_scan, null);

                ImageView imageTest = (ImageView) view.findViewById(R.id.testCamera);
                imageTest.setImageBitmap(photo);

                imageTest.setImageURI(image_uri);
            });



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
                                openCamera();

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

    private void openCamera() {
//        Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        fragmentLauncher.launch(camera);
    }
}
