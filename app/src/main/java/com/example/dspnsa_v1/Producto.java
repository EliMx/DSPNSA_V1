package com.example.dspnsa_v1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public class Producto {
    String idProducto;
    String idLista;
    String nombre;
    /*int codigoBarra;
    String imagen;
    int categoria;
    Date fechaAdquirido;
    Date fechaCaducidad;
    int tipo;*/
    Float precio;
    /*String comentario;
    String lugarcompra;
    String Pasillo;
    string almacenaje;*/

    public Producto() {
    }

    public Producto(String idProducto, String nombre, Float precio, String idLista) {
        this.idProducto = idProducto;
        this.idLista = idLista;
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getIdLista() {
        return idLista;
    }
    public void setIdLista(String idLista) {
        this.idLista = idLista;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Float getPrecio() {
        return precio;
    }
    public void setPrecio(Float precio) {
        this.precio = precio;
    }

}
