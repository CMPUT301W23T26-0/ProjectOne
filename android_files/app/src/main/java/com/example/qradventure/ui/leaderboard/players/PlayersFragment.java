package com.example.qradventure.ui.leaderboard.players;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qradventure.R;
import com.example.qradventure.qrcode.QRCode;
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
import java.util.Map;

/**
 * This class allows for the profiles of players (that aren't the
 * current user) to be displayed
 */
public class PlayersFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView qrCodeList;
    private Button sortButton;
    private PlayersQRListAdapter qrCodeAdapter;
    private ArrayList<QRCode> qrCodeDataList;

    /**
     * Constructor for PlayersFragment
     */
    public PlayersFragment() {
        // Required empty public constructor
    }

    /**
     * A function that runs a set of instructions upon creating the fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * A function that runs a set of instructions upon creating the view
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The newly created view
     */
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

    /**
     * A function that runs a set of instructions after the view is created
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText searchBar = view.findViewById(R.id.profile_search);
        searchBar.setVisibility(View.GONE);

        Bundle args = getArguments();
        assert args != null;
        TextView username = view.findViewById(R.id.username_text);
        String userClicked = args.getString("username");
        username.setText(userClicked);

        // Sets the profile image
        ImageView profilePic = view.findViewById(R.id.profile_image);
        profilePic.setImageDrawable(UserDataClass.getInstance().generateUserIcon(getContext(), userClicked));

        Query clickedPlayer = db.collection("Users")
                .whereEqualTo("username", userClicked);

        clickedPlayer.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
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
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        sortButton.setText(sortButton.getText() == "V" ? "Ʌ" : "V");
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.reverse(qrCodeDataList);
                qrCodeList.setAdapter(qrCodeAdapter);
                sortButton.setText(sortButton.getText() == "Ʌ" ? "V" : "Ʌ");
            }
        });

        // RecyclerView adapter and linear layout
        qrCodeList.setAdapter(qrCodeAdapter);
        qrCodeList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    /**
     * Updates the score highlights in the view
     * @param view The view whose score highlights are to be updated
     */
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
