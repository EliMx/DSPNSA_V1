package com.example.dspnsa_v1;

import android.content.Intent;
import android.media.MediaDrm;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
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
    private ListView listaView;
    String searchString = "";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    String userKey  = mAuth.getCurrentUser().getUid();
    private DatabaseReference mDatabaseReference = mDatabase.getReference("Listas").child(userKey);

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

        buscador = (SearchView) findViewById(R.id.searchView1);
        listaView = findViewById(R.id.listView1);

        setupSearchView();

        buscador.getContext().getResources().getIdentifier("android:id/submit_area", null, null);

    }

    private void setupSearchView() {
        buscador.setIconifiedByDefault(false);
        buscador.setSubmitButtonEnabled(true);
        buscador.setQueryHint("Search Here");
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
