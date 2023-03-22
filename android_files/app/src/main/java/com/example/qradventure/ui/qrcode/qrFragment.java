package com.example.qradventure.ui.qrcode;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qradventure.R;
import com.example.qradventure.qrcode.QRController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;

public class qrFragment extends Fragment {
    private ImageView img;
    private TextView qrName;
    private TextView qrScore;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        String uID = args.getString("UID");

        //--- initialize views to qr code items
        qrName = view.findViewById(R.id.qr_title);
        qrScore = view.findViewById(R.id.qr_score_value);
        img = view.findViewById(R.id.qr_image);

        //--- get info from database
        DocumentReference code = db.collection("QRCodes").document(hash);
        String TAG = "QR Fragment";
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

        return view;
    }
}