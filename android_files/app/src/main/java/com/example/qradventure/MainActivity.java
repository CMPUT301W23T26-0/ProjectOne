package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.qradventure.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // getting the inflater to work with fragments
        setContentView(R.layout.activity_main);

        setContentView(binding.getRoot());
        switchFragment(new ProfileFragment());
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