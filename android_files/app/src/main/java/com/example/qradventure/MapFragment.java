// https://www.youtube.com/watch?v=118wylgD_ig&t=353s&ab_channel=CodingWithMitch
// https://developers.google.com/maps/documentation/android-sdk/map
// https://github.com/googlemaps/android-samples/blob/main/ApiDemos/java/app/src/gms/java/com/example/mapdemo/RawMapViewDemoActivity.java
// https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
// https://developer.android.com/training/location/permissions
// https://developer.android.com/training/location/permissions
// https://developers.google.com/maps/documentation/android-sdk/location#:~:text=If%20your%20app%20needs%20to,location%20returned%20by%20the%20API.
package com.example.qradventure;

import static com.example.qradventure.BuildConfig.MAPS_API_KEY;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
// , GoogleMap.OnMyLocationButtonClickListener,
//        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback
public class MapFragment extends Fragment implements OnMapReadyCallback{
    // Views
    private MapView mapView;
    private GoogleMap mMap;
    Context context;
    FragmentActivity fragAct = new FragmentActivity();
    /**
     * Request code for location permission request.
     *
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in {@link
     */
    private boolean permissionDenied = false;
    // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher, as an instance variable.

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Set the layout file as the content view.
//        setContentView(R.layout.activity_main);
//
//        // Get a handle to the fragment and register the callback.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map_view);
//        mapFragment.getMapAsync(this);

    }

    // Get a handle to the GoogleMap object and display marker.
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(0, 0))
//                .title("Marker"));
//    }


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
        return view;
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

        Map mapObj = new Map();

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
//        mapObj.getLocationPermission();
//        mapObj.updateLocationUI();
//        mapObj.getDeviceLocation();
    }
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
//    @SuppressLint("MissingPermission")
//    private void enableMyLocation() {
//        // 1. Check if permissions are granted, if so, enable the my location layer
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED
//                || ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
//            return;
//        }
//
//        // 2. Otherwise, request location permissions from the user.
//        PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
//    }
//    @Override
//    public boolean onMyLocationButtonClick() {
//        // Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
//        // Return false so that we don't consume the event and the default behavior still occurs
//        // (the camera animates to the user's current position).
//        return false;
//    }
//
//    @Override
//    public void onMyLocationClick(@NonNull Location location) {
//        // Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            return;
//        }
//
//        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
//                Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
//                .isPermissionGranted(permissions, grantResults,
//                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
//            // Enable the my location layer if the permission has been granted.
//            enableMyLocation();
//        } else {
//            // Permission was denied. Display an error message
//            // Display the missing permission error dialog when the fragments resume.
//            permissionDenied = true;
//        }
//    }

//    @Override
//    protected void onResumeFragments() {
//        super.onResumeFragments();
//        if (permissionDenied) {
//            // Permission was not granted, display error dialog.
//            showMissingPermissionError();
//            permissionDenied = false;
//        }
//    }

//    /**
//     * Displays a dialog with error message explaining that the location permission is missing.
//     */
//    private void showMissingPermissionError() {
//        PermissionUtils.PermissionDeniedDialog
//                .newInstance(true).show(getSupportFragmentManager(), "dialog");
//    }



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