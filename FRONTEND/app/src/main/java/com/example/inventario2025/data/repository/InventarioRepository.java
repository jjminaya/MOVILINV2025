package com.example.inventario2025.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.inventario2025.data.local.dao.InventarioDao;
import com.example.inventario2025.data.local.entities.Colaborador;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.data.local.entities.Usuario;
import com.example.inventario2025.data.remote.api.InventorioApiService;
import com.example.inventario2025.data.remote.models.InventarioResponse;
import com.example.inventario2025.data.remote.models.InventarioRequest;
import com.example.inventario2025.data.remote.models.InventarioCreateRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        refreshInventoriesFromServer();
        return inventoryDao.getAllInventories();
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
                            Log.d(TAG, "Listener_Method - API_Inv ID: " + apiInv.getIdInventario() + ", Rango_API: '" + apiInv.getRangoColaborador() + "'");

                            Inventario inventario = new Inventario(
                                    apiInv.getIdInventario(),
                                    apiInv.getDescripcionInventario(),
                                    apiInv.getElementosInventario(),
                                    apiInv.getEstado(),
                                    apiInv.getRangoColaborador(),
                                    userId
                            );
                            inventoryDao.insert(inventario);
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

    public LiveData<List<Inventario>> getInventoriesByUserId(int userId) {
        inventoryApiService.getInventariosByUserId(userId).enqueue(new Callback<List<InventarioResponse>>() {
            @Override
            public void onResponse(Call<List<InventarioResponse>> call, Response<List<InventarioResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Inventario> inventoriesToInsert = new ArrayList<>();
                    Log.d(TAG, "API Response (LiveData method) RECEIVED for user " + userId + ". Count: " + response.body().size()); // NUEVO LOG 4

                    for (InventarioResponse apiResponse : response.body()) {
                        Log.d(TAG, "LiveData_Method - API_Inv ID: " + apiResponse.getIdInventario() + ", Rango_API: '" + apiResponse.getRangoColaborador() + "'");

                        inventoriesToInsert.add(new Inventario(
                                apiResponse.getIdInventario(),
                                apiResponse.getDescripcionInventario(),
                                apiResponse.getElementosInventario(),
                                apiResponse.getEstado(),
                                apiResponse.getRangoColaborador(),
                                userId
                        ));
                        Inventario tempInventario = inventoriesToInsert.get(inventoriesToInsert.size() - 1);
                        Log.d(TAG, "LiveData_Method - Room_Entity ID: " + tempInventario.getIdInventario() + ", Rango_Room_After_Conversion: '" + tempInventario.getRangoColaborador() + "'");
                    }
                    IO_EXECUTOR.execute(() -> {
                        inventoryDao.deleteAll();
                        inventoryDao.insertAll(inventoriesToInsert);
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

    public void updateInventario(int inventarioId, String newDescription, OnOperationCompleteListener listener) {
        Map<String, String> body = new HashMap<>();
        body.put("descripcionInventario", newDescription);

        inventoryApiService.updateInventario(inventarioId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess();
                    IO_EXECUTOR.execute(() -> {
                        Inventario existing = inventoryDao.getInventarioByIdSync(inventarioId);
                        if (existing != null) {
                            existing.setDescripcionInventario(newDescription);
                            inventoryDao.insert(existing);
                        }
                    });
                } else {
                    Log.e("InventarioRepository", "Error al actualizar inventario. Código: " + response.code() + ", Mensaje: " + response.message() + ", Body de error: " + (response.errorBody() != null ? response.errorBody().toString() : "N/A"));
                    listener.onFailure("Error al actualizar inventario: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("InventarioRepository", "Fallo de red al actualizar inventario: " + t.getMessage(), t);
                listener.onFailure("Fallo de red al actualizar inventario: " + t.getMessage());
            }
        });
    }

    public void deleteInventario(int idInventario, final OnOperationCompleteListener listener) {
        inventoryApiService.deleteInventario(idInventario).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    IO_EXECUTOR.execute(() -> {
                        inventoryDao.deleteById(idInventario);
                        if (listener != null) {
                            listener.onSuccess();
                        }
                        Log.d(TAG, "Inventario " + idInventario + " eliminado exitosamente de la API y DB local.");
                    });
                } else {
                    String errorMessage = "Error al eliminar inventario: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    if (listener != null) {
                        listener.onFailure(errorMessage);
                    }
                    Log.e(TAG, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure("Fallo de red al eliminar inventario: " + t.getMessage());
                }
                Log.e(TAG, "Fallo de red al eliminar inventario", t);
            }
        });
    }

    // obtener colaboradores por ID de inventario
    public void getColaboradoresByInventarioId(int inventarioId, OnColaboradoresLoadedListener listener) {
        inventoryApiService.getColaboradoresByInventarioId(inventarioId).enqueue(new Callback<List<Colaborador>>() {
            @Override
            public void onResponse(Call<List<Colaborador>> call, Response<List<Colaborador>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onColaboradoresLoaded(response.body());
                    Log.d(TAG, "Colaboradores cargados exitosamente para inventario " + inventarioId + ": " + response.body().size());
                } else {
                    String errorMessage = "Error al cargar colaboradores: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    listener.onColaboradoresLoadFailed(errorMessage);
                    Log.e(TAG, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Colaborador>> call, Throwable t) {
                listener.onColaboradoresLoadFailed("Fallo de red al cargar colaboradores: " + t.getMessage());
                Log.e(TAG, "Fallo de red al cargar colaboradores", t);
            }
        });
    }

    // Metodo para verificar si un usuario existe
    public void checkUserExists(String username, OnUserCheckListener listener) {
        inventoryApiService.checkUserExists(username).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onUserChecked(response.body());
                    Log.d(TAG, "Verificación de usuario '" + username + "': Valido=" + response.body().isValid() + ", ID=" + response.body().getIdUsuario());
                } else {
                    String errorMessage = "Error al verificar usuario: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    listener.onUserCheckFailed(errorMessage);
                    Log.e(TAG, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                listener.onUserCheckFailed("Fallo de red al verificar usuario: " + t.getMessage());
                Log.e(TAG, "Fallo de red al verificar usuario", t);
            }
        });
    }

    // Metodo para agregar un colaborador
    public void addColaborador(int inventarioId, int idUsuario, String rangoColaborador, OnOperationCompleteListener listener) {
        Map<String, Object> body = new HashMap<>();
        body.put("idInventario", inventarioId);
        body.put("idUsuario", idUsuario);
        body.put("rangoColaborador", rangoColaborador); // "COLAB"

        inventoryApiService.addColaborador(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess();
                    Log.d(TAG, "Colaborador agregado exitosamente a inventario " + inventarioId);
                } else {
                    String errorMessage = "Error al agregar colaborador: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    listener.onFailure(errorMessage);
                    Log.e(TAG, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onFailure("Fallo de red al agregar colaborador: " + t.getMessage());
                Log.e(TAG, "Fallo de red al agregar colaborador", t);
            }
        });
    }

    // Metodo para eliminar un colaborador
    public void deleteColaborador(int idColaborador, OnOperationCompleteListener listener) {
        inventoryApiService.deleteColaborador(idColaborador).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess();
                    Log.d(TAG, "Colaborador " + idColaborador + " eliminado exitosamente.");
                } else {
                    String errorMessage = "Error al eliminar colaborador: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    listener.onFailure(errorMessage);
                    Log.e(TAG, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onFailure("Fallo de red al eliminar colaborador: " + t.getMessage());
                Log.e(TAG, "Fallo de red al eliminar colaborador", t);
            }
        });
    }

    public void getAllActiveUsers(OnAllUsersLoadedListener listener) {
        inventoryApiService.getAllActiveUsers().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onAllUsersLoaded(response.body());
                } else {
                    listener.onAllUsersLoadFailed("Error al cargar usuarios: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                listener.onAllUsersLoadFailed("Fallo de red al cargar usuarios: " + t.getMessage());
            }
        });
    }

    public void updateInventarioElementoCount(int inventarioId, int nuevoConteo, OnOperationCompleteListener listener) {
        Map<String, String> body = new HashMap<>();
        body.put("elementosInventario", String.valueOf(nuevoConteo));

        inventoryApiService.updateInventario(inventarioId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    IO_EXECUTOR.execute(() -> {
                        Inventario inventario = inventoryDao.getInventarioByIdSync(inventarioId);
                        if (inventario != null) {
                            inventario.setElementosInventario(nuevoConteo);
                            inventoryDao.update(inventario);
                        }
                    });
                    if (listener != null) listener.onSuccess();
                    Log.d(TAG, "Conteo de elementos actualizado en API para inventario " + inventarioId);
                } else {
                    String errorMsg = "Error al actualizar conteo: " + response.code();
                    if (listener != null) listener.onFailure(errorMsg);
                    Log.e(TAG, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (listener != null) listener.onFailure("Fallo de red al actualizar conteo: " + t.getMessage());
                Log.e(TAG, "Fallo de red al actualizar conteo", t);
            }
        });
    }

    public interface OnColaboradoresLoadedListener {
        void onColaboradoresLoaded(List<Colaborador> colaboradores);
        void onColaboradoresLoadFailed(String message);
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onFailure(String message);
    }

    public interface OnUserCheckListener {
        void onUserChecked(Usuario usuario);
        void onUserCheckFailed(String message);
    }

    public interface OnAllUsersLoadedListener {
        void onAllUsersLoaded(List<Usuario> usuarios);
        void onAllUsersLoadFailed(String message);
    }
}