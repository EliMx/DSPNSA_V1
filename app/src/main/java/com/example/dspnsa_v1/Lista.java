package com.example.dspnsa_v1;

import androidx.annotation.VisibleForTesting;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Lista implements Serializable {
    String idLista;
    String Nombre;
    int idProductos;
    Date fechaAgendada;
    int Prioridad;
    Float costoTotal;
    //Map<Producto, Integer> Productos;
    Map<String, Integer> Productos;
    Boolean isDone;

    public Lista() {
    }

    public Lista(String idLista, String nombre, int idProductos, Date fechaAgendada, int prioridad, Float costoTotal,
                 Map<String, Integer> productos, Boolean isDone) {
        this.idLista = idLista;
        this.Nombre = nombre;
        this.idProductos = idProductos;
        this.fechaAgendada = fechaAgendada;
        this.Prioridad = prioridad;
        this.costoTotal = costoTotal;
        this.Productos = productos;
        this.isDone = isDone;
    }

    public String getIdLista() {
        return idLista;
    }

    public void setIdLista(String idLista) {
        this.idLista = idLista;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public int getIdProductos() {
        return idProductos;
    }

    public void setIdProductos(int idProductos) {
        this.idProductos = idProductos;
    }

    public Date getFechaAgendada() {
        return fechaAgendada;
    }

    public void setFechaAgendada(Date fechaAgendada) {
        this.fechaAgendada = fechaAgendada;
    }

    public int getPrioridad() {
        return Prioridad;
    }

    public void setPrioridad(int prioridad) {
        Prioridad = prioridad;
    }

    public Float getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(Float costoTotal) {
        this.costoTotal = costoTotal;
    }

    public Map<String, Integer> getProductos() {
        return Productos;
    }

    public void setProductos(Map<String, Integer> productos) {
        Productos = productos;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    @Override
    public String toString() {
        return this.Nombre;
    }

}