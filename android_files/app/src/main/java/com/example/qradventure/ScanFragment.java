package com.example.qradventure;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFragment extends Fragment {
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
//                    resultText.setText(result.getContents()); // debug
                    // Generate for QR code characteristics
                    QRCode code = new QRCode(result.getContents());

                    // Check if user has it already
                    boolean isSeen = true;

                    // Display all fragments in order (code -> pic prompt -> geo prompt)
                    displayPromptFragments(code, isSeen);
                }
            });

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

    // Sets up and opens scanner
    private void startScan() {
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);
        options.setBeepEnabled(false);
        options.setPrompt("Scan a QR Code!");
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        fragmentLauncher.launch(options);
    }

    private void displayPromptFragments(QRCode code, boolean isSeen) {
        // Display QR code dialog fragment
        ScanDisplayCodeFragment frag = new ScanDisplayCodeFragment(code, isSeen);
        frag.show(getActivity().getSupportFragmentManager(), "View QR Code Result");
    }


}