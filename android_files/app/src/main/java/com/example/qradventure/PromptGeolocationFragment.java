package com.example.qradventure;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * This class extends DialogFragment, and it serves to prompt
 * the user to record their geolocation for the recently
 * scanned QR code.
 */
public class PromptGeolocationFragment extends DialogFragment {
    String toastMessage = "Skipping geolocation...";

    // Data
    private UserDataClass user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbCodes;
    private QRCode code;

    // Location
    private FusedLocationProviderClient client;

    /**
     * Constructor for the PromptGeolocationFragment
     * @param code The newly scanned QR code associated with the geolocation
     */
    public PromptGeolocationFragment(QRCode code) {
        this.code = code;
    }

    /**
     * This function runs a set of instructions upon creating
     * the view for PromptGeolocationFragment, which includes data
     * instantiation and text set up for the view.
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Instantiate data
        user = user.getInstance();
        dbCodes = db.collection("QRCodes");

        // Set view title
        String promptTitle = "Would you like to record the QR code's geolocation?";

        // Return dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(promptTitle)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Record geolocation to QRCode
                                client = LocationServices
                                        .getFusedLocationProviderClient(
                                                getActivity());
                                getCurrentLocation();
                                toastMessage = "Saving geolocation...";
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Don't record geolocation (done automatically)
                            }
                        });

        return builder.create();
    }

    // https://www.geeksforgeeks.org/how-to-get-current-location-inside-android-fragment/
    /**
     * A function that gets the current geolocation of the user.
     */
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Initialize Location manager
        LocationManager locationManager
                = (LocationManager)getActivity()
                .getSystemService(
                        Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            client.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(
                                @NonNull Task<Location> task)
                        {

                            // Initialize location
                            Location location
                                    = task.getResult();
                            // Check condition
                            if (location != null) {
                                // When location result is not
                                // null save geolocation
                                saveGeolocation(location);
                            }
                            else {
                                // When location result is null
                                // initialize location request
                                LocationRequest locationRequest
                                        = new LocationRequest()
                                        .setPriority(
                                                LocationRequest
                                                        .PRIORITY_HIGH_ACCURACY)
                                        .setInterval(10000)
                                        .setFastestInterval(
                                                1000)
                                        .setNumUpdates(1);

                                // Initialize location call back
                                LocationCallback
                                        locationCallback
                                        = new LocationCallback() {
                                    @Override
                                    public void
                                    onLocationResult(
                                            LocationResult
                                                    locationResult)
                                    {
                                        // Initialize
                                        // location
                                        Location currLocation
                                                = locationResult
                                                .getLastLocation();

                                        // save geolocation
                                        saveGeolocation(currLocation);
                                    }
                                };

                                // Request location updates
                                client.requestLocationUpdates(
                                        locationRequest,
                                        locationCallback,
                                        Looper.myLooper());
                            }
                        }
                    });
        }
        else {
            // When location service is not enabled
            // open location setting
            startActivity(
                    new Intent(
                            Settings
                                    .ACTION_LOCATION_SOURCE_SETTINGS)
                            .setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    /**
     * A function that saves a geolocation to a QR code. This is
     * recorded in the database.
     * @param geolocation
     */
    private void saveGeolocation(Location geolocation) {
        // ScanFragment already ensures the code exists, don't have to do a check
        DocumentReference docRef;

        // Update code fields to have geolocation
        Map<String, Object> newCode = new HashMap<>();
        newCode.put("location", geolocation);
        newCode.put("name", code.getName());
        newCode.put("score", code.getScore());
        newCode.put("hash", code.getHashValue());

        // Update code in QRCode Collection with geolocation
        docRef = dbCodes.document(code.getHashValue());
        docRef.set(newCode)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    /**
     * This function runs a set of instructions when the view is destroyed,
     * which includes displaying a toast message.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }
}
