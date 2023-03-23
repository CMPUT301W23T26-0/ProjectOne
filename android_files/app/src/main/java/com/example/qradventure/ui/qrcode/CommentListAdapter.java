package com.example.qradventure.ui.qrcode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qradventure.R;

import java.util.ArrayList;

/**
 * This class allows data lists to be displayed in a list view
 */
public class CommentListAdapter extends ArrayAdapter<Comment> {
    public CommentListAdapter(Context context, ArrayList<Comment> comments) {
        super(context, 0, comments);
    }

    /**
     * returns the view for the comment adapting it accordingly
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.comment_content, parent, false);
        } else {
            view = convertView;
        }

        Comment comment = getItem(position);
        TextView author = view.findViewById(R.id.profile_name);
        TextView contents = view.findViewById(R.id.comment_contents);

        author.setText(comment.getAuthor());
        contents.setText(comment.getContents());

        return view;
    }
}
