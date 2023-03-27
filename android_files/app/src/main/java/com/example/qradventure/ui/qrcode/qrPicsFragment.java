package com.example.qradventure.ui.qrcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.qradventure.R;

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
        return view;
    }
}
