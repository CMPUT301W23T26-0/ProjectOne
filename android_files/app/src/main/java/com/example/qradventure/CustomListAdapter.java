package com.example.qradventure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder>{
    Context context;

    private ArrayList<QRCode> qrCodes;

    // RecyclerView recyclerView;
    public CustomListAdapter(Context context, ArrayList<QRCode> qrCodes) {
        this.context = context;
        this.qrCodes = qrCodes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_qrcontent, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QRCode qrCode = qrCodes.get(position);
        QRController qrController = new QRController();

        holder.qrCodeTitle.setText(qrCode.getName());
        holder.qrCodeComment.setText(qrCode.getComment());
        holder.qrCodeScore.setText(Integer.toString(qrCode.getScore()));
        holder.qrImage.setImageDrawable(qrController.generateImage(context, qrCode.getHashValue()));
    }


    @Override
    public int getItemCount() {
        return qrCodes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qrCodeTitle;
        public TextView qrCodeComment;
        public TextView qrCodeScore;
        public ImageView qrImage;
        public ViewHolder(View view) {
            super(view);
            this.qrCodeTitle = view.findViewById(R.id.qr_title);
            this.qrCodeComment = view.findViewById(R.id.qr_comment);
            this.qrCodeScore = view.findViewById(R.id.qr_score_value);
            this.qrImage = view.findViewById(R.id.qr_image);
        }
    }
}