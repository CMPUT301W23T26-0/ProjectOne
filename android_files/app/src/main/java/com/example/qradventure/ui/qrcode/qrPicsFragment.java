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
import com.example.qradventure.qrcode.QRController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This fragment allows pictures of QR code locations to be displayed
 */
public class qrPicsFragment extends Fragment {
    // data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbCodePics;

    private RecyclerView qrPicsList;
    private qrPicsArrayAdapter qrPicsAdapter;
    private ArrayList<Bitmap> qrPicsDataList;
    private String hash;

    /**
     * The constructor for the qrPicsFragment class
     */
    public qrPicsFragment() {
        // Required empty public constructor
    }

    /**
     * A function that runs a set of instructions upon class creation
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    /**
     * A function that runs a set of instructions upon view creation
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_pics, container, false);

        // Get bundle information
        Bundle args = getArguments();
        assert args != null;
        hash = args.getString("hash");

        // Set QR code's name in text
        TextView qr_pics_text = view.findViewById(R.id.qr_pics_text);
        QRController codeController = new QRController();
        String string = String.format("Pictures of %s's location", codeController.generateName(hash));
        qr_pics_text.setText(string);

        // Instantiate data
        dbCodePics = db.collection("QRCodes").document(hash).collection("Pictures");

        // Set up view
        qrPicsList = view.findViewById(R.id.qr_pics_list);
        qrPicsDataList = new ArrayList<>();
        qrPicsAdapter = new qrPicsArrayAdapter(getContext(), qrPicsDataList);
        qrPicsList.setAdapter(qrPicsAdapter);

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
        qrPicsList.setAdapter(qrPicsAdapter);
        qrPicsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Get images from database
        dbCodePics.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // Iterate through code's pictures and populate the list in qrPicsFragment
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convert saved strings into bitmaps
                            // https://stackoverflow.com/questions/23005948/convert-string-to-bitmap
                            String encodedPicDataString = (String) document.getData().get("picture");
                            byte[] encodedPicDataBytes = Base64.decode(encodedPicDataString, Base64.URL_SAFE);
                            Bitmap picToAdd = BitmapFactory.decodeByteArray(encodedPicDataBytes, 0, encodedPicDataBytes.length);

                            // Place image into the data list
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
