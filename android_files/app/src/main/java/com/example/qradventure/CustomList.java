package com.example.qradventure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomList extends ArrayAdapter<QRCode> {

    private ArrayList<QRCode> qrCodes;
    private Context context;

    public CustomList(Context context, ArrayList<QRCode> qrCodes) {
        super(context, 0, qrCodes);
        this.qrCodes = qrCodes;
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.profile_qrcontent, parent, false);
        }

        QRCode qrCode = qrCodes.get(position);

        TextView qrCodeTitle = view.findViewById(R.id.qr_title);
        TextView qrCodeComment = view.findViewById(R.id.qr_comment);
        TextView qrCodeScore = view.findViewById(R.id.qr_score_value);

        qrCodeTitle.setText(qrCode.getName());
        qrCodeComment.setText(qrCode.getComment());
        qrCodeScore.setText(Integer.toString(qrCode.getScore()));

        return view;
    }
}
