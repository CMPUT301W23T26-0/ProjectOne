package com.example.qradventure;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        View fragmentScan = inflater.inflate(R.layout.fragment_scan, container, false);

        // Find TextViews (Used for visual check on our example)
        sha256_text = fragmentScan.findViewById(R.id.sha256_sample);
        qr_value_text = fragmentScan.findViewById(R.id.qr_value);

        return fragmentScan;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Once view is created, we set a sample sha256 from the problem description
        // SetText for visual, then compute the value using a function
        sha256_test_string = "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6  -";
        sha256_text.setText(sha256_test_string);

        Double qr_score = calculateScore(sha256_test_string);
        qr_value_text.setText(String.format(String.valueOf(qr_score)));
    }

    public Double calculateScore(String content){
        // Calculates value from sha256 string
        // Can be improved later on, but for now it works
        StringBuilder valuez = new StringBuilder();
        Double value = 0d;
        int streak = 1;
        for (int i = 1; i < content.length()-1; i++) {
            if (content.charAt(i) == content.charAt(i-1) && content.charAt(i) != ' ') {
                streak += 1;
            }
            else if (streak > 1) {
                valuez.append(content.charAt(i-1));
                Character zero = '0';
                if (content.charAt(i-1) == '0') {
                    value += Math.pow(20, streak-1);
                } else {
                    value += Math.pow(Integer.parseInt(String.valueOf(content.charAt(i-1)), 16), streak - 1);
                }
                streak = 1;
            }
            else {
                streak = 1;
            }
        }
        return value;
    }
}