package com.example.qradventure.ui.scan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.fragment.app.DialogFragment;

import com.example.qradventure.R;
import com.example.qradventure.qrcode.QRCode;
import com.example.qradventure.qrcode.QRController;

/**
 * This class extends DialogFragment, and it serves to display a
 * newly scanned QR code's characteristics while also prompting the user
 * to take a picture of the code's location.
 */
public class DisplayCodePromptPictureFragment extends DialogFragment {
    private boolean isSeen;
    private QRCode code;
    private Boolean success = false; // Whether or not the user wants to take a picture
    private CameraInScanFrag scanFrag;

    // https://stackoverflow.com/questions/26734432/send-data-from-dialogfragment-to-fragment
    /**
     * This interface allows DisplayCodePromptPictureFragment
     * to open up the camera in ScanFragment. Note that the
     * camera cannot be opened from a DialogFragment.
     */
    public interface CameraInScanFrag {
        void openCameraInScanFrag(Boolean success);
    }

    /**
     * This function allows an instance of the
     * DisplayCodePromptPictureFragment to establish
     * a connection with an instance of ScanFragment
     * @param scanFrag An instance of ScanFragment
     */
    public void setScanFragment(CameraInScanFrag scanFrag) {
        this.scanFrag = scanFrag;
    }

    /**
     * Constructor for the DisplayCodePromptPictureFragment
     * @param code The newly scanned QR code whose characteristics are to be displayed.
     * @param isSeen Whether or not the code has been seen by the user
     */
    public DisplayCodePromptPictureFragment(QRCode code, boolean isSeen) {
        this.code = code;
        this.isSeen = isSeen;
    }

    /**
     * A function that runs a set of instructions upon creating
     * the view for DisplayCodePromptPictureFragment, which includes text set up
     * for the view.
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_scan_display_code, null);

        // Initialize variables
        TextView codeNameText = (TextView) view.findViewById(R.id.scan_display_code_name_text);
        ImageView codeImage = (ImageView) view.findViewById(R.id.scan_display_code_image); // For image display later
        TextView codeScoreText = (TextView) view.findViewById(R.id.scan_display_code_score_text);
        TextView bottomText = (TextView) view.findViewById(R.id.scan_display_code_bottom_text);

        // Set variables
        codeNameText.setText(code.getName());
        /* Set image here */
        String formattedScoreText = String.format("Score: %d", code.getScore());
        codeScoreText.setText(formattedScoreText);

        // Check if user has it already, and set bottom text accordingly
        if (isSeen) {
            bottomText.setText("Already collected.");
        } else {
            bottomText.setText("Added to collection!");
        }

        // Set up custom title
        String fragmentTitle = "Congratulations! You found...";
        TextView title = new TextView(getActivity());
        title.setText(fragmentTitle);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        QRController qrController = new QRController();
        codeImage.setImageDrawable(qrController.generateImage(getContext(), code.getHashValue()));

        // Return dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCustomTitle(title)
                .setView(view)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Indicate that the user wants to take a picture
                                success = true;
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Skip picture taking, prompt geolocation (done automatically)
                            }
                        });
        return builder.create();
    }

    /**
     * A function that runs a set of instructions when the
     * view is destroyed, which includes trying to open up the
     * camera in ScanFragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        scanFrag.openCameraInScanFrag(success);
    }
}
