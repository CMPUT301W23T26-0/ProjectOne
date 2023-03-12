package com.example.qradventure;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SavePictureFragment extends DialogFragment {
    Bitmap picture;
    String toastMessage = "Discarding picture...";
    Boolean success = false;

    private UserDataClass user;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    CollectionReference dbCodes;

    private QRCode code;


    public interface PictureInScanFrag {
        void savePictureInScanFrag (Bitmap picture, Boolean success);
    }

    PictureInScanFrag scanFrag;

    public void setScanFragment(PictureInScanFrag scanFrag) {
        this.scanFrag = scanFrag;
    }

    SavePictureFragment(Bitmap picture, QRCode code) {
        this.picture = picture;
        this.code = code;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dbCodes = db.collection("QRCodes");

        user = user.getInstance();

        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_save_picture, null);

        // Initialize variables to be changed))
        TextView saveText = (TextView) view.findViewById(R.id.save_picture_text);
        ImageView savePictureView = (ImageView) view.findViewById(R.id.save_picture_view);

        saveText.setText("Would you like to save the picture?");
        savePictureView.setImageBitmap(picture);

        // Return dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Save picture
                                savePicture();
                                toastMessage = "Saving picture...";
                                success = true;
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
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
        scanFrag.savePictureInScanFrag(picture, success);
    }

    private void savePicture() {
        DocumentReference docRef;
        
        // Update code in QRCode Collection
        docRef = dbCodes.document(code.getHashValue())
                .collection("Pictures").document(String.valueOf(picture));

        // https://stackoverflow.com/questions/40885860/how-to-save-bitmap-to-firebase#:~:text=To%20upload%20a%20file%20to,file%2C%20including%20the%20file%20name.&text=Once%20you've%20created%20an,the%20file%20to%20Firebase%20Storage.
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
        picture.recycle();
        byte[] byteArray = bao.toByteArray();
        String imageB64 = Base64.encodeToString(byteArray, Base64.URL_SAFE); // Converts byte array to string
        Map<String, Object> newCode = new HashMap<>();
        newCode.put("picture", imageB64);

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
}
