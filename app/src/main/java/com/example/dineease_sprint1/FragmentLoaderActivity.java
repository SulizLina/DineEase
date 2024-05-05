package com.example.dineease_sprint1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FragmentLoaderActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_loader);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set initial fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new homepage()).commit();

        // Set listener for BottomNavigationView item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Check which item is selected
            if (itemId == R.id.home) {
                selectedFragment = new homepage();
            } else if (itemId == R.id.add) {
                selectedFragment = new reservation();
            } else if (itemId == R.id.manage) {
                selectedFragment = new manage_reservation();
            }

            // Replace the current fragment with the selected one
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
            }

            return true;
        });
    }
}
