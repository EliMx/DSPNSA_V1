package com.example.dspnsa_v1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    NavigationView navigationView;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    FloatingActionButton mAddFab, mAddProductoFab, mAddListaFab;
    TextView addAlarmActionText, addPersonActionText;
    Boolean isAllFabsVisible;

    private FirebaseAuth mAuth;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference listasRef = rootRef.child("Listas");

    private RecyclerView recyclerView;
    private TextView emptyView;
    listaAdapter adapter; // Create Object of the Adapter class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //cargar ID del usuario;
        mAuth = FirebaseAuth.getInstance();
        String userKey  = mAuth.getCurrentUser().getUid();

        //Cargar elementos para mostrar listas guardadas por el usuario
        recyclerView = findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = (TextView) findViewById(R.id.empty_view);

        //Cargar listas guardadas por usuario
        Query query = listasRef.child(userKey);
        FirebaseRecyclerOptions<Lista> options = new FirebaseRecyclerOptions.Builder<Lista>().setQuery(query, Lista.class).build();
        adapter = new listaAdapter(options);
        recyclerView.setAdapter(adapter);

        //Barra lateral
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //Botones que flotan en menu
        bottomNavigation.setBackground(null);
        bottomNavigation.getMenu().getItem(2).setEnabled(false);

        //Barra lateral
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        //Menu inferior de navegacion
        navigationView = findViewById(R.id.drawer_menu_items);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener1);

        //Boton para abrir y cerrar barra lateral
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Registro  de los elementos de botones que flotan
        mAddFab = (FloatingActionButton) findViewById(R.id.fab1);
        mAddProductoFab = (FloatingActionButton) findViewById(R.id.fab2);
        mAddListaFab = (FloatingActionButton) findViewById(R.id.fab3);

        //Visibilidad de botones que flotan
        mAddProductoFab.setVisibility(View.GONE);
        mAddListaFab.setVisibility(View.GONE);

        isAllFabsVisible = false;

        mAddFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {
                            mAddProductoFab.show();
                            mAddListaFab.show();
                            isAllFabsVisible = true;
                        } else {
                            mAddProductoFab.hide();
                            mAddListaFab.hide();
                            isAllFabsVisible = false;
                        }
                    }
                });
        //Floating button "Agregar Lista"
        mAddListaFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MenuActivity.this, "Agregar Lista", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MenuActivity.this, GestorLista.class);
                        startActivity(intent);
                        finish();
                    }
                });

        mAddProductoFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MenuActivity.this, "Agregar Producto", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MenuActivity.this, GestorProducto.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
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
                            Intent intent = new Intent(MenuActivity.this, BuscadorActivity.class);
                            startActivity(intent);
                            finish();
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