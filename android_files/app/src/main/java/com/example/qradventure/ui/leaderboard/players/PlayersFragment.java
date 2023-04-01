package com.example.qradventure.ui.leaderboard.players;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qradventure.R;
import com.example.qradventure.qrcode.QRCode;
import com.example.qradventure.ui.profiles.ProfileFragment;
import com.example.qradventure.ui.profiles.ProfilesListArrayAdapter;
import com.example.qradventure.users.UserDataClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PlayersFragment extends Fragment {

    private UserDataClass user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCodes;
    private CollectionReference dbCodes;
    private RecyclerView qrCodeList;
    private Button sortButton;
    private PlayersQRListAdapter qrCodeAdapter;
    private ArrayList<QRCode> qrCodeDataList;

    private CollectionReference userCodesRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public PlayersFragment() {
        // Required empty public constructor
    }

    public static PlayersFragment newInstance(String param1, String param2) {
        PlayersFragment fragment = new PlayersFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        qrCodeList = view.findViewById(R.id.user_qr_code_list);
        sortButton = view.findViewById(R.id.sort_profileqr_button);
        qrCodeDataList = new ArrayList<>();
        qrCodeAdapter = new PlayersQRListAdapter(getContext(), qrCodeDataList);
        qrCodeList.setAdapter(qrCodeAdapter);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText searchBar = view.findViewById(R.id.profile_search);
        searchBar.setVisibility(View.GONE);

        Bundle args = getArguments();
        assert args != null;
        TextView username = view.findViewById(R.id.username_text);
        String userClicked = args.getString("username");
        username.setText(userClicked);

        CollectionReference playersCodes;
        Query clickedPlayer = db.collection("Users")
                .whereEqualTo("username", userClicked);

        clickedPlayer.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //playerID = document.getId();
                        Log.d("CLICKED_USER", document.getId() + " => " + document.getData());

                        db.collection("Users")
                                .document(document.getId())
                                .collection("Codes")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            // Iterate through user codes
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                // Populate user profile using db
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                                Map<String, Object> map = document.getData();
                                                QRCode codeToAdd = new QRCode();
                                                codeToAdd.setName((String) map.get("name"));
                                                // https://stackoverflow.com/questions/17164014/java-lang-classcastexception-java-lang-long-cannot-be-cast-to-java-lang-integer
                                                codeToAdd.setScore(((Long) map.get("score")).intValue());
                                                codeToAdd.setHashValue((String) map.get("hash"));
                                                qrCodeDataList.add(codeToAdd);
                                            }

                                            // Updates need to be done in this scope
                                            //qrCodeList.setAdapter(qrCodeAdapter);
                                            qrCodeAdapter.notifyDataSetChanged();
                                            updateScoreHighlights(view);

                                            // Sort using comparison getScore
                                            qrCodeDataList.sort(Comparator.comparing(QRCode::getScore));
                                            Collections.reverse(qrCodeDataList);
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                        //Map<String, Object> map = document.getData();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        sortButton.setText(sortButton.getText() == "V" ? "Ʌ" : "V");

        // RecyclerView adapter and linear layout
        qrCodeList.setAdapter(qrCodeAdapter);
        qrCodeList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        /*
        db.collection("Users")
                .document("480efbdbef78d266")
                .collection("Codes");


        playerCodes.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Iterate through user codes
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("QR_CODES", document.getId() + " => " + document.getData());

                        // Populate user profile using db
                        Map<String, Object> map = document.getData();
                        QRCode codeToAdd = new QRCode();
                        codeToAdd.setName((String) map.get("name"));
                        // https://stackoverflow.com/questions/17164014/java-lang-classcastexception-java-lang-long-cannot-be-cast-to-java-lang-integer
                        codeToAdd.setScore(((Long) map.get("score")).intValue());
                        codeToAdd.setHashValue((String) map.get("hash"));
                        //qrCodeDataList.add(codeToAdd);

                    }
                    // Updates need to be done in this scope
                    qrCodeAdapter.notifyDataSetChanged();
                    updateScoreHighlights(view);

                    // Sort using comparison getScore
                    qrCodeDataList.sort(Comparator.comparing(QRCode::getScore));
                    Collections.reverse(qrCodeDataList);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        */
    }

    private void updateScoreHighlights(View view) {
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
