package com.example.qradventure.ui.leaderboard;

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
import com.example.qradventure.ui.leaderboard.players.PlayersFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This class allows players to be displayed in a list in the LeaderboardFragment
 */
public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Player> players;

    /**
     * A constructor for PlayerListAdapter.
     * @param context The context of the data list
     * @param players The data list of players to be adapted
     */
    public PlayerListAdapter(Context context, ArrayList<Player> players) {
        this.context = context;
        this.players = players;
    }

    /**
     * A function that runs a set of instructions upon ViewHolder creation,
     * which includes setting up the view.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return The newly created ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_profile_content, parent, false);
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
        Player player = players.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player player = players.get(position);
                Bundle args = new Bundle();
                args.putString("username", player.getName());

                PlayersFragment playersFragment = new PlayersFragment();
                //args.putString("hash", qrCode.getHashValue());
                playersFragment.setArguments(args);
                FragmentActivity activity = (FragmentActivity) context;
                FragmentManager manager = activity.getSupportFragmentManager();

                manager.beginTransaction()
                        .replace(R.id.fragments, playersFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });

        holder.playerName.setText(player.getName());
        holder.playerScore.setText(String.valueOf(player.getScore()));
        holder.playerPlace.setText(String.valueOf(position + 1));
    }

    /**
     * A function that returns the data list size.
     * @return Size of the data list
     */
    @Override
    public int getItemCount() {
        return players.size();
    }

    /**
     * This class is used to update the view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView playerName;
        public TextView playerScore;
        public ImageView playerImage;
        public TextView playerPlace;

        /**
         * A function that updates the view.
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            this.playerName = view.findViewById(R.id.player_name);
            this.playerScore = view.findViewById(R.id.player_score);
            this.playerImage = view.findViewById(R.id.player_image);
            this.playerPlace = view.findViewById(R.id.player_place);
        }
    }
}
