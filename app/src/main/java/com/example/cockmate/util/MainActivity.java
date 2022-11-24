package com.example.cockmate.util;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.cockmate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MainMenuHomeFragment fragmentHome = new MainMenuHomeFragment();
    private MainMenuCommunityFragment fragmentCommunity = new MainMenuCommunityFragment();
    private MainMenuBookmarkFragment fragmentBookmark = new MainMenuBookmarkFragment();
    private MainMenuInfoFragment fragmentInfo = new MainMenuInfoFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.menu_frame_layout, fragmentHome).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()) {
                case R.id.menu_home:
                    transaction.replace(R.id.menu_frame_layout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.menu_community:
                    transaction.replace(R.id.menu_frame_layout, fragmentCommunity).commitAllowingStateLoss();
                    break;
                case R.id.menu_bookmark:
                    transaction.replace(R.id.menu_frame_layout, fragmentBookmark).commitAllowingStateLoss();
                    break;
                case R.id.menu_mInfo:
                    transaction.replace(R.id.menu_frame_layout, fragmentInfo).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}