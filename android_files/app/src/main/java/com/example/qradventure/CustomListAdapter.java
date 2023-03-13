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

/**
 * This class allows data lists to be displayed in a list view
 */
public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder>{
    // RecyclerView needs a custom adapter

    private Context context;
    private ArrayList<QRCode> qrCodes;

    /**
     * A constructor for CustomListAdapter.
     * @param context The context of the data list
     * @param qrCodes The data list of QR codes to be adapted
     */
    public CustomListAdapter(Context context, ArrayList<QRCode> qrCodes) {
        this.context = context;
        this.qrCodes = qrCodes;
    }

    /**
     * A function that runs a set of instructions upon view creation,
     * which includes setting up the view.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_qrcontent, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * A function that updates the adapter's data set.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QRCode qrCode = qrCodes.get(position);
        QRController qrController = new QRController();

        holder.qrCodeTitle.setText(qrCode.getName());
        holder.qrCodeComment.setText(qrCode.getComment());
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
        public TextView qrCodeComment;
        public TextView qrCodeScore;
        public ImageView qrImage;

        /**
         * A function that updates the view.
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            this.qrCodeTitle = view.findViewById(R.id.qr_title);
            this.qrCodeComment = view.findViewById(R.id.qr_comment);
            this.qrCodeScore = view.findViewById(R.id.qr_score_value);
            this.qrImage = view.findViewById(R.id.qr_image);
        }
    }
}