package com.example.dspnsa_v1;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// FirebaseRecyclerAdapter is a class provided by
// FirebaseUI. it provides functions to bind, adapt and show
// database contents in a Recycler View
public class listaAdapter extends FirebaseRecyclerAdapter<Lista, listaAdapter.listaViewholder> {

    public listaAdapter(
            @NonNull FirebaseRecyclerOptions<Lista> options)
    {
        super(options);
    }

    // Function to bind the view in Card view(here
    // "person.xml") iwth data in
    // model class(here "person.class")
    @Override
    protected void
    onBindViewHolder(@NonNull listaViewholder holder, int position, @NonNull Lista model)
    {
        holder.firstname.setText(model.getNombre());

        holder.itemView.setOnClickListener(v ->{
            Intent intent = new Intent(v.getContext(), GestorLista.class);
            intent.putExtra("Lista", model);
            v.getContext().startActivity(intent);
        });

    }

    // Function to tell the class about the Card view (here
    // "person.xml")in
    // which the data will be shown
    @NonNull
    @Override
    public listaViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista, parent, false);
        return new listaAdapter.listaViewholder(view);
    }

    // Sub Class to create references of the views in Crad
    // view (here "person.xml")
    class listaViewholder extends RecyclerView.ViewHolder{
        TextView firstname;
        public listaViewholder(@NonNull View itemView)
        {
            super(itemView);
            firstname = itemView.findViewById(R.id.textview);
        }
    }

}
