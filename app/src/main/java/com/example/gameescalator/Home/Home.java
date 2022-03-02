package com.example.gameescalator.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.example.gameescalator.Adapter.RecyclerAdapter;
import com.example.gameescalator.Classes.Messages;
import com.example.gameescalator.Fragments.HomeFragment;
import com.example.gameescalator.Fragments.ProfileFragment;
import com.example.gameescalator.R;
import com.example.gameescalator.User.MainActivity;
import com.example.gameescalator.User.RegisterUser;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //widgets:
    RecyclerView recyclerView;
    private static Toolbar toolbar;
    private DrawerLayout drawerLayout;
    public static NavigationView navigationView;

    //Firebase:
    public static FirebaseAuth firebaseAuth;
    public static FirebaseDatabase firebaseDatabase;

    //Vars:
    private ArrayList<Messages> messagesList;
    private RecyclerAdapter recyclerAdapter;
    private TextView gamesCount;

    public static View navigationHeader;
    public static TextView navigationUsername;

    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;

    final static int postDelayNavigationDrawer = 350;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        drawerLayout = findViewById(R.id.home_drawer_layout);
        navigationView = findViewById(R.id.drawer_nav);
        navigationView.setNavigationItemSelectedListener(this);
        gamesCount = findViewById(R.id.gamesCount);

        navigationHeader = navigationView.getHeaderView(0);
        navigationUsername = navigationHeader.findViewById(R.id.nav_username);
        navigationUsername.setText(firebaseAuth.getCurrentUser().getDisplayName());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        homeFragment = new HomeFragment(gamesCount);
        profileFragment = new ProfileFragment();

        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment(gamesCount)).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        navigationView.setCheckedItem(R.id.nav_home);

    }


    @Override
    public void onBackPressed() {

        GameDetails gameDetailsFragment = (GameDetails) getSupportFragmentManager().findFragmentByTag("GAME_DETAILS_FRAGMENT");

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else if (gameDetailsFragment != null && gameDetailsFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
            toolbar.setVisibility(View.VISIBLE);
        } else if (navigationView.getCheckedItem().getItemId() == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            toolbar.setVisibility(View.VISIBLE);
        }else super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_profile:

                drawerLayout.closeDrawer(GravityCompat.START);

                if (navigationView.getCheckedItem().getItemId() != R.id.nav_profile) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                        }
                    }, postDelayNavigationDrawer);
                    navigationView.setCheckedItem(R.id.nav_profile);
                    toolbar.setVisibility(View.GONE);
                }
                break;

            case R.id.nav_home:

                drawerLayout.closeDrawer(GravityCompat.START);

                if (navigationView.getCheckedItem().getItemId() != R.id.nav_home) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                        }
                    }, postDelayNavigationDrawer);
                    navigationView.setCheckedItem(R.id.nav_home);
                    toolbar.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.nav_signout:
                firebaseAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return false;
    }

    public static void hideToolbar() {
        toolbar.setVisibility(View.GONE);
    }

}