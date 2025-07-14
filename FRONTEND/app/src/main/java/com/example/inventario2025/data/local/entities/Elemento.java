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
    @SerializedName("idElementos")
    private int idElemento;

    @ColumnInfo(name = "uniCodeElemento")
    @SerializedName("uniCodeElemento")
    private String uniCodeElemento;

    @ColumnInfo(name = "descripcionElemento")
    @SerializedName("descripcionElemento")
    private String descripcionElemento;

    @ColumnInfo(name = "marcaElemento")
    @SerializedName("marcaElemento")
    private String marcaElemento;

    @ColumnInfo(name = "modeloElemento")
    @SerializedName("modeloElemento")
    private String modeloElemento;

    @ColumnInfo(name = "colorElemento")
    @SerializedName("colorElemento")
    private String colorElemento;

    @ColumnInfo(name = "estadoElemento")
    @SerializedName("estadoElemento")
    private String estadoElemento;

    @ColumnInfo(name = "idInventario")
    @SerializedName("inventarioElemento")
    private int idInventario;

    @ColumnInfo(name = "estado")
    @SerializedName("estado")
    private int estado;

    public Elemento() {}

    public Elemento(int idElemento, int idInventario, String descripcionElemento, int cantidad, int estado) {
        this.idElemento = idElemento;
        this.idInventario = idInventario;
        this.descripcionElemento = descripcionElemento;
        this.estado = estado;
        this.uniCodeElemento = null;
        this.marcaElemento = null;
        this.modeloElemento = null;
        this.colorElemento = null;
        this.estadoElemento = null;
    }

    public Elemento(int idElemento, String uniCodeElemento, String descripcionElemento,
                    String marcaElemento, String modeloElemento, String colorElemento,
                    String estadoElemento, int idInventario, int cantidad, int estado) {
        this.idElemento = idElemento;
        this.uniCodeElemento = uniCodeElemento;
        this.descripcionElemento = descripcionElemento;
        this.marcaElemento = marcaElemento;
        this.modeloElemento = modeloElemento;
        this.colorElemento = colorElemento;
        this.estadoElemento = estadoElemento;
        this.idInventario = idInventario;
        this.estado = estado;
    }

    public int getIdElemento() {
        return idElemento;
    }

    public String getUniCodeElemento() {
        return uniCodeElemento;
    }

    public String getDescripcionElemento() {
        return descripcionElemento;
    }

    public String getMarcaElemento() {
        return marcaElemento;
    }

    public String getModeloElemento() {
        return modeloElemento;
    }

    public String getColorElemento() {
        return colorElemento;
    }

    public String getEstadoElemento() {
        return estadoElemento;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public int getEstado() {
        return estado;
    }

    public void setIdElemento(int idElemento) {
        this.idElemento = idElemento;
    }

    public void setUniCodeElemento(String uniCodeElemento) {
        this.uniCodeElemento = uniCodeElemento;
    }

    public void setDescripcionElemento(String descripcionElemento) {
        this.descripcionElemento = descripcionElemento;
    }

    public void setMarcaElemento(String marcaElemento) {
        this.marcaElemento = marcaElemento;
    }

    public void setModeloElemento(String modeloElemento) {
        this.modeloElemento = modeloElemento;
    }

    public void setColorElemento(String colorElemento) {
        this.colorElemento = colorElemento;
    }

    public void setEstadoElemento(String estadoElemento) {
        this.estadoElemento = estadoElemento;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
