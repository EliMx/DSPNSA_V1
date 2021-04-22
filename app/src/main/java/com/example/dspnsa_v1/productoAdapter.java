package com.example.dspnsa_v1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class productoAdapter extends FirebaseRecyclerAdapter<Producto, productoAdapter.productoViewholder> {
    DatabaseReference ref;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userKey = mAuth.getCurrentUser().getUid();
    DatabaseReference refProductos = FirebaseDatabase.getInstance().getReference().child("Productos").child(userKey).getRef();
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

        holder.precioProducto.setText("Precio: $"+model.getPrecio());
        holder.itemView.setOnClickListener(v ->{
            Intent intent = new Intent(v.getContext(), GestorProducto.class);
            intent.putExtra("Producto", model);
            v.getContext().startActivity(intent);
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

        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.check.isChecked()){
                    refProductos.child(model.getIdProducto()).child("idLista_adquirido").setValue(model.idLista+"_true").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            refProductos.child(model.getIdProducto()).child("adquirido").setValue(true);
                            Toast.makeText(v.getContext(),"Se marco como adquirido", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
        CardView cardview;
        public productoViewholder(@NonNull View itemView)
        {
            super(itemView);
            cardview = itemView.findViewById(R.id.cardview);
            nombreProducto = itemView.findViewById(R.id.nombreProducto);
            precioProducto = itemView.findViewById(R.id.precioProducto);
            mScoreText1 = itemView.findViewById(R.id.score_1);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);
            check = (CheckBox) itemView.findViewById(R.id.itemCheckBox);

        }
    }
}
