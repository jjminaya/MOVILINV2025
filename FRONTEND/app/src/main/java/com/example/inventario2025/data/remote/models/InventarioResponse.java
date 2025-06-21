package com.example.inventario2025.data.remote.models;

import com.example.inventario2025.data.local.entities.Inventario;
import com.google.gson.annotations.SerializedName;

public class InventarioResponse {

    @SerializedName("idInventario")
    private int idInventario;
    @SerializedName("descripcionInventario")
    private String descripcionInventario;
    @SerializedName("elementosInventario")
    private int elementosInventario;
    @SerializedName("estado")
    private int estado;
    @SerializedName("rangoColaborador")
    private String rangoColaborador;

    // Constructor vacío (necesario para Gson)
    public InventarioResponse() {}

    public InventarioResponse(int idInventario, String descripcionInventario, int elementosInventario, int estado, String rangoColaborador) {
        this.idInventario = idInventario;
        this.descripcionInventario = descripcionInventario;
        this.elementosInventario = elementosInventario;
        this.estado = estado;
        this.rangoColaborador = rangoColaborador;
    }

    // Getters
    public int getIdInventario() { return idInventario; }
    public String getDescripcionInventario() { return descripcionInventario; }
    public int getElementosInventario() { return elementosInventario; } // Corrección del typo 'elementosInventos'
    public int getEstado() { return estado; }
    public String getRangoColaborador() { return rangoColaborador; }

    // Setters
    public void setIdInventario(int idInventario) { this.idInventario = idInventario; }
    public void setDescripcionInventario(String descripcionInventario) { this.descripcionInventario = descripcionInventario; }
    public void setElementosInventario(int elementosInventario) { this.elementosInventario = elementosInventario; }
    public void setEstado(int estado) { this.estado = estado; }
    public void voidRangoColaborador(String rangoColaborador) { this.rangoColaborador = rangoColaborador; } // Corrección del typo 'voidRangoColaborador' a 'setRangoColaborador'

    // Metodo para convertir a entidad local 'Inventario'
    public Inventario toInventario() {
        return new Inventario(
                this.idInventario,
                this.descripcionInventario,
                this.elementosInventario,
                this.estado,
                this.rangoColaborador
        );
    }
}