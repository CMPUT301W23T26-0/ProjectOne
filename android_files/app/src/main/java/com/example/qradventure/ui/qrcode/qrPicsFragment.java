package com.example.qradventure.ui.qrcode;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qradventure.R;
import com.example.qradventure.qrcode.QRCode;
import com.example.qradventure.qrcode.QRController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class qrPicsFragment extends Fragment {
    // data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbCodePics;
    private RecyclerView qrPicsList;
    private qrListArrayAdapter qrPicsAdapter;
    private ArrayList<Bitmap> qrPicsDataList;
    private String hash;

    public qrPicsFragment() {
        // Required empty public constructor
    }

    public static qrPicsFragment newInstance(String param1, String param2) {
        qrPicsFragment fragment = new qrPicsFragment();
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
        View view = inflater.inflate(R.layout.fragment_qr_pics, container, false);

        //--- Get bundle information
        Bundle args = getArguments();
        assert args != null;
        hash = args.getString("hash");

        // Set QR code's name in text
        TextView qr_pics_text = view.findViewById(R.id.qr_pics_text);
        QRController codeController = new QRController();
        String string = String.format("Pictures of %s's location", codeController.generateName(hash));
        qr_pics_text.setText(string);

        // data instantiation
        dbCodePics = db.collection("QRCodes").document(hash).collection("Pictures");

        // Set up view
        qrPicsList = view.findViewById(R.id.qr_pics_list);
        qrPicsDataList = new ArrayList<>();
        qrPicsAdapter = new qrListArrayAdapter(getContext(), qrPicsDataList);
        qrPicsList.setAdapter(qrPicsAdapter);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        qrPicsList.setAdapter(qrPicsAdapter);
        qrPicsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Get images from data base
        dbCodePics.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // Iterate through code pictures and populate pics fragment with them
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convert saved strings into bitmaps
                            // https://stackoverflow.com/questions/23005948/convert-string-to-bitmap
                            String encodedPicDataString = (String) document.getData().get("picture");
                            byte[] encodedPicDataBytes = Base64.decode(encodedPicDataString, Base64.URL_SAFE);
                            Bitmap picToAdd = BitmapFactory.decodeByteArray(encodedPicDataBytes, 0, encodedPicDataBytes.length);

                            // Place images into a list
                            qrPicsDataList.add(picToAdd);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }

                        // Updates need to be done in this scope
                        qrPicsAdapter.notifyDataSetChanged();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
    }
}
