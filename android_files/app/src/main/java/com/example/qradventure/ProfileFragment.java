package com.example.qradventure;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    ListView qrCodeList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        qrCodeList = view.findViewById(R.id.user_qr_code_list);
        qrCodeDataList = new ArrayList<>();
        qrCodeAdapter = new CustomList(getContext(), qrCodeDataList);
        qrCodeList.setAdapter(qrCodeAdapter);
        return view;
    }

    CustomList qrCodeAdapter;
    ArrayList<QRCode> qrCodeDataList;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        QRCode test = new QRCode("Cool QR Code Name", (int) 69, "This is a comment on a QR Code.", "", "");
        qrCodeDataList.add(test);
        QRCode test1 = new QRCode("Another QR Code", (int) 420, "This is a another comment on a QR Code.", "", "");
        qrCodeDataList.add(test1);
        updateScoreHighlights(view);
        qrCodeList.setAdapter(qrCodeAdapter);
    }

    public void updateScoreHighlights(View view) {
        TextView totalScore = view.findViewById(R.id.total_score_value);
        TextView highestScore = view.findViewById(R.id.highest_score_value);
        TextView lowestScore = view.findViewById(R.id.lowest_score_value);

        if (qrCodeDataList.size() < 1) {
            totalScore.setText("0");
            highestScore.setText("0");
            lowestScore.setText("0");
        } else {
            int sumTotal = 0;
            int highest = 0;
            int lowest = 2147483647;
            for (int i = 0; i < qrCodeDataList.size(); i++) {
                QRCode qrCode = qrCodeDataList.get(i);
                int score = qrCode.getScore();
                sumTotal += score;
                if (score > highest) {highest = score;}
                if (score < lowest) {lowest = score;}
            }
            totalScore.setText(Integer.toString(sumTotal));
            highestScore.setText(Integer.toString(highest));
            lowestScore.setText(Integer.toString(lowest));
        }
    }
}