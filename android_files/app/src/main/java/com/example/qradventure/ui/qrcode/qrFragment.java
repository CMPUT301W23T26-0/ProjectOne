package com.example.qradventure.ui.qrcode;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qradventure.R;
import com.example.qradventure.qrcode.QRController;
import com.example.qradventure.users.UserDataClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class qrFragment extends Fragment {
    private ImageView img;
    private TextView qrName;
    private TextView qrScore;
    private Button addButton;
    private EditText editText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView comments;
    private ArrayList<Comment> commentList = new ArrayList<>();
    private CommentListAdapter listAdapter;
    final String TAG = "QR Fragment";
    private Button picsButton;
    private TextView qrPlayersText;

    public qrFragment() {
        // Required empty public constructor
    }

    public static qrFragment newInstance(String param1, String param2) {
        qrFragment fragment = new qrFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        //--- Get bundle information
        Bundle args = getArguments();
        assert args != null;
        String hash = args.getString("hash");

        //--- initialize views to qr code items
        UserDataClass user = UserDataClass.getInstance();
        qrName = view.findViewById(R.id.qr_title);
        qrScore = view.findViewById(R.id.qr_score_value);
        img = view.findViewById(R.id.qr_image);

        //--- get info from database
        final DocumentReference code = db.collection("QRCodes").document(hash);
        code.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                QRController qrController = new QRController();
                qrName.setText(documentSnapshot.getString("name"));
                qrScore.setText(Integer.toString(documentSnapshot.getLong("score").intValue()));
                img.setImageDrawable(qrController.generateImage(img.getContext(), documentSnapshot.getString("hash")));
                Log.d(TAG, "Successfully set qr code info");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Successfully set qr code info");
            }
        });

        //-- comments
        comments = view.findViewById(R.id.comments);
        listAdapter = new CommentListAdapter(this.getContext(), commentList);
        comments.setAdapter(listAdapter);
        addButton = view.findViewById(R.id.comment_button);
        editText = view.findViewById(R.id.comment_edit);

        final CollectionReference commentDB = code.collection("Comments");

        // Pics
        picsButton = view.findViewById(R.id.pics_button);

        // Text to show who else has scanned this QR code
        qrPlayersText = view.findViewById(R.id.qr_players_text);

        // Update qrPlayersText using db
        code.collection("Users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Some user owns this QR code
                            String username;
                            String[] qrPlayers = new String[2];
                            Boolean isOwned = false;
                            int excessPlayerCount = 0;
                            int index = 0;
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Map<String, Object> users = document.getData();
                                username = (String) users.get("username");
                                if (username.equals(user.getUsername())) {
                                    // Check if current player owns code
                                    isOwned = true;
                                } else if (index < 2) {
                                    // Display a max of 2 other players
                                    qrPlayers[index] = username;
                                    index++;
                                } else {
                                    // Once we find 2 others, start counting excess
                                    excessPlayerCount++;
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            // Format outputText
                            String outputText;
                            if (isOwned) {
                                // Current player scanned it
                                if (index == 0) {
                                    // No other players scanned it
                                    outputText = "Scanned by only you.";
                                } else if (index == 1) {
                                    // One other player scanned it
                                    outputText = String.format("Scanned by you and %s.",
                                            qrPlayers[0]);
                                } else if (index == 2) {
                                    if (excessPlayerCount == 0) {
                                        // Only 2 players scanned it
                                        outputText = String.format("Scanned by you, %s, and %s.",
                                                qrPlayers[0], qrPlayers[1]);
                                    } else {
                                        // More than 2 players scanned it
                                        outputText = String.format("Scanned by you, %s, %s, and %d other(s).",
                                                qrPlayers[0], qrPlayers[1], excessPlayerCount);
                                    }
                                } else {
                                    // Should not be reached
                                    outputText = "Error";
                                }
                            } else {
                                // Current player did not scan it
                                if (index == 0) {
                                    // No other players scanned it
                                    outputText = "Not scanned by anyone.";
                                } else if (index == 1) {
                                    // One other player scanned it
                                    outputText = String.format("Scanned by %s.",
                                            qrPlayers[0]);
                                } else if (index == 2) {
                                    if (excessPlayerCount == 0) {
                                        // Only 2 players scanned it
                                        outputText = String.format("Scanned by %s and %s.",
                                                qrPlayers[0], qrPlayers[1]);
                                    } else {
                                        // More than 2 players scanned it
                                        outputText = String.format("Scanned by %s, %s, and %d other(s).",
                                                qrPlayers[0], qrPlayers[1], excessPlayerCount);
                                    }
                                } else {
                                    // Should not be reached
                                    outputText = "Error";
                                }
                            }

                            qrPlayersText.setText(outputText);

                        } else {
                            // No users own this QR code or error occurs
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            qrPlayersText.setText("Not scanned by anyone.");
                        }
                    }
                });

        // Add comments
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String contents = editText.getText().toString();
                HashMap<String, String> data = new HashMap<>();
                if (contents.length() > 0) {
                    data.put("author", user.getUsername());
                    data.put("contents", contents);
                    commentDB.document(String.valueOf(commentList.size()))
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "Data added successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Data couldn't be added" + e.toString());
                                }
                            });
                }
                editText.setText("");
            }
        });

        picsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Put arguments into fragment
                qrPicsFragment frag = new qrPicsFragment();
                Bundle args = new Bundle();
                args.putString("hash", hash);
                frag.setArguments(args);

                // Change the current fragment to display the qrPicsFragment
                // https://developer.android.com/guide/fragments/fragmentmanager#java
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragments, frag)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Update the list
        commentDB.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                commentList.clear();
                for (QueryDocumentSnapshot doc: value) {
                    Log.d(TAG, doc.getId());
                    commentList.add(new Comment(doc.getString("author"), doc.getString("contents")));
                }
                listAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}