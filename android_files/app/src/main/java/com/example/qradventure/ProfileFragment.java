package com.example.qradventure;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // Data
    private UserDataClass user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCodes;
    private CollectionReference dbCodes;

    private RecyclerView qrCodeList;
    private Button sortButton;
    private CustomListAdapter qrCodeAdapter;
    private ArrayList<QRCode> qrCodeDataList;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = user.getInstance();

        userCodes = db.collection("Users").document(user.getUserPhoneID())
                .collection("Codes");

        dbCodes = db.collection("QRCodes");

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

        // https://firebase.google.com/docs/firestore/query-data/queries
        // Populate profile using database
        userCodes.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
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

        // Set username using user data
        String username = user.getUsername();
        TextView usernameText = (TextView) view.findViewById(R.id.username_text);
        usernameText.setText(username);

        // Set profile image using user data (not required yet)
        //ImageView userImg = view.findViewById(R.id.profile_image);
        //userImg.setImageDrawable(new QRController().generateImage(getContext(), android_id));

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
                // Remove QR code from profile
                int index = viewHolder.getAdapterPosition();
                QRCode codeToRemove = qrCodeDataList.get(index);
                qrCodeDataList.remove(index);
                updateScoreHighlights(view);
                qrCodeAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                // https://firebase.google.com/docs/firestore/manage-data/delete-data
                // Remove QR code from user in db
                userCodes.document(codeToRemove.getHashValue())
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
        };

        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(qrCodeList);
    }

    // Updates scores texts
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