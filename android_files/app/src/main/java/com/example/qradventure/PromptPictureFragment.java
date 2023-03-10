package com.example.qradventure;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
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
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class PromptPictureFragment extends DialogFragment {
    String toastMessage = "Skipping picture...";
//    Bitmap picture;
    Uri picture;

    // https://www.youtube.com/watch?v=JMdHMMEO8ZQ
//    private final ActivityResultLauncher<Intent> fragmentLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                    Bundle bundle = result.getData().getExtras();
//                    picture  = (Bitmap) bundle.get("data");
//                    SavePictureFragment frag = new SavePictureFragment(picture);
//                    frag.show(getActivity().getSupportFragmentManager(), "Prompt Geolocation");
//                }
//                Bundle bundle = result.getData().getExtras();
//                picture  = (Bitmap) bundle.get("data");
//                SavePictureFragment frag = new SavePictureFragment(picture);
//                frag.show(getActivity().getSupportFragmentManager(), "Prompt Geolocation");
//            });

//    private final ActivityResultLauncher<Intent> fragmentLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                Intent data = result.getData();
//                picture = (Bitmap) data.getExtras().get("data");
//                SavePictureFragment frag = new SavePictureFragment(picture);
//                frag.show(getActivity().getSupportFragmentManager(),"Save picture");
//            });

//    ActivityResultLauncher<Intent> fragmentLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == RESULT_OK) {
//                        // There are no request codes
//                        Intent data = result.getData();
//                        picture = data.getData();
//                        displaySavePicture(picture);
//                    } else {
//                        Intent data = result.getData();
//                        picture = data.getData();
//                        displaySavePicture(picture);
//                    }
//
//                }
//            }
//    );

    Uri cam_uri = null;

//    ActivityResultLauncher<Uri> startCamera = registerForActivityResult(
//            new ActivityResultContracts.TakePicture(),
//            new ActivityResultCallback<Boolean>() {
//                @Override
//                public void onActivityResult(Boolean result) {
////                    if (result.getResultCode() == RESULT_OK) {
////                        // There are no request codes
////                        displaySavePicture(cam_uri);
////                    }
//                    displaySavePicture(cam_uri);
//                }
//            }
//    );

    // https://medium.com/codex/how-to-implement-the-activity-result-api-takepicture-contract-with-uri-return-type-7c93881f5b0f
//    ActivityResultLauncher<Intent> startCamera = registerForActivityResult(new ActivityResultContracts.TakePicture()) {
//
//    }

    ActivityResultLauncher<Intent> startCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    cam_uri = (Uri) result.getData().getExtras().get("data");
                    displaySavePicture(cam_uri);
                }
            }
    );

    public void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
//        cam_uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri);

        //startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE); // OLD WAY
        startCamera.launch(cameraIntent); // VERY NEW WAY
    }

    private void displaySavePicture(Uri picture) {
        SavePictureFragment frag = new SavePictureFragment(picture);
        frag.show(getActivity().getSupportFragmentManager(),"Save picture");
    }

//    private final ActivityResultLauncher<Intent> fragmentLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//
////            new ActivityResultCallback<ActivityResult>() {
////                @Override
////                public void onActivityResult(ActivityResult result) {
////                    if (result.getResultCode() == Activity.RESULT_OK) {
////                        Intent data = result.getData();
////                        Bitmap photo = (Bitmap) data.getExtras().get("data");
////
////                        View view = getActivity().getLayoutInflater()
////                                .inflate(R.layout.fragment_scan, null);
////
////                        ImageView imageTest = (ImageView) view.findViewById(R.id.testCamera);
////                        imageTest.setImageBitmap(photo);
////                    }
////                }
//                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
////                    View view = getActivity().getLayoutInflater()
////                            .inflate(R.layout.fragment_scan, null);
//                    picture = (Bitmap) result.getData().getExtras().get("data");
////                    ImageView imageTest = (ImageView) view.findViewById(R.id.testCamera);
////                    imageTest.setImageBitmap(photo);
//                }
//                picture = (Bitmap) result.getData().getExtras().get("data");
//
//            });



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

                                toastMessage = "Opening camera...";
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
        System.out.println("Hi");
//        displaySavePicture(picture);
//        displaySavePicture(cam_uri);
    }

    private void openCamera() {
//        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        fragmentLauncher.launch(camera);
        pickCamera();
    }
}
