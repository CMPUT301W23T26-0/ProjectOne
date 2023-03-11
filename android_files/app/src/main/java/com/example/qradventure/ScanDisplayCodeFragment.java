package com.example.qradventure;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.fragment.app.DialogFragment;

public class ScanDisplayCodeFragment extends DialogFragment {
    private boolean isSeen;
    private QRCode code;

    public ScanDisplayCodeFragment(QRCode code, boolean isSeen) {
        this.code = code;
        this.isSeen = isSeen;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_scan_display_code, null);

        // Initialize variables
        TextView codeNameText = (TextView) view.findViewById(R.id.scan_display_code_name_text);
//        ImageView codeImage = (ImageView) view.findViewById(R.id.scan_display_code_image);
        TextView codeScoreText = (TextView) view.findViewById(R.id.scan_display_code_score_text);
        TextView bottomText = (TextView) view.findViewById(R.id.scan_display_code_bottom_text);

        // Set variables
        codeNameText.setText(code.getName());
        /* Set image here */
        String formattedScoreText = String.format("Score: %d", code.getScore());
        codeScoreText.setText(formattedScoreText);

        // Check if user has it already
        if (isSeen) {
            // If they do, set bottom text to "Already collected."
            bottomText.setText("Already collected.");
        } else {
            // Otherwise, set bottom text to "Added to collection."
            bottomText.setText("Added to collection!");
            // Also add it to their profile
        }


        String fragmentTitle = "Congratulations! You found...";
        TextView title = new TextView(getActivity());
        title.setText(fragmentTitle);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        // Return dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCustomTitle(title)
                .setView(view)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Allow user to take picture
                                // Prompt geolocation after
                                PromptPictureFragment frag = new PromptPictureFragment();
                                frag.show(getActivity().getSupportFragmentManager(), "Prompt Picture");
                            }
                        });
        return builder.create();
    }
}
