package com.example.qradventure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

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
    // TODO: Rename and change types and number of parameters2
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
    RecyclerView qrCodeList;
    Button sortButton;
    CustomListAdapter qrCodeAdapter;
    ArrayList<QRCode> qrCodeDataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        qrCodeList = view.findViewById(R.id.user_qr_code_list);
        sortButton = view.findViewById(R.id.sort_profileqr_button);
        qrCodeDataList = new ArrayList<>();
        qrCodeAdapter = new CustomListAdapter(getContext(), qrCodeDataList);
        qrCodeList.setAdapter(qrCodeAdapter);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Text QR Codes
        qrCodeDataList.add(new QRCode("BFG5DGW54"));
        qrCodeDataList.add(new QRCode("Amazing ore"));
        qrCodeDataList.add(new QRCode("Amazing ore")); // testing repeatability
        qrCodeDataList.add(new QRCode("Lots of points"));
        qrCodeDataList.add(new QRCode("Not that much points"));

        updateScoreHighlights(view);

        // Username is Unique Android ID (Can be used as login)
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        TextView username = (TextView) view.findViewById(R.id.username_text);
        username.setText(android_id);

        //ImageView userImg = view.findViewById(R.id.profile_image);
        //userImg.setImageDrawable(new QRController().generateImage(getContext(), android_id));

        // Sort using comparison getScore
        qrCodeDataList.sort(Comparator.comparing(QRCode::getScore));
        Collections.reverse(qrCodeDataList);

        sortButton.setText(sortButton.getText() == "V" ? "Ʌ" : "V");

        // RecyclerView adapter and linear layout
        qrCodeList.setAdapter(qrCodeAdapter);
        qrCodeList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        // Sort Button
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.reverse(qrCodeDataList);
                qrCodeList.setAdapter(qrCodeAdapter);
                sortButton.setText(sortButton.getText() == "Ʌ" ? "V" : "Ʌ");
            }
        });

        // Responsible for swipe delete
        ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                qrCodeDataList.remove(viewHolder.getAdapterPosition());
                updateScoreHighlights(view);
                qrCodeAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };

        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(qrCodeList);
    }

    // Updates scores texts
    public void updateScoreHighlights(View view) {
        TextView totalScore = view.findViewById(R.id.total_score_value);
        TextView highestScore = view.findViewById(R.id.highest_score_value);
        TextView lowestScore = view.findViewById(R.id.lowest_score_value);
        TextView qrCount = view.findViewById(R.id.profile_qr_count);

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
        String count = "Count: " + Integer.toString(qrCodeDataList.size());
        qrCount.setText(count);
    }
}