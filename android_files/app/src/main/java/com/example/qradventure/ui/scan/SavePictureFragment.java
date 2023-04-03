package com.example.qradventure.ui.scan;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.qradventure.R;
import com.example.qradventure.users.UserDataClass;
import com.example.qradventure.qrcode.QRCode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class extends DialogFragment, and it serves to display a
 * a newly taken picture while also prompting the user if they
 * want to save or discard the picture.
 */
public class SavePictureFragment extends DialogFragment {
    private Bitmap picture;
    private String toastMessage = "Discarding picture...";

    // Data
    private UserDataClass user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbCodes;
    private QRCode code;

    // https://stackoverflow.com/questions/26734432/send-data-from-dialogfragment-to-fragment
    /**
     * This interface allows SavePictureFragment to open up
     * PromptGeolocationFragment from ScanFragment. This is done
     * so that DialogFragments appear in the correct order
     * rather than concurrently.
     */
    public interface GeolocationInScanFrag {
        void promptGeolocationInScanFrag();
    }

    private GeolocationInScanFrag scanFrag;

    /**
     * This function allows an instance of the
     * SavePictureFragment to establish
     * a connection with an instance of the
     * ScanFragment
     * @param scanFrag An instance of ScanFragment
     */
    public void setScanFragment(GeolocationInScanFrag scanFrag) {
        this.scanFrag = scanFrag;
    }

    /**
     * Constructor for the SavePictureFragment
     * @param picture The newly taken picture
     * @param code The newly scanned QR code associated with the picture
     */
    SavePictureFragment(Bitmap picture, QRCode code) {
        this.picture = picture;
        this.code = code;
    }

    /**
     * This function runs a set of instructions upon creating the
     * view for SavePictureFragment, which includes data instantiation
     * and view set up.
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return The view of SavePictureFragment
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Instantiate data
        dbCodes = db.collection("QRCodes");
        user = user.getInstance();

        // Get view for SavePictureFragment
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_save_picture, null);

        // Initialize variables to be changed
        TextView saveText = (TextView) view.findViewById(R.id.save_picture_text);
        ImageView savePictureView = (ImageView) view.findViewById(R.id.save_picture_view);

        saveText.setText("Would you like to save the picture?");
        savePictureView.setImageBitmap(picture);

        // Return edited view for DialogFragment
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Save picture
                                savePicture();
                                toastMessage = "Saving picture...";
                            }
                        })
                .setNegativeButton("Discard",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Discard picture (done automatically)
                            }
                        });

        return builder.create();
    }

    /**
     * A function that runs a set of instructions when the view
     * is destroyed, which includes displaying a toast message
     * and opening up PromptGeolocationFragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
        scanFrag.promptGeolocationInScanFrag();
    }

    /**
     * This function saves the newly taken picture to the QR code,
     * which is recorded in the database.
     */
    private void savePicture() {
        // Update code in QRCode Collection
        DocumentReference docRef = dbCodes.document(code.getHashValue())
                .collection("Pictures").document(String.valueOf(picture));

        // https://stackoverflow.com/questions/40885860/how-to-save-bitmap-to-firebase#:~:text=To%20upload%20a%20file%20to,file%2C%20including%20the%20file%20name.&text=Once%20you've%20created%20an,the%20file%20to%20Firebase%20Storage.
        // Picture (bitmap) is compressed and converted into a byte array
        // Byte array is then converted into a string (Characters in string represent bytes from byte array)
        // String representation of byte array is saved to database
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
