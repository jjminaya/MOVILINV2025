package com.example.inventario2025.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class InventarioCreateRequest {

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("userID")
    private int userID;

    public InventarioCreateRequest(String descripcion, int userID) {
        this.descripcion = descripcion;
        this.userID = userID;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getUserID() {
        return userID;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}