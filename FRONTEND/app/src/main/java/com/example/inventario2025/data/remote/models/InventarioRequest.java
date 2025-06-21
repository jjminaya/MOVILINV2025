package com.example.inventario2025.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class InventarioRequest {

    @SerializedName("descripcionInventario")
    private String descripcionInventario;

    // Constructor
    public InventarioRequest(String descripcionInventario) {
        this.descripcionInventario = descripcionInventario;
    }

    // Getter
    public String getDescripcionInventario() {
        return descripcionInventario;
    }

    // Setter
    public void setDescripcionInventario(String descripcionInventario) {
        this.descripcionInventario = descripcionInventario;
    }
}