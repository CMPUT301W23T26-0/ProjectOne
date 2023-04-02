package com.example.qradventure.ui.leaderboard.players;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qradventure.R;
import com.example.qradventure.qrcode.QRCode;
import com.example.qradventure.qrcode.QRController;
import com.example.qradventure.ui.profiles.ProfilesListArrayAdapter;
import com.example.qradventure.ui.qrcode.qrFragment;

import java.util.ArrayList;

/**
 * This class allows QR codes to be displayed in PlayersFragment
 */
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

    /**
     * A function that runs a set of instructions upon creating the view holder
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return The newly created view holder
     */
    @Override
    public ProfilesListArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_qrcontent, parent, false);
        ProfilesListArrayAdapter.ViewHolder viewHolder = new ProfilesListArrayAdapter.ViewHolder(view);
        return viewHolder;
    }

    /**
     * A function that runs a set of instructions upon binding a given ViewHolder
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ProfilesListArrayAdapter.ViewHolder holder, int position) {
        // Allow QR codes to be viewed
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRCode qrCode = qrCodes.get(position);

                qrFragment frag = new qrFragment();
                Bundle args = new Bundle();
                args.putString("hash", qrCode.getHashValue());
                frag.setArguments(args);

                FragmentActivity activity = (FragmentActivity) context;
                FragmentManager manager = activity.getSupportFragmentManager();

                manager.beginTransaction()
                        .replace(R.id.fragments, frag)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });

        QRCode qrCode = qrCodes.get(position);
        QRController qrController = new QRController();

        holder.qrCodeTitle.setText(qrCode.getName());
        holder.qrCodeScore.setText(Integer.toString(qrCode.getScore()));
        holder.qrImage.setImageDrawable(qrController.generateImage(context, qrCode.getHashValue()));
    }

    /**
     * A function that returns the data list size.
     * @return Size of the data list
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
         * @param view The view to be updated
         */
        public ViewHolder(View view) {
            super(view);
            this.qrCodeTitle = view.findViewById(R.id.qr_title);
            this.qrCodeScore = view.findViewById(R.id.qr_score_value);
            this.qrImage = view.findViewById(R.id.qr_image);
        }
    }
}
