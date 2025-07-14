package com.example.inventario2025.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.inventario2025.data.local.dao.ElementoDao;
import com.example.inventario2025.data.local.entities.Elemento;
import com.example.inventario2025.data.remote.api.InventorioApiService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ElementoRepository {

    private final ElementoDao elementoDao;
    private final InventorioApiService inventorioApiService;
    private static final String TAG = "ElementoRepository";

    private final ExecutorService executorService;

    public ElementoRepository(ElementoDao elementoDao, InventorioApiService inventorioApiService) {
        this.elementoDao = elementoDao;
        this.inventorioApiService = inventorioApiService;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void getElementosByInventarioId(int inventarioId, OnElementosLoadedListener listener) {
        inventorioApiService.getElementosByInventarioId(inventarioId).enqueue(new Callback<List<Elemento>>() {
            @Override
            public void onResponse(Call<List<Elemento>> call, Response<List<Elemento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Elemento> elementos = response.body();
                    executorService.execute(() -> {
                        elementoDao.deleteElementsByInventarioId(inventarioId);
                        elementoDao.insertAll(elementos);
                    });
                    listener.onElementosLoaded(elementos);
                    Log.d(TAG, "Elementos cargados desde la API para inventario " + inventarioId + ": " + elementos.size());
                } else {
                    String errorMessage = "Error al cargar elementos: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    listener.onElementosLoadFailed(errorMessage);
                    Log.e(TAG, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Elemento>> call, Throwable t) {
                listener.onElementosLoadFailed("Fallo de red al cargar elementos: " + t.getMessage());
                Log.e(TAG, "Fallo de red al cargar elementos", t);
            }
        });
    }

    public void createElemento(Elemento elemento, OnOperationCompleteListener listener) {
        inventorioApiService.createElemento(elemento).enqueue(new Callback<Elemento>() {
            @Override
            public void onResponse(Call<Elemento> call, Response<Elemento> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executorService.execute(() -> elementoDao.insert(response.body()));
                    listener.onSuccess();
                    Log.d(TAG, "Elemento creado exitosamente: " + response.body().getDescripcionElemento());
                } else {
                    String errorMessage = "Error al crear elemento: " + response.code();
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
            public void onFailure(Call<Elemento> call, Throwable t) {
                listener.onFailure("Fallo de red al crear elemento: " + t.getMessage());
                Log.e(TAG, "Fallo de red al crear elemento", t);
            }
        });
    }

    public void updateElemento(int idElemento, Elemento elemento, OnOperationCompleteListener listener) {
        inventorioApiService.updateElemento(idElemento, elemento).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    executorService.execute(() -> elementoDao.update(elemento));
                    listener.onSuccess();
                    Log.d(TAG, "Elemento " + idElemento + " actualizado exitosamente.");
                } else {
                    String errorMessage = "Error al actualizar elemento: " + response.code();
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
                listener.onFailure("Fallo de red al actualizar elemento: " + t.getMessage());
                Log.e(TAG, "Fallo de red al actualizar elemento", t);
            }
        });
    }

    public void deleteElemento(int idElemento, OnOperationCompleteListener listener) {
        inventorioApiService.deleteElemento(idElemento).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    executorService.execute(() -> elementoDao.deleteById(idElemento));
                    listener.onSuccess();
                    Log.d(TAG, "Elemento " + idElemento + " eliminado exitosamente.");
                } else {
                    String errorMessage = "Error al eliminar elemento: " + response.code();
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
                listener.onFailure("Fallo de red al eliminar elemento: " + t.getMessage());
                Log.e(TAG, "Fallo de red al eliminar elemento", t);
            }
        });
    }

    public LiveData<List<Elemento>> getElementosLocalByInventarioId(int inventarioId) {
        return elementoDao.getElementosByInventarioId(inventarioId);
    }

    public LiveData<Elemento> getElementoLocalById(int elementoId) {
        return elementoDao.getElementoById(elementoId);
    }

    public void insertElementoLocal(Elemento elemento) {
        executorService.execute(() -> elementoDao.insert(elemento));
    }

    public void updateElementoLocal(Elemento elemento) {
        executorService.execute(() -> elementoDao.update(elemento));
    }

    public void deleteElementoLocal(Elemento elemento) {
        executorService.execute(() -> elementoDao.delete(elemento));
    }

    public interface OnElementosLoadedListener {
        void onElementosLoaded(List<Elemento> elementos);
        void onElementosLoadFailed(String message);
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onFailure(String message);
    }
}