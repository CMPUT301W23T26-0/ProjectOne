// https://www.youtube.com/watch?v=118wylgD_ig&t=353s&ab_channel=CodingWithMitch
// https://developers.google.com/maps/documentation/android-sdk/map
// https://github.com/googlemaps/android-samples/blob/main/ApiDemos/java/app/src/gms/java/com/example/mapdemo/RawMapViewDemoActivity.java
// https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
// https://developer.android.com/training/location/permissions
// https://developer.android.com/training/location/permissions
// https://developers.google.com/maps/documentation/android-sdk/location#:~:text=If%20your%20app%20needs%20to,location%20returned%20by%20the%20API.
// https://www.geeksforgeeks.org/how-to-get-current-location-inside-android-fragment/
// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
package com.example.qradventure.ui.map;

import static com.example.qradventure.BuildConfig.MAPS_API_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.qradventure.R;
import com.example.qradventure.users.UserDataClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback{
    private MapView mapView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference locations = db.collection("QRCodes");
    private List<String> docLocations = new ArrayList<>();
    // https://stackoverflow.com/questions/13543457/how-do-you-create-a-dictionary-in-java
    // "arshajii" https://stackoverflow.com/users/1357341/arshajii
    // edited by "Timo Giese", https://stackoverflow.com/users/4440113/timo-giese
    private Map<String, LatLng> locationDict = new HashMap<>();
    private Map<String, Double> lengthDict = new HashMap<>();
    private GoogleMap mMap;
    private Location currLocation;
    Button getCloseButton;
    private FusedLocationProviderClient client;
    private UserDataClass user = UserDataClass.getInstance();

    /**
     * Constructor for MapFragment
     */
    public MapFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initialize fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflate layout of fragment and assign variables to views
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.map_view);
        Bundle mapBundle = null;
        if (savedInstanceState != null) {
            mapBundle = savedInstanceState.getBundle(MAPS_API_KEY);
        }
        mapView.onCreate(mapBundle);
        mapView.getMapAsync(this);

        // Assign variable
        getCloseButton = view.findViewById(R.id.bt_location);

        // Initialize location client
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }

    /**
     * Update the current user location
     */
    private void updateLocation(){
        // check condition
        if (ContextCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // When permission is granted
            // Call method
            retrieveLocation();
            updateLocationUI();
        }
        else {
            // When permission is not granted
            // Call method
            requestPermissions(new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    100);
        }
    }

    /**
     * Check if the user allowed their location to be tracked. If granted, update the user location.
     * If denied, display "Permission denied."
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check condition
        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1]
                                                        == PackageManager.PERMISSION_GRANTED)) {
            // When permission are granted
            // Call method
            updateLocation();
        }
        else {
            // When permission are denied
            // Display toast
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Set the user's last location
     */
    @SuppressLint("MissingPermission")
    private void retrieveLocation() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            client.getLastLocation().addOnCompleteListener(
                    task -> {
                        // Initialize location
                        currLocation = task.getResult();
                        user.setCurrentLocation(task.getResult());

                        // Check condition
                        if (currLocation == null) {
                            // When location result is null
                            // initialize location request
                            LocationRequest locationRequest = new LocationRequest().setPriority(
                                            LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(10000)
                                    .setFastestInterval(1000)
                                    .setNumUpdates(1);

                            // Initialize location call back
                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void
                                onLocationResult(LocationResult locationResult) {
                                    // Initialize location
                                    currLocation = locationResult.getLastLocation();
                                    user.setCurrentLocation(currLocation);
                                }
                            };

                            // Request location updates
                            client.requestLocationUpdates(
                                    locationRequest,
                                    locationCallback,
                                    Looper.myLooper());
                        }
                    });
        }
        else {
            // When location service is not enabled
            // open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    // https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
    /**
     * Set Google Maps to display MyLocationButton if a location is available
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Initialize Google Maps MapView
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // https://stackoverflow.com/questions/55933929/android-display-user-location-on-map-fragment
        // "hio" https://stackoverflow.com/users/8388068/hio
        MapsInitializer.initialize(getActivity());
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);                // hybrid map now

        updateLocation();
        placeMarkers();

        getCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();
                // initialize user and marker longs and lats
                double userLongitude = currLocation.getLongitude();
                double userLatitude = currLocation.getLatitude();
                LatLng tempLocation;
                double tempLongitude;
                double tempLatitude;
                double distance;

                // for each marker, get the distance between the user and the marker using pythagoras
                // https://www.w3schools.com/java/java_hashmap.asp
                for (String i : locationDict.keySet()) {
                    tempLocation = locationDict.get(i);
                    tempLatitude = tempLocation.latitude;
                    tempLongitude = tempLocation.longitude;

                    // get the distance using pythagoras
                    distance = getPythag(userLongitude, userLatitude, tempLongitude, tempLatitude);

                    // put each marker with its distance to the user in a dictionary
                    lengthDict.put(i, distance);
                }
                // https://howtodoinjava.com/java/sort/java-sort-map-by-values/
                // sorts dictionary into a new dictionary
                LinkedHashMap<String, Double> sortedMap = lengthDict.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
                // https://www.geeksforgeeks.org/how-to-get-first-or-last-entry-from-java-linkedhashmap/
                // converts keys of dictionary into an array
                String[] aKeys = sortedMap.keySet().toArray(new String[sortedMap.size()]);

                // display list of nearby QR codes
                nearbyQRCodeDialog(aKeys);
            }
        });
    }

    /**
     * Returns the distance between two points (the first two params are the user and
     * the next two params are a marker) using Pythagoras.
     * @param userLongitude
     * @param userLatitude
     * @param tempLongitude
     * @param tempLatitude
     * @return Distance between user and marker
     */
    private double getPythag(double userLongitude, double userLatitude, double tempLongitude, double tempLatitude){
        double x = Math.pow((userLatitude - tempLatitude), 2);      // x = (ULat - TLat)^2
        double y = Math.pow((userLongitude - tempLongitude), 2);    // y = (ULong - TLong)^2
        return Math.sqrt(x + y);                                    // sqrt(x + y)
    }

    // https://stackoverflow.com/questions/50035752/how-to-get-list-of-documents-from-a-collection-in-firestore-android
    // "Alex Mamo", https://stackoverflow.com/users/5246885/alex-mamo
    /**
     * Get locations of scanned QR codes from DB and save them as markers on the map
     */
    private void placeMarkers(){
        locations.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // get the location of QR code from the DB
                        Map<String, Object> qrLocation = (Map<String, Object>) document.get("location");

                        // if user chose not to save location, skip
                        if (qrLocation == null){
                            continue;
                        }
                        // get long and lat
                        Double longitude = (Double) qrLocation.get("longitude");
                        Double latitude = (Double) qrLocation.get("latitude");
                        // get name of marker
                        String tempName = document.getString("name");
                        // create a temporary LatLng object
                        LatLng tempLocation = new LatLng(latitude, longitude);
                        // add location to list
                        locationDict.put(tempName, tempLocation);
                        // add a marker
                        mMap.addMarker(new MarkerOptions()
                                .position(tempLocation)
                                .title(tempName));
                    }
                    Log.d("SUCCESS", docLocations.toString());
                } else {
                    Log.d("NO BUENO", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * Displays nearby QR codes. Shows up to 5 codes.
     */
    private void nearbyQRCodeDialog(String[] qrCodeList) {
        int qrCodeListLength = qrCodeList.length;
        int maxListLimit = 5;

        //error check max limit
        int lowestLength;
        if (qrCodeListLength < maxListLimit){
            lowestLength = qrCodeListLength;
        }
        else {
            lowestLength = maxListLimit;
        }

        //initialize displayed QR codes
        String[] tempList = new String[lowestLength];
        int i = 0;
        while (i < lowestLength){
            tempList[i] = qrCodeList[i];
            i++;
        }

        //moves camera to selected QR code
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LatLng tempLocation = locationDict.get(qrCodeList[which]);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tempLocation, 20));
            }
        };

        //displays QR code list
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Nearby QR Codes")
                .setItems(tempList, listener)
                .setCancelable(true)
                .show();
    }

    /**
     * Get user location when switching back to MapFragment
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        //update location when switching to map fragment
        updateLocation();
    }
}