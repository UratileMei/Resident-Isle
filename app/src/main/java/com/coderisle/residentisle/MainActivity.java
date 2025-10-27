package com.coderisle.residentisle;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.coderisle.residentisle.R;
import com.coderisle.residentisle.fragments.*;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView rvRecentAnnouncements;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigation;
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(navigationView));
// Load home fragment by default
        if (savedInstanceState == null) {
            loadFragment(new AdminDashboardFragment());
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }

        // Bottom Navigation handling
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment;

            if (id == R.id.nav_map) {
                selectedFragment = new MapFragment();
            } else if (id == R.id.nav_report) {
                selectedFragment = new ReportIssueFragment();
            } else if (id == R.id.nav_track) {
                selectedFragment = new VolunteerFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else {
                selectedFragment = new HomeFragment();
            }

            loadFragment(selectedFragment);
            return true;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}