package com.example.inventario2025.ui.reporte;

public class ReporteMovimiento {
    private int id;
    private String fecha;
    private String usuario;      // nombre del usuario
    private String tipoObjeto;   // puede ser Inventario o Elemento
    private String accion;       // Crear, Modificar, Eliminar
    private String descripcion;

    // Constructor vacío (necesario para Retrofit)
    public ReporteMovimiento(int id, String fecha, String usuario, String tipoObjeto, String accion, String descripcion) {
        this.id = id;
        this.fecha = fecha;
        this.usuario = usuario;
        this.tipoObjeto = tipoObjeto;
        this.accion = accion;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getId() { return id; }
    public String getFecha() { return fecha; }
    public String getUsuario() { return usuario; }
    public String getTipoObjeto() { return tipoObjeto; }
    public String getAccion() { return accion; }
    public String getDescripcion() { return descripcion; }

    public void setId(int id) { this.id = id; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setTipoObjeto(String tipoObjeto) { this.tipoObjeto = tipoObjeto; }
    public void setAccion(String accion) { this.accion = accion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
