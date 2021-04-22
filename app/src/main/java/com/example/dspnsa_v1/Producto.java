package com.example.dspnsa_v1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.Date;

public class Producto implements Serializable {
    String idProducto;
    String idLista;
    String nombre;
    int codigoBarra;
    String imagen;
    int categoria;
    Date fechaAdquirido;
    Date fechaCaducidad;
    Boolean adquirido;
    int tipo;
    Float precio;
    String comentario;
    String lugarcompra;
    String Pasillo;
    String almacenaje;
    String idLista_adquirido;

    public Producto() {
    }

    public Producto(String idProducto, String nombre, Float precio, String idLista) {
        this.idProducto = idProducto;
        this.idLista = idLista;
        this.nombre = nombre;
        this.precio = precio;
    }

    public Producto(String idProducto, String idLista, String nombre, int codigoBarra, String imagen, int categoria, Date fechaAdquirido,
                    Date fechaCaducidad, int tipo, Float precio, String comentario, String lugarcompra, String pasillo, String almacenaje, Boolean adquirido, String idLista_adquirido) {
        this.idProducto = idProducto;
        this.idLista = idLista;
        this.nombre = nombre;
        this.codigoBarra = codigoBarra;
        this.imagen = imagen;
        this.categoria = categoria;
        this.fechaAdquirido = fechaAdquirido;
        this.fechaCaducidad = fechaCaducidad;
        this.tipo = tipo;
        this.precio = precio;
        this.comentario = comentario;
        this.lugarcompra = lugarcompra;
        Pasillo = pasillo;
        this.almacenaje = almacenaje;
        this.adquirido = adquirido;
        this.idLista_adquirido = idLista_adquirido;
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

    public int getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(int codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public Date getFechaAdquirido() {
        return fechaAdquirido;
    }

    public void setFechaAdquirido(Date fechaAdquirido) {
        this.fechaAdquirido = fechaAdquirido;
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getLugarcompra() {
        return lugarcompra;
    }

    public void setLugarcompra(String lugarcompra) {
        this.lugarcompra = lugarcompra;
    }

    public String getPasillo() {
        return Pasillo;
    }

    public void setPasillo(String pasillo) {
        Pasillo = pasillo;
    }

    public String getAlmacenaje() {
        return almacenaje;
    }

    public void setAlmacenaje(String almacenaje) {
        this.almacenaje = almacenaje;
    }

    public Boolean getAdquirido() {
        return adquirido;
    }

    public void setAdquirido(Boolean adquirido) {
        this.adquirido = adquirido;
    }

    public String getIdLista_adquirido() {
       return idLista_adquirido;
    }

    public void setIdLista_adquirido(String idLista_adquirido) {
        this.idLista_adquirido = idLista_adquirido;
    }
}
