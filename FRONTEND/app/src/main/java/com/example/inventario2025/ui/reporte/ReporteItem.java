package com.example.inventario2025.ui.reporte;

public class ReporteItem {
    private String titulo;
    private String fecha;
    private String detalle;

    public ReporteItem(String titulo, String fecha, String detalle) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.detalle = detalle;
    }

    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public String getDetalle() { return detalle; }
}
