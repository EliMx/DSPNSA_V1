package com.example.dspnsa_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    NavigationView navigationView;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    FloatingActionButton mAddFab, mAddAlarmFab, mAddPersonFab;
    TextView addAlarmActionText, addPersonActionText;
    Boolean isAllFabsVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        //openFragment(HomeFragment.newInstance("", ""));

        //Floating button menu
        bottomNavigation.setBackground(null);
        bottomNavigation.getMenu().getItem(2).setEnabled(false);

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        navigationView = findViewById(R.id.drawer_menu_items);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener1);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Register all the FABs with their IDs
        // This FAB button is the Parent
        mAddFab = (FloatingActionButton) findViewById(R.id.fab1);
        mAddAlarmFab = (FloatingActionButton) findViewById(R.id.fab2);
        mAddPersonFab = (FloatingActionButton) findViewById(R.id.fab3);

        mAddAlarmFab.setVisibility(View.GONE);
        mAddPersonFab.setVisibility(View.GONE);

        isAllFabsVisible = false;

        mAddFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {
                            mAddAlarmFab.show();
                            mAddPersonFab.show();
                            isAllFabsVisible = true;
                        } else {
                            mAddAlarmFab.hide();
                            mAddPersonFab.hide();
                            isAllFabsVisible = false;
                        }
                    }
                });
        mAddPersonFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MenuActivity.this, "Agregar Lista", Toast.LENGTH_SHORT).show();
                    }
                });

        mAddAlarmFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MenuActivity.this, "Agregar Producto", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            //openFragment(HomeFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_search:
                            //openFragment(SmsFragment.newInstance("", ""));
                            return true;
                        //case R.id.navigation_add:
                            //openFragment(NotificationFragment.newInstance("", ""));
                          //  return true;
                        case R.id.navigation_dashboard:
                            //openFragment(NotificationFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_profile:
                            //openFragment(NotificationFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener1 =
            new NavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_account:
                            // openFragment(NotificationFragment.newInstance("", ""));
                            return true;
                        //case R.id.nav_settings:
                            //openFragment(NotificationFragment.newInstance("", ""));
                        //    return true;
                        case R.id.nav_help:
                            Intent intent = new Intent(MenuActivity.this, HelpActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        case R.id.nav_logout:
                            FirebaseAuth.getInstance().signOut();
                            return true;
                    }
                    return true;
                }
            };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

