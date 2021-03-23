package com.example.dspnsa_v1;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.io.Serializable;
public class productosAdapter{
/*public class productosAdapter extends FirebaseRecyclerAdapter<String, productosAdapter.productoViewholder> {

    public productosAdapter(
            @NonNull FirebaseRecyclerOptions<String> options) {
        super(options);
    }

    // Function to bind the view in Card view(here
    // "person.xml") iwth data in
    // model class(here "person.class")
    @Override
    protected void
    onBindViewHolder(@NonNull productosAdapter.productoViewholder holder, int position, @NonNull String model) {
        holder.firstname.setText(model);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), GestorLista.class);
            intent.putExtra("String", (Serializable) model);
            v.getContext().startActivity(intent);
        });
    }

    // Function to tell the class about the Card view (here
    // "person.xml")in
    // which the data will be shown
    @NonNull
    @Override
    public productosAdapter.productoViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.producto, parent, false);
        return new productosAdapter.productoViewholder(view);
    }

    // Sub Class to create references of the views in Crad
    // view (here "person.xml")
    class productoViewholder extends RecyclerView.ViewHolder {
        TextView firstname;

        public productoViewholder(View view) {
            super(view);
            firstname = itemView.findViewById(R.id.textview);
        }
        /*public productosAdapter(@NonNull View itemView)
        {
            super(itemView);
            firstname = itemView.findViewById(R.id.textview);
        }*/
/*
        public productoViewholder(View view) {
            super(view);
            firstname = itemView.findViewById(R.id.textview);
        }
    }*/

}
