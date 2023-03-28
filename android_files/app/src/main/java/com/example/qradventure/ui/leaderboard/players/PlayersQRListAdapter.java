package com.example.qradventure.ui.leaderboard.players;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.qradventure.R;
import com.example.qradventure.qrcode.QRCode;
import com.example.qradventure.qrcode.QRController;
import com.example.qradventure.ui.profiles.ProfilesListArrayAdapter;

import java.util.ArrayList;

public class PlayersQRListAdapter extends RecyclerView.Adapter<ProfilesListArrayAdapter.ViewHolder>{
    private Context context;
    private ArrayList<QRCode> qrCodes;

    /**
     * A constructor for CustomListAdapter.
     * @param context The context of the data list
     * @param qrCodes The data list of QR codes to be adapted
     */
    public PlayersQRListAdapter(Context context, ArrayList<QRCode> qrCodes) {
        this.context = context;
        this.qrCodes = qrCodes;
    }

    @Override
    public ProfilesListArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_qrcontent, parent, false);
        ProfilesListArrayAdapter.ViewHolder viewHolder = new ProfilesListArrayAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProfilesListArrayAdapter.ViewHolder holder, int position) {
        QRCode qrCode = qrCodes.get(position);
        QRController qrController = new QRController();

        holder.qrCodeTitle.setText(qrCode.getName());
        holder.qrCodeScore.setText(Integer.toString(qrCode.getScore()));
        holder.qrImage.setImageDrawable(qrController.generateImage(context, qrCode.getHashValue()));
    }

    /**
     * A function that returns the data list size.
     * @return
     */
    @Override
    public int getItemCount() {
        return qrCodes.size();
    }

    /**
     * This class is used to update the view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qrCodeTitle;
        public TextView qrCodeScore;
        public ImageView qrImage;

        /**
         * A function that updates the view.
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            this.qrCodeTitle = view.findViewById(R.id.qr_title);
            this.qrCodeScore = view.findViewById(R.id.qr_score_value);
            this.qrImage = view.findViewById(R.id.qr_image);
        }
    }
}
