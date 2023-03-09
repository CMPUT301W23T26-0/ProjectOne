package com.example.qradventure;

import android.app.Dialog;
import android.os.Bundle;
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
        ImageView codeImage = (ImageView) view.findViewById(R.id.scan_display_code_image);
        TextView codeScoreText = (TextView) view.findViewById(R.id.scan_display_code_score_text);

        // Set variables
        codeNameText.setText(code.getName());
        /* Set image here */
        String formattedScoreText = String.format("Score: %d", code.getScore());
        codeScoreText.setText(formattedScoreText);

        // Return dialog
        Dialog builder = new Dialog(getActivity());
        builder.setContentView(view);
        return builder;
    }
}
