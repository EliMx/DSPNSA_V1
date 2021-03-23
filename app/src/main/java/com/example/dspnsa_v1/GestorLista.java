package com.example.dspnsa_v1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class GestorLista extends AppCompatActivity {

    //Componentes del registro
    private EditText EditTextNombre;
    private Button buttonRegistrar, buttonEliminar, ver;

    //variables del registro
    private String nombre = "";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    String userKey  = mAuth.getCurrentUser().getUid();
    private DatabaseReference mDatabaseReference = mDatabase.getReference("Listas").child(userKey);

    private Lista listaDetalles;
    private String listaId;

    private Long productoDetalles;
    private String productoId;

    private RecyclerView recyclerView;
    private TextView emptyView;
    productosAdapter adapter; // Create Object of the Adapter class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestorlista);
        this.setTitle("Nueva lista");

        mDatabaseReference.keepSynced(true);
        mDatabaseReference.keepSynced(false);

        EditTextNombre = (EditText) findViewById(R.id.inputNombre);
        buttonRegistrar = (Button) findViewById(R.id.registroButton);
        buttonEliminar = (Button) findViewById(R.id.eliminarButton);
        buttonEliminar.setVisibility(View.INVISIBLE);

        //Cargar elementos para mostrar los productos guardadas por el usuario
        recyclerView = findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent1 = getIntent();
        listaDetalles = (Lista) intent1.getSerializableExtra("Lista");

       // Intent intent2 = getIntent();
        //productoDetalles = (Long) intent2.getSerializableExtra("Producto");

        if (listaDetalles != null){
            update(listaDetalles);
            buttonEliminar.setVisibility(View.VISIBLE);
        }


        //Cargar productos guardadas por usuario
        //DatabaseReference productoDatabaseReference = mDatabase.getReference("Productos").child(userKey).child(listaId);
        //productoId = productoDetalles.;
        /*
        Query query1 = mDatabaseReference.child(listaId).child("Productos");
        //Query query = mDatabaseReference.child(listaId).child("Productos");
        FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>().setQuery(query1, String.class).build();
        adapterPrep = new productosAdapter(options);
        recyclerView.setAdapter(adapterPrep);
/*
        /*ver = (Button) findViewById(R.id.ver);

        ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GestorLista.this, PruebaActivity.class);
                startActivity(intent);
            }
        });*/



        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = EditTextNombre.getText().toString();
                if (!nombre.isEmpty()){
                    registrarLista();
                }else{
                    Toast.makeText( GestorLista.this, "Ingrese nombre de lista", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertMessage();
            }
        });
    }
/*
    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }*/

    private void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        DeleteData();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(GestorLista.this, "No hubo cambios",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }

            private void DeleteData() {
                listaId = listaDetalles.idLista;
                Query query = mDatabaseReference;
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() > 0){
                            for(DataSnapshot itemSnapshot : dataSnapshot.getChildren()){
                                if (itemSnapshot.getKey().equals(listaId)){
                                    String key = itemSnapshot.getKey();
                                    dataSnapshot.getRef().child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(GestorLista.this, "Lista eliminada", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(GestorLista.this, MenuActivity.class);
                                                startActivity(intent);
                                            }
                                            else
                                            {
                                                Log.e("Task failed", "Task failed", task.getException());
                                                Toast.makeText(GestorLista.this, "internal error"+task.getException(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }else{
                            Toast.makeText(GestorLista.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(GestorLista.this, "Error occurred sorry", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void update(Lista listaDetalles) {
        String n = listaDetalles.getNombre();
        EditTextNombre.setText(n);
    }

    private void registrarLista() {
        if (listaDetalles != null){
            listaId = listaDetalles.idLista;
            mDatabaseReference.child(listaId).child("nombre").setValue(nombre);
            Toast.makeText(GestorLista.this, "Información actualizada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(GestorLista.this, MenuActivity.class));
            finish();
        }else{
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String listaKey = mDatabaseReference.push().getKey();
                Lista lista = new Lista();
                lista.setIdLista(listaKey);
                lista.setNombre(nombre);
                if (snapshot.hasChild(nombre)) {
                    Toast.makeText(GestorLista.this, "Lista ya existe", Toast.LENGTH_SHORT).show();
                }else{
                    mDatabaseReference.child(listaKey).setValue(lista);
                    Toast.makeText(GestorLista.this, "Informacion guardada", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GestorLista.this, MenuActivity.class));
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GestorLista.this, "Lo sentimos, su informacion no pudo ser almacenada. Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        }
    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(GestorLista.this, MenuActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
