package com.example.qradventure;

import android.Manifest;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.qradventure.databinding.ActivityMainBinding;
import com.example.qradventure.ui.leaderboard.LeaderboardFragment;
import com.example.qradventure.ui.map.MapFragment;
import com.example.qradventure.ui.profiles.ProfileFragment;
import com.example.qradventure.ui.scan.ScanFragment;

/**
 * This activity allows the user to access all of the main
 * fragments, which include ProfileFragment, ScanFragment,
 * LeaderboardFragment, and MapFragment
 */
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    /**
     * This function runs a set of instructions upon activity
     * creation, which includes permission and view set up.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // getting the inflater to work with fragments
        setContentView(R.layout.activity_main);

        setContentView(binding.getRoot());
        switchFragment(new ProfileFragment());

        //request location permission
        requestPermissions(new String[] {
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION },
                100);

        binding.bottomNavi.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navi_profile:
                    switchFragment(new ProfileFragment()); // switching fragment made into a function
                    break;
                case R.id.navi_scan:
                    switchFragment(new ScanFragment());
                    break;
                case R.id.navi_leaderboard:
                    switchFragment(new LeaderboardFragment());
                    break;
                case R.id.navi_map:
                    switchFragment(new MapFragment());
                    break;
            }

            return true;
        });
    }

    /**
     * This function allows the main fragments to be navigated
     * through by the user via the app's bottom navigation bar.
     * @param fragment The fragment to be navigated to
     */
    private void switchFragment(Fragment fragment) {
        // Fragment manager example from the developers guide
        // https://developer.android.com/guide/fragments/fragmentmanager#java
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
            .replace(R.id.fragments, fragment)
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit();
    }
}