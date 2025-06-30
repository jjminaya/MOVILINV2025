package com.example.inventario2025.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.inventario2025.data.local.dao.InventarioDao;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.data.remote.api.InventorioApiService;
import com.example.inventario2025.data.remote.models.InventarioResponse;
import com.example.inventario2025.data.remote.models.InventarioRequest;
import com.example.inventario2025.data.remote.models.InventarioCreateRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventarioRepository {

    private final InventarioDao inventoryDao;
    private final InventorioApiService inventoryApiService;

    private static final String TAG = "InventarioRepository";

    private static final ExecutorService IO_EXECUTOR = Executors.newSingleThreadExecutor();

    public InventarioRepository(InventarioDao inventoryDao, InventorioApiService inventoryApiService) {
        this.inventoryDao = inventoryDao;
        this.inventoryApiService = inventoryApiService;
    }

    // Metodo para obtener todos los inventarios (desde la API principal, luego a Room)
    public LiveData<List<Inventario>> getInventories() {
        refreshInventoriesFromServer(); // Esto dispara la llamada a la API y guarda en Room
        return inventoryDao.getAllInventories(); // Siempre observar Room para la UI
    }

    public void getInventoriesByUserId(int userId, OnOperationCompleteListener listener) {
        inventoryApiService.getInventariosByUserId(userId).enqueue(new retrofit2.Callback<List<com.example.inventario2025.data.remote.models.InventarioResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.example.inventario2025.data.remote.models.InventarioResponse>> call, retrofit2.Response<List<com.example.inventario2025.data.remote.models.InventarioResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.inventario2025.data.remote.models.InventarioResponse> apiInventarios = response.body();
                    Log.d(TAG, "API Response (Listener method) RECEIVED for user " + userId + ". Count: " + apiInventarios.size()); // NUEVO LOG 1

                    IO_EXECUTOR.execute(() -> {
                        inventoryDao.deleteAll(); // Borrará todos los inventarios locales
                        for (com.example.inventario2025.data.remote.models.InventarioResponse apiInv : apiInventarios) {
                            // NUEVO LOG 2: Valor del rango ANTES de crear la entidad Room (en el método del Listener)
                            Log.d(TAG, "Listener_Method - API_Inv ID: " + apiInv.getIdInventario() + ", Rango_API: '" + apiInv.getRangoColaborador() + "'");

                            Inventario inventario = new Inventario(
                                    apiInv.getIdInventario(),
                                    apiInv.getDescripcionInventario(),
                                    apiInv.getElementosInventario(),
                                    apiInv.getEstado(),
                                    apiInv.getRangoColaborador(), // Este valor viene directamente del API
                                    userId // Este es el ownerUserId
                            );
                            inventoryDao.insert(inventario);
                            // NUEVO LOG 3: Valor del rango DESPUÉS de crear la entidad Room y justo ANTES de insertar (en el método del Listener)
                            Log.d(TAG, "Listener_Method - Room_Entity ID: " + inventario.getIdInventario() + ", Rango_Room_After_Conversion: '" + inventario.getRangoColaborador() + "'");
                        }
                    });
                    if (listener != null) listener.onSuccess();
                } else {
                    String errorMessage = "Error al obtener inventarios por usuario (Listener method): " + response.code() + " - " + response.message();
                    System.err.println("API Error (getInventoriesByUserId - Listener method): " + errorMessage);
                    if (listener != null) listener.onFailure(errorMessage);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.example.inventario2025.data.remote.models.InventarioResponse>> call, Throwable t) {
                String errorMessage = "Error de red al obtener inventarios por usuario (Listener method): " + t.getMessage();
                System.err.println("Network Error (getInventoriesByUserId - Listener method): " + errorMessage);
                if (listener != null) listener.onFailure(errorMessage);
            }
        });
    }

    private void refreshInventoriesFromServer() {
        inventoryApiService.getInventarios().enqueue(new Callback<List<InventarioResponse>>() {
            @Override
            public void onResponse(Call<List<InventarioResponse>> call, Response<List<InventarioResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Inventario> inventoriesToInsert = new ArrayList<>();
                    for (InventarioResponse apiResponse : response.body()) {
                        inventoriesToInsert.add(new Inventario(
                                apiResponse.getIdInventario(),
                                apiResponse.getDescripcionInventario(),
                                apiResponse.getElementosInventario(),
                                apiResponse.getEstado(),
                                apiResponse.getRangoColaborador(),
                                0
                        ));
                    }
                    IO_EXECUTOR.execute(() -> {
                        inventoryDao.insertAll(inventoriesToInsert);
                    });
                } else {
                    System.err.println("API Error (refresh all): " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<InventarioResponse>> call, Throwable t) {
                System.err.println("Network Error (refresh all): " + t.getMessage());
            }
        });
    }

    // Metodo para obtener inventarios por ID de usuario (desde la API, luego a Room)
    // Esto siempre recargará y luego la UI observará Room
    public LiveData<List<Inventario>> getInventoriesByUserId(int userId) {
        inventoryApiService.getInventariosByUserId(userId).enqueue(new Callback<List<InventarioResponse>>() {
            @Override
            public void onResponse(Call<List<InventarioResponse>> call, Response<List<InventarioResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Inventario> inventoriesToInsert = new ArrayList<>();
                    Log.d(TAG, "API Response (LiveData method) RECEIVED for user " + userId + ". Count: " + response.body().size()); // NUEVO LOG 4

                    for (InventarioResponse apiResponse : response.body()) {
                        // NUEVO LOG 5: Valor del rango ANTES de crear la entidad Room (en el método LiveData)
                        Log.d(TAG, "LiveData_Method - API_Inv ID: " + apiResponse.getIdInventario() + ", Rango_API: '" + apiResponse.getRangoColaborador() + "'");

                        inventoriesToInsert.add(new Inventario(
                                apiResponse.getIdInventario(),
                                apiResponse.getDescripcionInventario(),
                                apiResponse.getElementosInventario(),
                                apiResponse.getEstado(),
                                apiResponse.getRangoColaborador(),
                                userId
                        ));
                        // NUEVO LOG 6: Valor del rango DESPUÉS de crear la entidad Room (en el método LiveData)
                        Inventario tempInventario = inventoriesToInsert.get(inventoriesToInsert.size() - 1);
                        Log.d(TAG, "LiveData_Method - Room_Entity ID: " + tempInventario.getIdInventario() + ", Rango_Room_After_Conversion: '" + tempInventario.getRangoColaborador() + "'");
                    }
                    IO_EXECUTOR.execute(() -> {
                        // Agregamos deleteAll aquí también para asegurar una base de datos limpia durante la depuración
                        inventoryDao.deleteAll();
                        inventoryDao.insertAll(inventoriesToInsert); // Insertar/actualizar en Room
                    });
                } else {
                    System.err.println("API Error (by user id - LiveData method): " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<InventarioResponse>> call, Throwable t) {
                System.err.println("Network Error (by user id - LiveData method): " + t.getMessage());
            }
        });
        return inventoryDao.getInventariosByUserId(userId);
    }

    public void createInventarioOWNR(String description, int userId, OnOperationCompleteListener listener) {
        InventarioCreateRequest request = new InventarioCreateRequest(description, userId);
        inventoryApiService.createInventarioOWNR(request).enqueue(new Callback<InventarioResponse>() {
            @Override
            public void onResponse(Call<InventarioResponse> call, Response<InventarioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InventarioResponse createdInventarioResponse = response.body();
                    IO_EXECUTOR.execute(() -> {
                        Inventario createdInventario = new Inventario(
                                createdInventarioResponse.getIdInventario(),
                                createdInventarioResponse.getDescripcionInventario(),
                                createdInventarioResponse.getElementosInventario(),
                                createdInventarioResponse.getEstado(),
                                createdInventarioResponse.getRangoColaborador(),
                                userId
                        );
                        inventoryDao.insert(createdInventario);
                    });
                    if (listener != null) listener.onSuccess();
                } else {
                    String errorMsg = "Error al crear inventario: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.err.println("API Error (createInventarioOWNR): " + errorMsg);
                    if (listener != null) listener.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<InventarioResponse> call, Throwable t) {
                System.err.println("Network Error (createInventarioOWNR): " + t.getMessage());
                if (listener != null) listener.onFailure("Error de red al crear inventario: " + t.getMessage());
            }
        });
    }

    public void updateInventario(int id, String description, OnOperationCompleteListener listener) {
        //por implementar
    }

    public void deleteInventario(int id, OnOperationCompleteListener listener) {
        //por implementar
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onFailure(String message);
    }
}