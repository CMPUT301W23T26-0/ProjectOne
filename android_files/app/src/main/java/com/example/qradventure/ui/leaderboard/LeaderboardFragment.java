package com.example.qradventure.ui.leaderboard;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.qradventure.users.UserDataClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import com.example.qradventure.R;

/**
 * This class allows the leaderboard to be displayed
 */
public class LeaderboardFragment extends Fragment {
    private UserDataClass user;
    private RadioGroup leaderboardToggler;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView playerList;
    private PlayerListAdapter playerAdapter;
    private ArrayList<Player> playerDataList;

    /**
     * Constructor for leaderboard fragment
     */
    public LeaderboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeaderboardFragment.
     */
    public static LeaderboardFragment newInstance(String param1, String param2) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        return fragment;
    }

    /**
     * This function runs a set of instructions upon fragment creation
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This function runs a set of instructions upon view creation
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
        user = user.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        leaderboardToggler = view.findViewById(R.id.leaderboard_toggle);
        playerList = view.findViewById(R.id.player_list);
        playerDataList = new ArrayList<>();
        playerAdapter = new PlayerListAdapter(getContext(), playerDataList);
        playerList.setAdapter(playerAdapter);
        playerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    /**
     * This function runs a set of instructions after the view
     * has been created, which includes querying the top total scorers
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        leaderboardToggler.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Query topScorers;
                String field;

                if (checkedId == R.id.QRCode_toggle) {
                    field = "highestQrScore";
                } else {
                    field = "totalScore";
                }

                topScorers = db.collection("Users").orderBy(field, Query.Direction.DESCENDING);
                topScorers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            playerDataList.clear();
                            // Iterate through user codes
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                // Populate user profile using db
                                Map<String, Object> playerInfo = document.getData();
                                Player player = new Player();
                                player.setName(playerInfo.get("username").toString());
                                player.setScore(Integer.parseInt(playerInfo.get(field).toString()));
                                playerDataList.add(player);
                            }

                            playerAdapter.notifyDataSetChanged();
                            // Updates need to be done in this scope
                            updateTopPlayers(view);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });

        leaderboardToggler.check(R.id.Total_toggle);
    }

    /**
     * This function updates the top three user highlights
     * @param view The view to be updated
     */
    public void updateTopPlayers(View view) {
        TextView first = view.findViewById(R.id.first_place);
        TextView second = view.findViewById(R.id.second_place);
        TextView third = view.findViewById(R.id.third_place);

        ImageView firstImg = view.findViewById(R.id.imageView);
        ImageView secondImg = view.findViewById(R.id.imageView2);
        ImageView thirdImg = view.findViewById(R.id.imageView3);

        TextView[] topThree = new TextView[]{first, second, third};
        ImageView[] topThreeImg = new ImageView[]{firstImg, thirdImg, secondImg};

        int min;
        if (playerAdapter.getItemCount() < 3) {
            min = playerAdapter.getItemCount();
        } else {
            min = 3;
        }
        for (int i = 0; i < min; i++) {
            topThree[i].setText(playerDataList.get(i).getName());
            topThreeImg[i].setImageDrawable(UserDataClass.getInstance().generateUserIcon(getContext(), playerDataList.get(i).getName()));
        }
    }
}