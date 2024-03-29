package com.example.qradventure.ui.profiles;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qradventure.qrcode.QRCode;
import com.example.qradventure.R;
import com.example.qradventure.qrcode.QRController;
import com.example.qradventure.ui.leaderboard.players.PlayersFragment;
import com.example.qradventure.users.UserDataClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

/**
 * This class allows the user's profile to be displayed
 */
public class ProfileFragment extends Fragment {
    // Data
    private UserDataClass user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCodes;
    private CollectionReference dbCodes;
    private RecyclerView qrCodeList;
    private Button sortButton;
    private boolean viewingUser;
    private EditText profileSearchBar;
    private ProfilesListArrayAdapter qrCodeAdapter;
    private ArrayList<QRCode> qrCodeDataList;
    private TextView deleteInfo;
    private ImageView profilePic;

    /**
     * Constructor for the ProfileFragment
     */
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
        return new ProfileFragment();
    }

    /**
     * This function runs a set of instructions upon fragment
     * creation.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This function runs a set of instructions upon
     * view creation, which includes data instantiation
     * and list adapter set up.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The newly created View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // data instantiation
        user = user.getInstance();
        userCodes = user.getUserCodesRef();
        dbCodes = db.collection("QRCodes");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        qrCodeList = view.findViewById(R.id.user_qr_code_list);
        sortButton = view.findViewById(R.id.sort_profileqr_button);
        profileSearchBar = view.findViewById(R.id.profile_search);
        qrCodeDataList = new ArrayList<>();
        qrCodeAdapter = new ProfilesListArrayAdapter(getContext(), qrCodeDataList);
        qrCodeList.setAdapter(qrCodeAdapter);
        deleteInfo = view.findViewById(R.id.delete_text);
        profilePic = view.findViewById(R.id.profile_image);
        //qrCodeList.addOnItemTouchListener(this);
        return view;
    }

    /**
     * This function runs a set of instructions after the view
     * has been created, which includes populating the QR code datalist.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // https://firebase.google.com/docs/firestore/query-data/queries
        // Populate profile using database
        this.viewingUser = true;
        displayProfileInfo(userCodes, user.getUsername(), view);

        // RecyclerView adapter and linear layout
        qrCodeList.setAdapter(qrCodeAdapter);
        qrCodeList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        sortButton.setText(sortButton.getText() == "V" ? "Ʌ" : "V");
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
        ItemTouchHelper.SimpleCallback itemTouchHelperUser = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // Disables swiping when viewing other's profiles
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!viewingUser) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (viewingUser) {
                    // Remove QR code from profile
                    int index = viewHolder.getAdapterPosition();
                    QRCode codeToRemove = qrCodeDataList.get(index);
                    qrCodeDataList.remove(index);
                    updateScoreHighlights(view);
                    qrCodeAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                    // https://firebase.google.com/docs/firestore/manage-data/delete-data
                    // Remove QR code from user in db
                    user.deleteUserCode(codeToRemove);

                    // Remove user from qr code in db
                    dbCodes.document(codeToRemove.getHashValue())
                            .collection("Users").document(user.getUserPhoneID())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting document", e);
                                }
                            });
                }
            }
        };

        new ItemTouchHelper(itemTouchHelperUser).attachToRecyclerView(qrCodeList);

        // Responsible for search bar actions
        profileSearchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d("TEST RESPONSE", "Action ID = " + actionId + "KeyEvent = " + event);

                // Without check, gets called twice
                if (event != null) {
                    searchProfile(v.getText().toString(), view);
                }
                return false;
            }
        });

    }

    /**
     * This function updates the score highlights.
     * @param view The view to be updated
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

    /**
     * This function searches for a user with the given username in the database,
     * calls displayProfileInfo if successful, otherwise creates a Toast alert
     * @param username  String of username being searched
     * @param view      View to be passed to displayProfileInfo
     */
    public void searchProfile(String username, View view) {
        Toast searchAlert = Toast.makeText(getActivity().getApplicationContext(), "User does not exist, try again", Toast.LENGTH_SHORT);
        db.collection("Users").whereEqualTo("username", username).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                openPlayerInfo(username);
                            } else {
                                searchAlert.show();
                            }
                        }
                    }
            });

    }

    /**
     * This function displays profile information and by updating the list adapter
     * as well as the score highlights.
     * @param profileCodes  CollectionReference of the user's scanned codes
     * @param username      String of the user's username
     * @param view          View to be updated
     */
    private void displayProfileInfo(CollectionReference profileCodes, String username, View view) {
        profileCodes.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        qrCodeDataList.clear();
                        qrCodeAdapter.notifyDataSetChanged();
                        // Iterate through user codes
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Populate user profile using db
                            Map<String, Object> map = document.getData();
                            QRCode codeToAdd = new QRCode();
                            codeToAdd.setName((String) map.get("name"));
                            // https://stackoverflow.com/questions/17164014/java-lang-classcastexception-java-lang-long-cannot-be-cast-to-java-lang-integer
                            codeToAdd.setScore(((Long) map.get("score")).intValue());
                            codeToAdd.setHashValue((String) map.get("hash"));
                            qrCodeDataList.add(codeToAdd);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }

                        TextView usernameText = (TextView) view.findViewById(R.id.username_text);
                        usernameText.setText(username);

                        // Set profile image using user data (not required yet)
                        // ImageView userImg = view.findViewById(R.id.profile_image);
                        // userImg.setImageDrawable(new QRController().generateImage(getContext(), android_id));

                        // Updates need to be done in this scope
                        qrCodeAdapter.notifyDataSetChanged();
                        updateScoreHighlights(view);

                        // Sort using comparison getScore
                        qrCodeDataList.sort(Comparator.comparing(QRCode::getScore));
                        Collections.reverse(qrCodeDataList);

                        // Sets the delete text to visible only if the viewed profile is owners' profile
                        deleteInfo.setVisibility(View.VISIBLE);

                        // Sets the profile image
                        profilePic.setImageDrawable(user.generateUserIcon(getContext(), username));

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }

    /**
     * Opens a player's profile after a successful search
     * @param username The username of a given player
     */
    private void openPlayerInfo(String username) {
        Bundle args = new Bundle();
        args.putString("username", username);

        PlayersFragment playersFragment = new PlayersFragment();
        playersFragment.setArguments(args);
        FragmentActivity activity = (FragmentActivity) getContext();
        FragmentManager manager = activity.getSupportFragmentManager();

        manager.beginTransaction()
                .replace(R.id.fragments, playersFragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }
}