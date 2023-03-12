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
import android.view.Display;
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
        SavePictureFragment.PictureInScanFrag{

    private UserDataClass user;
    private QRCode code;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    CollectionReference userCodes;

    CollectionReference dbCodes;

    // So fragment appears in order
    @Override
    public void openCameraInScanFrag(Boolean state) {
        if (state) {
            // Open camera and resulting picture afterwards
            openCamera();
        } else {
            // Otherwise, just prompt geolocation
            promptGeolocation();
        }
    }

    // So fragment appears in order
    public void savePictureInScanFrag(Bitmap picture, Boolean state) {
        if (state) {
            // Save picture to QRCode
        }
        // Prompt geolocation if user saves or discards picture
        promptGeolocation();
    }

    // TODO: Rename parameter arguments, choose names that match

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String sha256_test_string;

    private TextView sha256_text;
    private TextView qr_value_text;

    Button scanButton;
    TextView resultText;

    // Used for getting scan results
    // https://github.com/journeyapps/zxing-android-embedded
    private final ActivityResultLauncher<ScanOptions> fragmentLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    // User exited scan
                    Toast.makeText(getContext(), "Exiting scanner...", Toast.LENGTH_SHORT).show();
                } else {
                    // Successful scans

                    // Generate for QR code characteristics
                    code = new QRCode(result.getContents());

                    // Save code if user doesn't have, otherwise don't save
                    trySaveCodeToUser();
                }
            });

    public ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Bitmap picture = null;
                Boolean success = false;
                if (result.getData() != null) {
                    // Get bitmap
                    Intent data = result.getData();
                    picture = (Bitmap) data.getExtras().get("data");

                    success = true;
                }
                // Open bitmap with save picture frag
                displayPicture(picture, success);
            }
    );

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Instantiate user data
        user = user.getInstance();

        // Instantiate data from database
        userCodes = db.collection("Users").document(user.getUserPhoneID())
                .collection("Codes");

        dbCodes = db.collection("QRCodes");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        scanButton = view.findViewById(R.id.scanButton);
        resultText = view.findViewById(R.id.resultText);

        // Open up scanner from start
        startScan();

        // Allow user to scan again if they exit
        scanButton.setOnClickListener(v -> startScan());

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

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
                        // If user has it, don't save it and show code anyways
                        displayCodeAndPromptPicture(true);

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        // If user doesn't have it, save it (associate code with user)
                        Map<String, Object> newCode = new HashMap<>();
                        newCode.put("location", null);
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

                        // Save it again (associate user with code)
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

    private void saveUserToCode() {
        // This function is only accessed if user doesn't have code in trySaveCodeToUser()
        // Don't need to check if code has user again

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

    // Sets up and opens scanner
    private void startScan() {
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);
        options.setBeepEnabled(false);
        options.setPrompt("Scan a QR Code!");
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        fragmentLauncher.launch(options);
    }

    public void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        launcher.launch(camera);
    }

    private void displayCodeAndPromptPicture(boolean isSeen) {
        // Display QR code dialog fragment
        DisplayCodePromptPictureFragment frag = new DisplayCodePromptPictureFragment(code, isSeen);
        frag.setScanFragment(ScanFragment.this);
        frag.show(getActivity().getSupportFragmentManager(), "Display Code and Prompt Picture");
    }

    private void displayPicture(Bitmap picture, Boolean success) {
        if (success) {
            SavePictureFragment frag = new SavePictureFragment(picture);
            frag.setScanFragment(ScanFragment.this);
            frag.show(getActivity().getSupportFragmentManager(),"Save picture");
        } else {
            Toast.makeText(getContext(), "Exiting camera...", Toast.LENGTH_SHORT).show();
            promptGeolocation();
        }
    }

    private void promptGeolocation() {
        PromptGeolocationFragment frag = new PromptGeolocationFragment(code);
        frag.show(getActivity().getSupportFragmentManager(), "Prompt geolocation");
    }
}