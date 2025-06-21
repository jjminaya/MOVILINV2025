package com.example.inventario2025.data.remote.api;

import com.example.inventario2025.data.remote.models.InventarioRequest;
import com.example.inventario2025.data.remote.models.InventarioResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import java.util.List;

public interface InventorioApiService {

    // Obtiene todos los inventarios activos
    @GET("inventarios") // Endpoint: http://200.234.238.128/api/inventarios
    Call<List<InventarioResponse>> getInventarios();

    // Obtiene la data de un inventario por su ID
    @GET("inventarios/{id}") // Endpoint: http://200.234.238.128/api/inventarios/:id
    Call<InventarioResponse> getInventarioById(@Path("id") int idInventario);

    // Obtiene todos los inventarios activos de un usuario específico
    @GET("inventarios/user/{userID}") // Endpoint: http://200.234.238.128/api/inventarios/user/:userID'
    Call<List<InventarioResponse>> getInventariosByUserId(@Path("userID") int userId);

    // Crea un nuevo inventario (requiere un body con "descripcionInventario")
    @POST("inventarios") // Endpoint: http://200.234.238.128/api/inventarios
    Call<InventarioResponse> createInventario(@Body InventarioRequest inventarioRequest);

    // Modifica la data de un inventario existente (requiere un body con "descripcionInventario")
    @PUT("inventarios/{id}") // Endpoint: http://200.234.238.128/api/inventarios/:id
    Call<InventarioResponse> updateInventario(@Path("id") int idInventario, @Body InventarioRequest inventarioRequest);

    // Elimina un inventario por su ID (retorna Call<Void> si no hay cuerpo de respuesta)
    @DELETE("inventarios/{id}") // Endpoint: http://200.234.238.128/api/inventarios/:id
    Call<Void> deleteInventario(@Path("id") int idInventario);
}