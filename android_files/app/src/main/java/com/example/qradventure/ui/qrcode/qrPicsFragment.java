package com.example.qradventure.ui.qrcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.qradventure.R;
import com.example.qradventure.qrcode.QRCode;
import com.example.qradventure.qrcode.QRController;

public class qrPicsFragment extends Fragment {

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
        String hash = args.getString("hash");

        TextView qr_pics_text = view.findViewById(R.id.qr_pics_text);
        QRController codeController = new QRController();
        String string = String.format("Pictures of %s", codeController.generateName(hash));
        qr_pics_text.setText(string);

        https://stackoverflow.com/questions/18623574/adding-list-of-images-into-a-scroll-view
        // Get images from data base

        // Place all images into a list

        // Display list

        return view;
    }
}
