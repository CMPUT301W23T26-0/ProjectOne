package com.example.qradventure;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.DialogFragment;

public class DisplayCodePromptPictureFragment extends DialogFragment {

    String toastMessage = "Skipping camera...";

    private boolean isSeen;
    private QRCode code;

    private Boolean state = false;

    // https://stackoverflow.com/questions/26734432/send-data-from-dialogfragment-to-fragment
    public interface CameraInScanFrag {
        void openCameraInScanFrag(Boolean state);
    }

    CameraInScanFrag scanFrag;

    public void setScanFragment(CameraInScanFrag scanFrag) {
        this.scanFrag = scanFrag;
    }

    public DisplayCodePromptPictureFragment(QRCode code, boolean isSeen) {
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
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Allow user to take picture
                                state = true;
                                toastMessage = "Opening camera...";
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Skip picture taking, prompt geolocation
                            }
                        });
        return builder.create();
    }

    // In case user closes, don't take picture and don't save geolocation
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
        scanFrag.openCameraInScanFrag(state);
    }
}
