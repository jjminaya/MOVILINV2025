package com.example.inventario2025.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.inventario2025.data.local.dao.InventarioDao;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.data.remote.api.InventorioApiService;
import com.example.inventario2025.data.remote.models.InventarioResponse;
import com.example.inventario2025.data.remote.models.InventarioRequest;

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
                                apiResponse.getRangoColaborador()
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
                    for (InventarioResponse apiResponse : response.body()) {
                        inventoriesToInsert.add(new Inventario(
                                apiResponse.getIdInventario(),
                                apiResponse.getDescripcionInventario(),
                                apiResponse.getElementosInventario(),
                                apiResponse.getEstado(),
                                apiResponse.getRangoColaborador()
                        ));
                    }
                    IO_EXECUTOR.execute(() -> {
                        inventoryDao.insertAll(inventoriesToInsert); // Insertar/actualizar en Room
                    });
                } else {
                    System.err.println("API Error (by user id): " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<InventarioResponse>> call, Throwable t) {
                System.err.println("Network Error (by user id): " + t.getMessage());
            }
        });
        // Siempre devolvemos el LiveData de Room.
        return inventoryDao.getAllInventories();
    }

    public void createInventario(String description, OnOperationCompleteListener listener) {
        InventarioRequest request = new InventarioRequest(description);
        inventoryApiService.createInventario(request).enqueue(new Callback<InventarioResponse>() {
            @Override
            public void onResponse(Call<InventarioResponse> call, Response<InventarioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Inventario newInventario = new Inventario(
                            response.body().getIdInventario(),
                            response.body().getDescripcionInventario(),
                            response.body().getElementosInventario(),
                            response.body().getEstado(),
                            response.body().getRangoColaborador()
                    );
                    IO_EXECUTOR.execute(() -> inventoryDao.insert(newInventario));
                    if (listener != null) listener.onSuccess();
                } else {
                    System.err.println("API Error (create): " + response.code() + " - " + response.message());
                    if (listener != null) listener.onFailure("Error al crear inventario: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<InventarioResponse> call, Throwable t) {
                System.err.println("Network Error (create): " + t.getMessage());
                if (listener != null) listener.onFailure("Error de red al crear inventario: " + t.getMessage());
            }
        });
    }

    public void updateInventario(int id, String description, OnOperationCompleteListener listener) {
        InventarioRequest request = new InventarioRequest(description);
        inventoryApiService.updateInventario(id, request).enqueue(new Callback<InventarioResponse>() {
            @Override
            public void onResponse(Call<InventarioResponse> call, Response<InventarioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Inventario updatedInventario = new Inventario(
                            response.body().getIdInventario(),
                            response.body().getDescripcionInventario(),
                            response.body().getElementosInventario(),
                            response.body().getEstado(),
                            response.body().getRangoColaborador()
                    );
                    IO_EXECUTOR.execute(() -> inventoryDao.update(updatedInventario));
                    if (listener != null) listener.onSuccess();
                } else {
                    System.err.println("API Error (update): " + response.code() + " - " + response.message());
                    if (listener != null) listener.onFailure("Error al actualizar inventario: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<InventarioResponse> call, Throwable t) {
                System.err.println("Network Error (update): " + t.getMessage());
                if (listener != null) listener.onFailure("Error de red al actualizar inventario: " + t.getMessage());
            }
        });
    }

    public void deleteInventario(int id, OnOperationCompleteListener listener) {
        inventoryApiService.deleteInventario(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    IO_EXECUTOR.execute(() -> {
                        inventoryDao.deleteById(id);
                    });
                    if (listener != null) listener.onSuccess();
                } else {
                    System.err.println("API Error (delete): " + response.code() + " - " + response.message());
                    if (listener != null) listener.onFailure("Error al eliminar inventario: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.err.println("Network Error (delete): " + t.getMessage());
                if (listener != null) listener.onFailure("Error de red al eliminar inventario: " + t.getMessage());
            }
        });
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onFailure(String message);
    }
}