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
                    Toast.makeText(getContext(), "Exiting scanner...", Toast.LENGTH_LONG).show();
                } else {
                    // Successful scans

                    // Generate for QR code characteristics
                    code = new QRCode(result.getContents());

                    // Save code if user doesn't have, otherwise don't save
                    handleCode(); // updates userHasCode
                }
            });

    public ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Get bitmap
                Intent data = result.getData();
                Bitmap picture = (Bitmap) data.getExtras().get("data");

                // Open bitmap with save picture frag
                displayPicture(picture);
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
        userCodes = db
                .collection("Users").document(user.getUserPhoneID())
                .collection("Codes");

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

    private void handleCode() {
        // https://firebase.google.com/docs/firestore/query-data/get-data#java_2
        DocumentReference docRef = userCodes.document(code.getHashValue());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // If user has it, don't save and update flag
                        displayCodeAndPromptPicture(true);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        // If user doesn't have it, save and update flag
                        Map<String, Object> newCode = new HashMap<>();
                        newCode.put(code.getHashValue(), 1); // Object value doesn't matter
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
                        displayCodeAndPromptPicture(false);
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
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

    private void displayCodeAndPromptPicture(boolean isSeen) {
        // Display QR code dialog fragment
        DisplayCodePromptPictureFragment frag = new DisplayCodePromptPictureFragment(code, isSeen);
        frag.setScanFragment(ScanFragment.this);
        frag.show(getActivity().getSupportFragmentManager(), "Display Code and Prompt Picture");
    }

    private void displayPicture(Bitmap picture) {
        SavePictureFragment frag = new SavePictureFragment(picture);
        frag.setScanFragment(ScanFragment.this);
        frag.show(getActivity().getSupportFragmentManager(),"Save picture");
    }

    private void promptGeolocation() {
        PromptGeolocationFragment frag = new PromptGeolocationFragment(code);
        frag.show(getActivity().getSupportFragmentManager(), "Prompt geolocation");
    }

    public void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        launcher.launch(camera);
    }
}