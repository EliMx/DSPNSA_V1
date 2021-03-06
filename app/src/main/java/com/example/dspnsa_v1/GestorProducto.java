package com.example.dspnsa_v1;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorProducto extends AppCompatActivity {
    //Componentes del registro
    private TextInputEditText EditTextNombre, EditTextPrecio;
    private Button buttonRegistrar, buttonEliminar;

    //variables del registro
    private String nombre = "";
    private float precio = 0;
    private String listaId;
    private String productoId;

    Spinner dropdow;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    String userKey  = mAuth.getCurrentUser().getUid();
    private DatabaseReference mDatabaseReference = mDatabase.getReference("Listas").child(userKey);
    DatabaseReference mDatabaseReferenceProductos = mDatabase.getReference("Productos").child(userKey);
    private List<String> nomeConsulta = new ArrayList<String>();
    private ArrayAdapter<String> dataAdapter;

    // Member variables for holding the score
    private int mScore1;
    // Member variables for holding the score
    private TextView mScoreText1;

    private Producto productoDetalles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestorproducto);
        this.setTitle("Nuevo producto");

        //Find the TextViews by ID
        mScoreText1 = (TextView)findViewById(R.id.score_1);

        EditTextNombre = (TextInputEditText) findViewById(R.id.inputNombre);
        EditTextPrecio = (TextInputEditText) findViewById(R.id.inputPrecio);
        buttonRegistrar = (Button) findViewById(R.id.registroButton);
        buttonEliminar = (Button) findViewById(R.id.eliminarButton);
        buttonEliminar.setVisibility(View.INVISIBLE);

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nomeConsulta);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdow = findViewById(R.id.spinner1);
        dropdow.setAdapter(dataAdapter);

        //Cargar detalles de lista seleccionada en menu inicial
        Intent intent1 = getIntent();
        productoDetalles = (Producto) intent1.getSerializableExtra("Producto");
        if (productoDetalles != null) {
            buttonRegistrar.setText("Actualizar");
            updateVista(productoDetalles);
        }else{
        }

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Lista data = snapshot.getValue(Lista.class);
                    nomeConsulta.add(data.getNombre());
                }
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                precio = Float.parseFloat(EditTextPrecio.getText().toString());
                if (!nombre.isEmpty()){
                    registrarProducto();
                }else{
                    Toast.makeText( GestorProducto.this, "Ingrese nombre de producto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateVista(Producto productoDetalles) {
        EditTextNombre.setText(productoDetalles.getNombre());
        EditTextPrecio.setText(productoDetalles.getPrecio().toString());

    }

    private void registrarProducto() {
        if (productoDetalles != null){
            listaId = productoDetalles.idLista;
            mDatabaseReference.child(listaId).child("nombre").setValue(nombre);
            Toast.makeText(GestorProducto.this, "Informaci??n actualizada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(GestorProducto.this, MenuActivity.class));
            finish();
        }else{
            mDatabaseReferenceProductos.orderByChild("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean exists = false;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        //Verificar si producto ya existe
                        if ((ds.getValue(Producto.class).getNombre()).equals(nombre)) {
                            exists = true;
                            break;
                        } else {
                            exists = false;
                        }
                    }
                    if (exists) {
                        Toast.makeText(GestorProducto.this, "Producto ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        //Crear producto
                        String productoKey = mDatabaseReferenceProductos.push().getKey();
                        Producto producto = new Producto();
                        producto.setIdProducto(productoKey);
                        producto.setNombre(nombre);
                        producto.setPrecio(precio);
                        producto.setAdquirido(false);
                        mDatabaseReferenceProductos.child(productoKey).setValue(producto);
                        Toast.makeText(GestorProducto.this, "Informacion guardada", Toast.LENGTH_SHORT).show();
                        agregarProductoLista(producto);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(GestorProducto.this, "Lo sentimos, su informacion no pudo ser almacenada. Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void agregarProductoLista(Producto producto) {
        Map<Producto, Integer> productosInventario = new HashMap<>();

        int cantidad = Integer.parseInt(String.valueOf(mScoreText1.getText()));
        productosInventario.put(producto, cantidad);

        String listaNombre = (String) dropdow.getSelectedItem();

        Query query = mDatabaseReference.orderByChild("nombre").equalTo(listaNombre);
        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() > 0){
                    for(DataSnapshot itemSnapshot : snapshot.getChildren()){
                        String key = itemSnapshot.getKey();
                        String inventarioKey = mDatabaseReference.child(key).push().getKey();
                        //Actualizar informacion de producto con id de lista seleccionada en dropdown en base de datos
                        Map<String, Object> productoUpdates = new HashMap<>();
                        productoUpdates.put("idLista", key);
                        productoUpdates.put("idLista_adquirido", key+"_"+producto.getAdquirido());
                        mDatabaseReferenceProductos.child(producto.idProducto).updateChildren(productoUpdates);
                        //Guardar referencia de producto en el objeto lista en base de datos
                        snapshot.getRef().child(key).child("Productos").child(producto.idProducto).setValue(cantidad).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(GestorProducto.this, "Producto agregado a lista", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(GestorProducto.this, MenuActivity.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Log.e("Task failed", "Task failed", task.getException());
                                    Toast.makeText(GestorProducto.this, "internal error"+task.getException(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(GestorProducto.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GestorProducto.this, "Error occurred sorry", Toast.LENGTH_SHORT).show();

            }
        });
    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(GestorProducto.this, MenuActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void decreaseScore(View view) {
        // Get the ID of the button that was clicked
        int viewID = view.getId();
        switch (viewID){
            //If it was on Team 1
            case R.id.decrease:
                //Decrement the score and update the TextView
                mScore1--;
                mScoreText1.setText(String.valueOf(mScore1));
                break;
        }
    }

    public void increaseScore(View view) {
        //Get the ID of the button that was clicked
        int viewID = view.getId();
        switch (viewID) {
            //If it was on Team 1
            case R.id.increase:
                //Increment the score and update the TextView
                mScore1++;
                mScoreText1.setText(String.valueOf(mScore1));
                break;
        }
    }
}