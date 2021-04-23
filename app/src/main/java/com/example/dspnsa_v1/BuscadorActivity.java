package com.example.dspnsa_v1;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaDrm;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class BuscadorActivity extends AppCompatActivity{

    private ArrayList<Lista> listas;
    SearchView buscador;
    CharSequence queryBusqueda;

    private FirebaseAuth mAuth;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference listasRef = rootRef.child("Listas");

    private RecyclerView recyclerView;
    private TextView emptyView;
    listaAdapter adapter;
    FirebaseRecyclerOptions<Lista> options;
    Query query1;
    String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);
        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //cargar ID del usuario;
        mAuth = FirebaseAuth.getInstance();
        userKey  = mAuth.getCurrentUser().getUid();

        buscador = (SearchView) findViewById(R.id.searchView1);

        queryBusqueda = buscador.getQuery(); // get the query string currently in the text field
        System.out.println("x ---------------------------> "+queryBusqueda);
        setupSearchView();

        //Registro  de los elementos de view para mostrar listas guardadas por el usuario
        recyclerView = findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Cargar listas guardadas por usuario
        query1 = listasRef.child(userKey);
        options = new FirebaseRecyclerOptions.Builder<Lista>().setQuery(query1, Lista.class).build();
        adapter = new listaAdapter(options);
        recyclerView.setAdapter(adapter);

        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //setUpRecyclerView();
                firebaseBusquedaPalabra(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.filter(newText);
                firebaseBusquedaLetra(newText);
                return false;
            }
        });

    }

    private void firebaseBusquedaLetra(String query) {
        Query firebaseSearchQuery = listasRef.child(userKey).orderByChild("nombre").startAt(query);
        FirebaseRecyclerOptions<Lista> options1 = new FirebaseRecyclerOptions.Builder<Lista>().setQuery(firebaseSearchQuery, Lista.class).build();
        listaAdapter adapter1 = new listaAdapter(options1);
        adapter1.startListening();
        recyclerView.setAdapter(adapter1);
    }

    private void firebaseBusquedaPalabra(String query) {
        Query firebaseSearchQuery = listasRef.child(userKey).orderByChild("nombre").equalTo(query);
        FirebaseRecyclerOptions<Lista> options1 = new FirebaseRecyclerOptions.Builder<Lista>().setQuery(firebaseSearchQuery, Lista.class).build();
        listaAdapter adapter1 = new listaAdapter(options1);
        adapter1.startListening();
        recyclerView.setAdapter(adapter1);
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

    private void setupSearchView() {
        buscador.setIconifiedByDefault(false);
        buscador.setSubmitButtonEnabled(true);
        buscador.setQueryHint("Buscar aqui");
    }

    // habilidar boton para volver atras
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(BuscadorActivity.this, MenuActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
