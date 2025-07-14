package com.example.inventario2025.data.local.entities;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName("valido")
    private boolean valido;
    @SerializedName("idUsuario")
    private Integer idUsuario; // es que puede llegar null, se muere si no

    public Usuario(boolean valido, Integer idUsuario) {
        this.valido = valido;
        this.idUsuario = idUsuario;
    }

    public boolean isValid() {
        return valido;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
}