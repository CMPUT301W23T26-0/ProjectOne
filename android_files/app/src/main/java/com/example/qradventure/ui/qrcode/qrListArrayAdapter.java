package com.example.qradventure.ui.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qradventure.R;
import java.util.ArrayList;

public class qrListArrayAdapter extends RecyclerView.Adapter<qrListArrayAdapter.ViewHolder>{
    // RecyclerView needs a custom adapter

    private Context context;
    private ArrayList<Bitmap> picsList;

    /**
     * A constructor for CustomListAdapter.
     * @param context The context of the data list
     * @param picsList The data list of QR codes to be adapted
     */
    public qrListArrayAdapter(Context context, ArrayList<Bitmap> picsList) {
        this.context = context;
        this.picsList = picsList;
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
        View view = LayoutInflater.from(context).inflate(R.layout.pics_content, parent, false);
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
    public void onBindViewHolder(qrListArrayAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // nothing for now
//                QRCode qrCode = qrCodes.get(position);
//
//                qrFragment frag = new qrFragment();
//                Bundle args = new Bundle();
//                args.putString("hash", qrCode.getHashValue());
//                frag.setArguments(args);
//
//                FragmentActivity activity = (FragmentActivity) context;
//                FragmentManager manager = activity.getSupportFragmentManager();
//
//                manager.beginTransaction()
//                        .replace(R.id.fragments, frag)
//                        .setReorderingAllowed(true)
//                        .addToBackStack(null)
//                        .commit();
            }
        });

//        QRCode qrCode = qrCodes.get(position);
//        QRController qrController = new QRController();
//
//        holder.qrCodeTitle.setText(qrCode.getName());
//        holder.qrCodeScore.setText(Integer.toString(qrCode.getScore()));
//        holder.qrImage.setImageDrawable(qrController.generateImage(context, qrCode.getHashValue()));
        Bitmap pic = picsList.get(position);
        holder.qrPic.setImageBitmap(pic);
    }

    /**
     * This class is used to update the view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView qrCodeTitle;
//        public TextView qrCodeScore;
//        public ImageView qrImage;
        public ImageView qrPic;

        /**
         * A function that updates the view.
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            this.qrPic = view.findViewById(R.id.qr_pics_image);
//            this.qrCodeTitle = view.findViewById(R.id.qr_title);
//            this.qrCodeScore = view.findViewById(R.id.qr_score_value);
//            this.qrImage = view.findViewById(R.id.qr_image);
        }
    }

    /**
     * A function that returns the data list size.
     * @return
     */
    @Override
    public int getItemCount() {
        return picsList.size();
    }
}
