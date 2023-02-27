package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.qradventure.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        switchFragment(new ProfileFragment());

        binding.bottomNavi.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navi_profile:
                    switchFragment(new ProfileFragment());
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
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragments, fragment);
        transaction.commit();
    }
}