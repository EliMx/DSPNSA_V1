package com.example.dspnsa_v1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
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
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
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
    listaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //cargar ID del usuario;
        mAuth = FirebaseAuth.getInstance();
        String userKey  = mAuth.getCurrentUser().getUid();

        //Registro  de los elementos de view para mostrar listas guardadas por el usuario
                recyclerView = findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = (TextView) findViewById(R.id.empty_view);

        //Registro  de los elementos de menu inferior
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //Configuracion de boton de menu inferior
        bottomNavigation.setBackground(null);
        bottomNavigation.getMenu().getItem(2).setEnabled(false);

        //Registro  de los elementos de barra lateral
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        configuracionBarraLateral();

        //Registro  de los elementos de menu de navegacion
        navigationView = findViewById(R.id.drawer_menu_items);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener1);

        //Registro  de los elementos de botones flotantes
        mAddFab = (FloatingActionButton) findViewById(R.id.fab1);
        mAddProductoFab = (FloatingActionButton) findViewById(R.id.fab2);
        mAddListaFab = (FloatingActionButton) findViewById(R.id.fab3);

        //Configuracion de boton de botones flotantes
        botonesFlotantes();

        //Cargar listas guardadas por usuario
        Query query = listasRef.child(userKey);
        FirebaseRecyclerOptions<Lista> options = new FirebaseRecyclerOptions.Builder<Lista>().setQuery(query, Lista.class).build();
        adapter = new listaAdapter(options);
        recyclerView.setAdapter(adapter);
    }

    private void configuracionBarraLateral() {
        //Registro  de los elementos del boton para abrir y cerrar barra lateral
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void botonesFlotantes() {
        //Visibilidad de botones que flotan
        mAddProductoFab.setVisibility(View.GONE);
        mAddListaFab.setVisibility(View.GONE);
        isAllFabsVisible = false;
        //Boton flotante padre
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
        //Boton flotante hijo "Agregar Lista"
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

        //Boton flotante hijo "Agregar Producto"
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

/*public class MenuActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    NavigationView navigationView;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    FloatingActionButton mAddFab, mAddProductoFab, mAddListaFab;
    TextView addAlarmActionText, addPersonActionText;
    Boolean isAllFabsVisible;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference listasRef = rootRef.child("Listas");
    String userKey;

    private RecyclerView recyclerView;
    private TextView emptyView;
    listaAdapter adapter; // Create Object of the Adapter class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //cargar ID del usuario;
        mAuth = FirebaseAuth.getInstance();
        userKey = mAuth.getCurrentUser().getUid();

        // Cargar elementos para mostrar listas guardadas por el usuario
        recyclerView = findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = (TextView) findViewById(R.id.empty_view);

        //Cargar listas guardadas por usuario
        Query query = listasRef.child(userKey);
        FirebaseRecyclerOptions<Lista> options = new FirebaseRecyclerOptions.Builder<Lista>().setQuery(query, Lista.class).build();
        adapter = new listaAdapter(options);
        recyclerView.setAdapter(adapter);

        //Activar accion para deslizar lista a la izq para eliminar
        //enableSwipe();

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

        botonesBurbuja();
    }

    private void botonesBurbuja() {
        //Boton burbuja padre
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

        //Boton burbuja accion "Agregar Lista"
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
        //Boton burbuja accion "Agregar Producto"
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
    /*
    private void enableSwipe() {
        ItemTouchHelper mIth = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT) {
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        final int toPos = target.getAdapterPosition();
                        // move item in `fromPos` to `toPos` in adapter.
                        return true;// true if moved, false otherwise
                    }
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        // remove from adapter
                        String postKey =adapter.getRef(viewHolder.getAdapterPosition()).getKey();
                        listasRef.child(userKey).child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    //Your data is removed successfully!
                                    Toast.makeText(MenuActivity.this, "Lista eliminada", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        Bitmap icon;
                        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                            View itemView = viewHolder.itemView;
                            float height = (float) itemView.getBottom() - (float) itemView.getTop();
                            float width = height / 3;
                            Paint p = new Paint();
                            // swiping from left to right, no se requiere realmente
                            if(dX > 0){
                                p.setColor(Color.parseColor("#388E3C"));
                                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                                c.drawRect(background,p);
                                icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_delete);
                                RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                                c.drawBitmap(icon,null,icon_dest,p);
                            }
                            // swiping from right to left
                            else {
                                p.setColor(Color.parseColor("#D32F2F"));
                                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                                c.drawRect(background,p);
                                icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_delete);
                                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                                c.drawBitmap(icon,null,icon_dest,p);
                            }
                        }
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                });
        mIth.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    //Barra inferior opciones
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

    //Opciones de barra lateral
    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener1 =
            new NavigationView.OnNavigationItemSelectedListener() {
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
*/
