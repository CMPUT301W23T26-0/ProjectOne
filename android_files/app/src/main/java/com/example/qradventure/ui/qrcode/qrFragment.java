package com.example.qradventure.ui.qrcode;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

        picsButton = view.findViewById(R.id.pics_button);

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
                qrPicsFragment frag = new qrPicsFragment();

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