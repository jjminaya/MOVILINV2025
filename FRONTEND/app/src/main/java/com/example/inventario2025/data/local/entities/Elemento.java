package com.example.inventario2025.data.local.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

@Entity(tableName = "elemento")
public class Elemento implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idElemento")
    @SerializedName("idElemento")
    private int idElemento;

    @ColumnInfo(name = "idInventario")
    @SerializedName("idInventario")
    private int idInventario;

    @ColumnInfo(name = "descripcionElemento")
    @SerializedName("descripcionElemento")
    private String descripcionElemento;

    @ColumnInfo(name = "cantidad")
    @SerializedName("cantidad")
    private int cantidad;

    @ColumnInfo(name = "estado")
    @SerializedName("estado")
    private int estado;

    public Elemento() {}

    public Elemento(int idElemento, int idInventario, String descripcionElemento, int cantidad, int estado) {
        this.idElemento = idElemento;
        this.idInventario = idInventario;
        this.descripcionElemento = descripcionElemento;
        this.cantidad = cantidad;
        this.estado = estado;
    }

    public int getIdElemento() {
        return idElemento;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public String getDescripcionElemento() {
        return descripcionElemento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getEstado() {
        return estado;
    }

    public void setIdElemento(int idElemento) {
        this.idElemento = idElemento;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public void setDescripcionElemento(String descripcionElemento) {
        this.descripcionElemento = descripcionElemento;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
