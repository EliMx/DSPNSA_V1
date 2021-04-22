package com.example.dspnsa_v1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class GestorLista extends AppCompatActivity {

    //Componentes del registro
    private EditText nombreListaTextNombre;
    private CheckBox check;
    private AppCompatImageButton buttonRegistrar, buttonEliminar, ver;

    //variables del registro
    private String nombre = "";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private String userKey = mAuth.getCurrentUser().getUid();
    private DatabaseReference mDatabaseReference = mDatabase.getReference("Listas").child(userKey);
    private DatabaseReference productosRef = mDatabase.getReference("Productos").child(userKey);

    private Lista listaDetalles;
    private String listaId;
    private String productoId;

    private RecyclerView recyclerView;
    private TextView emptyView;
    private productoAdapter adapProd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestorlista);
        this.setTitle("Nueva lista");

        mDatabaseReference.keepSynced(true);
        mDatabaseReference.keepSynced(false);

        nombreListaTextNombre = (EditText) findViewById(R.id.inputNombre);
        buttonRegistrar = findViewById(R.id.registroButton);
        buttonEliminar = findViewById(R.id.eliminarButton);
        buttonEliminar.setVisibility(View.INVISIBLE);

        // Cargar elementos para mostrar listas guardadas por el usuario
        //listViewProductos = (ListView) findViewById(R.id.recycler1);
        recyclerView = findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Cargar detalles de lista seleccionada en menu inicial
        Intent intent1 = getIntent();
        listaDetalles = (Lista) intent1.getSerializableExtra("Lista");

        if (listaDetalles != null) {
            update(listaDetalles);
            buttonEliminar.setVisibility(View.VISIBLE);
            String idLista_adquirido = listaDetalles.idLista+"_false";
            Query queryRefProductos = productosRef.orderByChild("idLista_adquirido").equalTo(idLista_adquirido);
            //Query queryRefProductos2 = queryRefProductos.orderByChild("adquirico").equalTo(false);
            Query queryRefLista = mDatabaseReference.child(listaDetalles.idLista).child("Productos");
            FirebaseRecyclerOptions<Producto> options = new FirebaseRecyclerOptions.Builder<Producto>().setQuery(queryRefProductos, Producto.class).build();
            adapProd = new productoAdapter(options, queryRefLista);
            recyclerView.setAdapter(adapProd);
            //Activar accion para deslizar lista a la izq para eliminar
            enableSwipe(queryRefProductos, queryRefLista);
        }else{
            //registrarLista();
        }

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        botones();
    }

    private void botones() {
        //Boton actualizar lista
        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = nombreListaTextNombre.getText().toString();
                if (!nombre.isEmpty()) {
                    registrarLista();
                } else {
                    Toast.makeText(GestorLista.this, "Ingrese nombre de lista", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton eliminar lista
        buttonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertMessage();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (listaDetalles!=null){
            adapProd.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listaDetalles!=null){
            adapProd.stopListening();
        }
    }

    private void enableSwipe(Query query, Query queryProductoEliminar) {
        ItemTouchHelper mIth = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT) {
                    public boolean onMove(RecyclerView recyclerView,
                                          ViewHolder viewHolder, ViewHolder target) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        final int toPos = target.getAdapterPosition();
                        // move item in `fromPos` to `toPos` in adapter.
                        return true;// true if moved, false otherwise
                    }
                    public void onSwiped(ViewHolder viewHolder, int direction) {
                        // remove from adapter
                        String postKey = adapProd.getRef(viewHolder.getAdapterPosition()).getKey();
                        //Referencias para eliminar de nodo productos
                        DatabaseReference refProductoEliminar = query.getRef();
                        //Referencias para eliminar de nodo Listas Referencia a producto
                        DatabaseReference refListaProductoEliminar = queryProductoEliminar.getRef();
                        //Remover de lista
                        refListaProductoEliminar.child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    //Your data is removed successfully!
                                    Toast.makeText(GestorLista.this, "Lista actualizada", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //Remover de productos
                        refProductoEliminar.child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    //Your data is removed successfully!
                                    Toast.makeText(GestorLista.this, "Producto eliminada", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
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
        nombreListaTextNombre.setText(n);
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

    // Habilitar funcion de volver atras al presionar boton <-
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
