package com.example.inventario2025.data.local.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "inventario")
public class Inventario {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idInventario")
    @SerializedName("idInventario")
    private int idInventario;

    @ColumnInfo(name = "descripcionInventario")
    @SerializedName("descripcionInventario")
    private String descripcionInventario;

    @ColumnInfo(name = "elementosInventario")
    @SerializedName("elementosInventario")
    private int elementosInventario;

    @ColumnInfo(name = "estado")
    @SerializedName("estado")
    private int estado;

    @ColumnInfo(name = "rangoColaborador")
    @SerializedName("rangoColaborador")
    private String rangoColaborador;

    @ColumnInfo(name = "ownerUserId")
    private int ownerUserId;

    public Inventario() {}

    public Inventario(int idInventario, String descripcionInventario, int elementosInventario, int estado, String rangoColaborador, int ownerUserId) {
        this.idInventario = idInventario;
        this.descripcionInventario = descripcionInventario;
        this.elementosInventario = elementosInventario;
        this.estado = estado;
        this.rangoColaborador = rangoColaborador;
        this.ownerUserId = ownerUserId;
    }

    public Inventario(String descripcionInventario, int elementosInventario, int estado, String rangoColaborador) {
        this.descripcionInventario = descripcionInventario;
        this.elementosInventario = elementosInventario;
        this.estado = estado;
        this.rangoColaborador = rangoColaborador;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public String getDescripcionInventario() {
        return descripcionInventario;
    }

    public void setDescripcionInventario(String descripcionInventario) {
        this.descripcionInventario = descripcionInventario;
    }

    public int getElementosInventario() {
        return elementosInventario;
    }

    public void setElementosInventario(int elementosInventario) {
        this.elementosInventario = elementosInventario;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getRangoColaborador() {
        return rangoColaborador;
    }

    public void setRangoColaborador(String rangoColaborador) {
        this.rangoColaborador = rangoColaborador;
    }

    public int getOwnerUserId() { return ownerUserId; }

    public void setOwnerUserId(int ownerUserId) { this.ownerUserId = ownerUserId; }
}