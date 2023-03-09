// https://www.youtube.com/watch?v=118wylgD_ig&t=353s&ab_channel=CodingWithMitch
// https://developers.google.com/maps/documentation/android-sdk/map
// https://github.com/googlemaps/android-samples/blob/main/ApiDemos/java/app/src/gms/java/com/example/mapdemo/RawMapViewDemoActivity.java
// https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
// https://developer.android.com/training/location/permissions
// https://developer.android.com/training/location/permissions
// https://developers.google.com/maps/documentation/android-sdk/location#:~:text=If%20your%20app%20needs%20to,location%20returned%20by%20the%20API.
// https://www.geeksforgeeks.org/how-to-get-current-location-inside-android-fragment/
// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
package com.example.qradventure;

import static com.example.qradventure.BuildConfig.MAPS_API_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback{
    // Views
    private MapView mapView;
    //private LatLng currentLocation;
    private boolean locationPermissionGranted;

    private GoogleMap mMap;
    TextView tvLatitude, tvLongitude;
    private double[] currentLocationArray = new double[2];
    private Location currLocation;
    Button btLocation;
    /**
     * Request code for location permission request.
     *
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;


    /**
     * Flag indicating whether a requested permission has been denied after returning in {@link
     */
    private boolean permissionDenied = false;
    FusedLocationProviderClient client;

    public MapFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance() {
        Fragment frag = new Fragment();
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.map_view);
        Bundle mapBundle = null;
        if (savedInstanceState != null) {
            mapBundle = savedInstanceState.getBundle(MAPS_API_KEY);
        }
        mapView.onCreate(mapBundle);
        mapView.getMapAsync(this);
        // Assign variable
        btLocation = view.findViewById(R.id.bt_location);
        tvLatitude = view.findViewById(R.id.tv_latitude);
        tvLongitude = view.findViewById(R.id.tv_longitude);

        // Initialize location client
        client = LocationServices
                .getFusedLocationProviderClient(
                        getActivity());

        btLocation.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view)
                    {
                        // check condition
                        if (ContextCompat.checkSelfPermission(
                                getActivity(),
                                android.Manifest.permission
                                        .ACCESS_FINE_LOCATION)
                                == PackageManager
                                .PERMISSION_GRANTED
                                && ContextCompat.checkSelfPermission(
                                getActivity(),
                                android.Manifest.permission
                                        .ACCESS_COARSE_LOCATION)
                                == PackageManager
                                .PERMISSION_GRANTED) {
                            // When permission is granted
                            locationPermissionGranted = true;

                            // Call method

                            getCurrentLocation();
                            updateLocationUI();
                        }
                        else {
                            // When permission is not granted
                            // Call method
                            locationPermissionGranted = false;
                            requestPermissions(
                                    new String[] {
                                            android.Manifest.permission
                                                    .ACCESS_FINE_LOCATION,
                                            Manifest.permission
                                                    .ACCESS_COARSE_LOCATION },
                                    100);
                        }
                    }
                });

        return view;
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
        // Check condition
        if (requestCode == 100 && (grantResults.length > 0)
                && (grantResults[0] + grantResults[1]
                == PackageManager.PERMISSION_GRANTED)) {
            // When permission are granted
            // Call method
            getCurrentLocation();
        }
        else {
            // When permission are denied
            // Display toast
            Toast
                    .makeText(getActivity(),
                            "Permission denied",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation()
    {
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
                            currLocation = location;
                            // Check condition
                            if (location != null) {
                                // When location result is not
                                // null set latitude
                                tvLatitude.setText(
                                        String.valueOf(
                                                location
                                                        .getLatitude()));

                                //currentLocationArray[0] = location.getLatitude();
                                // set longitude
                                tvLongitude.setText(
                                        String.valueOf(
                                                location
                                                        .getLongitude()));
                                //currentLocationArray[1] = location.getLongitude();
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
                                        Location location1
                                                = locationResult
                                                .getLastLocation();
                                        currLocation = location1;
                                        // Set latitude
                                        tvLatitude.setText(
                                                String.valueOf(
                                                        location1
                                                                .getLatitude()));
                                        // Set longitude
                                        tvLongitude.setText(
                                                String.valueOf(
                                                        location1
                                                                .getLongitude()));
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

    // https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMapOptions options = new GoogleMapOptions();
        // to edit options in code
//        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
//                .compassEnabled(true)
//                .rotateGesturesEnabled(true)
//                .tiltGesturesEnabled(true);


        LatLng uofa = new LatLng(53.52682, -113.524493735076);    // u of a coords
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);                // hybrid map now
        googleMap.addMarker(new MarkerOptions()                         // set marker to uofa
                .position(uofa)
                .title("University of Alberta"));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(uofa));      // move to uOfA and Zoom In

        // https://stackoverflow.com/questions/55933929/android-display-user-location-on-map-fragment
        // "hio" https://stackoverflow.com/users/8388068/hio
        MapsInitializer.initialize(getActivity());
        mMap = googleMap;
//        mMap.setOnMyLocationButtonClickListener(this);
//        mMap.setOnMyLocationClickListener(this);
//        enableMyLocation();

        //googleMap.setMyLocationEnabled(true);
    }

    public Location getLocation(){
        return currLocation;
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}