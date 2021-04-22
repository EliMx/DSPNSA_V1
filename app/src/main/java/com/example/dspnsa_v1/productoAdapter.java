package com.example.dspnsa_v1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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
    //mDatabaseReference = mDatabase.getReference("Listas").child(userKey);
    //mDatabaseReference.child(listaDetalles.idLista).child("Productos");
    int cantidad;
    private int mScore1;

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
                holder.mScoreText1.setText( String.valueOf(cantidad));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*
        ValueEventListener followListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int x = dataSnapshot.getValue(Integer.class);
                String xx = String.valueOf(x);
                System.out.println("-------------------->"+dataSnapshot.getValue(Integer.class));
                holder.mScoreText1.setText(xx);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.child(model.getIdProducto()).addValueEventListener(followListener); //Adds a listener
        */

        holder.precioProducto.setText("Precio: $"+model.getPrecio());
        holder.itemView.setOnClickListener(v ->{
            Intent intent = new Intent(v.getContext(), GestorProducto.class);
            intent.putExtra("Producto", model);
            v.getContext().startActivity(intent);
        });

        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Marcado como adquirido", Toast.LENGTH_SHORT).show();
            }
        });



        holder.increase.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                String pasteData = holder.mScoreText1.getText().toString();
                mScore1 = Integer.parseInt(pasteData);
                mScore1++;
                //mScoreText1.setText(String.valueOf(mScore1));
                Toast.makeText(v.getContext(),"Se actualizo cantidad", Toast.LENGTH_SHORT).show();
                ref.child(model.getIdProducto()).setValue(mScore1);
                holder.mScoreText1.setText(String.valueOf(mScore1));
            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pasteData = holder.mScoreText1.getText().toString();
                mScore1 = Integer.parseInt(pasteData);
                mScore1--;
                //mScoreText1.setText(String.valueOf(mScore1));
                ref.child(model.getIdProducto()).setValue(mScore1);
                holder.mScoreText1.setText(String.valueOf(mScore1));
            }
        });

        //Click largo
        /*
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(),"Long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        */
    }

    @NonNull
    @Override
    public productoViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.producto, parent, false);
        return new productoAdapter.productoViewholder(view);
    }

    class productoViewholder extends RecyclerView.ViewHolder{
        TextView nombreProducto;
        TextView precioProducto;
        TextView mScoreText1;
        Button increase;
        Button decrease;
        CheckBox check;
        public productoViewholder(@NonNull View itemView)
        {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.nombreProducto);
            precioProducto = itemView.findViewById(R.id.precioProducto);
            mScoreText1 = itemView.findViewById(R.id.score_1);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);
            check = (CheckBox) itemView.findViewById(R.id.itemCheckBox);

        }
    }
}
