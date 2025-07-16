package com.example.inventario2025.data.local.entities;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName("valido")
    private boolean valido;

    @SerializedName("idUsuario")
    private Integer idUsuario;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("tipoUsuario")
    private String tipoUsuario;

    @SerializedName("idPersona")
    private int idPersona;

    @SerializedName("estado")
    private int estado;

    public Usuario() {}

    // Getters

    public boolean isValid() {
        return valido;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public int getIdPersona() {
        return idPersona;
    }

    public int getEstado() {
        return estado;
    }

    // Setters
    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}