package com.example.inventario2025.data.remote.api;

import com.example.inventario2025.ui.reporte.ReporteMovimiento;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovimientoService {
    // ✅ Obtener todos los movimientos
    // Endpoint: GET http://200.234.238.128/api/movimientos
    @GET("movimientos")
    Call<List<ReporteMovimiento>> getMovimientos();

    // ✅ Obtener movimientos por fecha (yyyy-MM-dd)
    // Endpoint: GET http://200.234.238.128/api/movimientos?fecha=2025-07-16
    @GET("movimientos")
    Call<List<ReporteMovimiento>> getMovimientosByFecha(
            @Query("fecha") String fecha
    );
}
