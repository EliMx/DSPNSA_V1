package com.example.dspnsa_v1;

import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class productoAdapter extends FirebaseRecyclerAdapter<Producto, productoAdapter.productoViewholder> {
    DatabaseReference ref;
    int cantidad;
    public productoAdapter(@NonNull FirebaseRecyclerOptions<Producto> options, Query query)
    {
        super(options);
        ref = query.getRef();
    }

    @Override
    protected void onBindViewHolder(@NonNull productoViewholder holder, int position, @NonNull Producto model) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cantidad = snapshot.child(model.getIdProducto()).getValue(Integer.class);
                holder.nombreProducto.setText(cantidad+" "+model.getNombre());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //holder.nombreProducto.setText(cantidad+" "+model.getNombre());
        holder.cantidadProducto.setText("Precio: $"+model.getPrecio());
        holder.itemView.setOnClickListener(v ->{
            Intent intent = new Intent(v.getContext(), GestorProducto.class);
            intent.putExtra("Producto", model);
            v.getContext().startActivity(intent);
        });
    }

    @NonNull
    @Override
    public productoViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.producto, parent, false);
        return new productoAdapter.productoViewholder(view);
    }

    class productoViewholder extends RecyclerView.ViewHolder{
        TextView nombreProducto;
        TextView cantidadProducto;
        public productoViewholder(@NonNull View itemView)
        {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.nombreProducto);
            cantidadProducto = itemView.findViewById(R.id.cantidadProducto);
        }
    }
}
