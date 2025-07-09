package com.example.inventario2025.data.remote.api;

import com.example.inventario2025.data.local.entities.Colaborador;
import com.example.inventario2025.data.remote.models.InventarioRequest;
import com.example.inventario2025.data.remote.models.InventarioResponse;
import com.example.inventario2025.data.remote.models.InventarioCreateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import java.util.List;
import java.util.Map;

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

    // Crea un nuevo inventario con descripción y userID
    @POST("inventarios/newOWNRInv")// Mismo endpoint POST para crear, pero con un body diferente
    Call<InventarioResponse> createInventarioOWNR(@Body InventarioCreateRequest request);

    // Modifica la data de un inventario existente (requiere un body con "descripcionInventario")
    @PUT("inventarios/{id}") // Endpoint: http://200.234.238.128/api/inventarios/:id
    Call<Void> updateInventario(@Path("id") int inventarioId, @Body Map<String, String> body); // Ajusta el tipo de retorno si tu API devuelve algo

    // Elimina un inventario por su ID (retorna Call<Void> si no hay cuerpo de respuesta)
    @DELETE("inventarios/{id}") // Endpoint: http://200.234.238.128/api/inventarios/:id
    Call<Void> deleteInventario(@Path("id") int idInventario);

    // Endpoint para obtener colaboradores de un inventario http://200.234.238.128/api/inventarios/inventario/:inventarioID
    @GET("inventarios/inventario/{inventarioID}")
    Call<List<Colaborador>> getColaboradoresByInventarioId(@Path("inventarioID") int inventarioId);
}