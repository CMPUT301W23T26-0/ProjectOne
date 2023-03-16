package com.example.qradventure;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFragment extends Fragment implements DisplayCodePromptPictureFragment.CameraInScanFrag,
        SavePictureFragment.GeolocationInScanFrag{
    // For data
    private UserDataClass user;
    private QRCode code;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCodes;
    private CollectionReference dbCodes;

    /**
     * An implemented function from the CameraInScanFrag
     * interface. It lets the DisplayCodePromptPictureFragment
     * to open the camera in ScanFragment for picture
     * taking. Note that the camera can only be opened in a
     * Fragment, not a DialogFragment.
     * @param success Whether or not the user wants to take a picture.
     */
    @Override
    public void openCameraInScanFrag(Boolean success) {
        if (success) {
            // Open camera and resulting picture afterwards
            openCamera();
        } else {
            // Otherwise, just prompt geolocation
            promptGeolocation();
        }
    }

    /**
     * An implemented function from the GeolocationInScanFrag
     * interface. It lets the SavePictureFragment prompt for
     * geolocation from ScanFrag. This is done so that the all
     * of the DialogFragment's appear in order.
     */
    public void promptGeolocationInScanFrag() {
        // Prompt geolocation if user saves or discards picture
        promptGeolocation();
    }

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    // For view
    Button scanButton;
    TextView resultText;

    // An ActivityResultLauncher used for getting scan results
    // https://github.com/journeyapps/zxing-android-embedded
    // ZXing Android Embedded has Apache 2.0 License
    private final ActivityResultLauncher<ScanOptions> fragmentLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    // User exited scanner
                    Toast.makeText(getContext(), "Exiting scanner...", Toast.LENGTH_SHORT).show();
                } else {
                    // User scanned something

                    // Generate for QR code characteristics
                    code = new QRCode(result.getContents());

                    // Save code if user doesn't have, otherwise don't save
                    trySaveCodeToUser();
                }
            });

    // An ActivityResultLauncher used for getting camera results
    public ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Initial conditions are for user closing the camera
                Bitmap picture = null; // No picture
                Boolean success = false; // Picture should not be displayed

                // User took a picture
                if (result.getData() != null) {
                    // Get bitmap
                    Intent data = result.getData();
                    picture = (Bitmap) data.getExtras().get("data");

                    // Picture should be displayed
                    success = true;
                }

                // Attempt to display picture
                tryDisplayingPicture(picture, success);
            }
    );

    /**
     * Constructor for the ScanFragment
     */
    public ScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanFragment newInstance(String param1, String param2) {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Runs a set of instructions on creation.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Runs a set of instructions on view creation, including data
     * instantiation and opening up the scanner.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Instantiate user data
        user = user.getInstance();

        // Instantiate data from database
        userCodes = user.getUserCodesRef();
        dbCodes = db.collection("QRCodes");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        // Get IDs for elements in View
        scanButton = view.findViewById(R.id.scanButton);
        resultText = view.findViewById(R.id.resultText);

        // Open up scanner from start
        startScan();

        // Allow user to scan again if they exit
        scanButton.setOnClickListener(v -> startScan());

        return view;
    }

    /**
     * A set of instructions run on view creation.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    /**
     * A function that tries to save QR codes to a user's profile. If a user
     * has a code already, the code is not saved. If a user doesn't have the
     * code already, the code is saved. Users' QR codes are tracked in a
     * database.
     */
    private void trySaveCodeToUser() {
        // https://firebase.google.com/docs/firestore/query-data/get-data#java_2
        DocumentReference docRef = userCodes.document(code.getHashValue());

        // Check if user has code already
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // If user has it, don't save it, but show code characteristics
                        displayCodeAndPromptPicture(true);

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        // If user doesn't have it, save it (associate code with user)
                        Map<String, Object> newCode = new HashMap<>();
                        newCode.put("name", code.getName());
                        newCode.put("score", code.getScore());
                        newCode.put("hash", code.getHashValue());

                        user.addUserCode(code.getHashValue(), newCode);

                        // Save it again (but associate user with code this time)
                        saveCodeToDb();

                        // Show code
                        displayCodeAndPromptPicture(false);

                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    /**
     * A function that saves QR codes to the database. This is done to keep
     * track of QR code-specific information, like which QR codes are owned by
     * which users, geolocations, and pictures.
     */
    private void saveCodeToDb() {
        // Almost same code as saveCodeToUser()
        DocumentReference docRef = dbCodes.document(code.getHashValue());

        // Check if code exists in db
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // If code exists, save user to code
                        saveUserToCode();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        // If code doesn't exist, save it to db
                        Map<String, Object> newCode = new HashMap<>();
                        newCode.put("name", code.getName());
                        newCode.put("score", code.getScore());
                        newCode.put("hash", code.getHashValue());
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

                        // Code exists in db now, so save user to code
                        saveUserToCode();

                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * A function that saves/associates users to QR codes in the database.
     * This is done to keep track of which QR codes belong to who. In other
     * words, it allows for QR codes to be owned by multiple users.
     */
    private void saveUserToCode() {
        // Function only accessed if user isn't associated with code
        // Check already done in trySaveCodeToUser(), no need to do check again

        DocumentReference docRef = dbCodes.document(code.getHashValue())
                .collection("Users").document(user.getUserPhoneID());

        // Associate user to code
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("username", user.getUsername());
        docRef.set(newUser)
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

    /**
     *  This function sets up and opens the scanner.
     */
    private void startScan() {
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);
        options.setBeepEnabled(false);
        options.setPrompt("Scan a QR Code!");
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        fragmentLauncher.launch(options);
    }

    /**
     *  This function opens the camera.
     */
    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        launcher.launch(camera);
    }

    /**
     * This function displays the code characteristics and prompts
     * the user if they want to take a picture.
     * @param isSeen Whether or not the user owns the code already.
     */
    private void displayCodeAndPromptPicture(boolean isSeen) {
        // Display dialog fragment for QR code characteristics and picture prompt
        DisplayCodePromptPictureFragment frag = new DisplayCodePromptPictureFragment(code, isSeen);
        frag.setScanFragment(ScanFragment.this);
        frag.show(getActivity().getSupportFragmentManager(), "Display Code and Prompt Picture");
    }

    /**
     * A function that attempts to display the newly taken picture.
     * @param picture The newly taken picture from the camera.
     * @param success Whether or not a picture was taken.
     */
    private void tryDisplayingPicture(Bitmap picture, Boolean success) {
        // If user took a picture, display it
        if (success) {
            SavePictureFragment frag = new SavePictureFragment(picture, code);
            frag.setScanFragment(ScanFragment.this);
            frag.show(getActivity().getSupportFragmentManager(),"Save picture");
        } else {
            // If user didn't take a picture, don't display it
            Toast.makeText(getContext(), "Exiting camera...", Toast.LENGTH_SHORT).show();
            promptGeolocation();
        }
    }

    /**
     * A function that opens PromptGeolocationFragment, which
     * is a DialogFragment.
     */
    private void promptGeolocation() {
        PromptGeolocationFragment frag = new PromptGeolocationFragment(code);
        frag.show(getActivity().getSupportFragmentManager(), "Prompt geolocation");
    }
}