package com.example.inventario2025.data.local.entities;

import com.google.gson.annotations.SerializedName;

public class Colaborador {

    @SerializedName("idColaboradores")
    private int idColaboradores;
    @SerializedName("username")
    private String username;
    @SerializedName("nombresPersona")
    private String nombresPersona;
    @SerializedName("apellidosPersona")
    private String apellidosPersona;
    @SerializedName("rangoColaborador")
    private String rangoColaborador;

    public Colaborador(int idColaboradores, String username, String nombresPersona, String apellidosPersona, String rangoColaborador) {
        this.idColaboradores = idColaboradores;
        this.username = username;
        this.nombresPersona = nombresPersona;
        this.apellidosPersona = apellidosPersona;
        this.rangoColaborador = rangoColaborador;
    }

    public int getIdColaboradores() {
        return idColaboradores;
    }

    public String getUsername() {
        return username;
    }

    public String getNombresPersona() {
        return nombresPersona;
    }

    public String getApellidosPersona() {
        return apellidosPersona;
    }

    public String getRangoColaborador() {
        return rangoColaborador;
    }

    public void setIdColaboradores(int idColaboradores) {
        this.idColaboradores = idColaboradores;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNombresPersona(String nombresPersona) {
        this.nombresPersona = nombresPersona;
    }

    public void setApellidosPersona(String apellidosPersona) {
        this.apellidosPersona = apellidosPersona;
    }

    public void setRangoColaborador(String rangoColaborador) {
        this.rangoColaborador = rangoColaborador;
    }
}