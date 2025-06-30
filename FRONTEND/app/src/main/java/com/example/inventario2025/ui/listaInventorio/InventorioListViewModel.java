package com.example.inventario2025.ui.listaInventorio;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.data.repository.InventarioRepository;
import com.example.inventario2025.data.local.InventarioBaseDatos;
import com.example.inventario2025.data.local.dao.InventarioDao;
import com.example.inventario2025.data.remote.RetrofitClient;
import com.example.inventario2025.data.remote.api.InventorioApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import android.util.Log;

public class InventorioListViewModel extends AndroidViewModel {

    private static final String TAG = "InventarioViewModel"; // Tag para logs
    private final InventarioRepository inventoryRepository;

    private final LiveData<List<Inventario>> allCachedInventories;
    private final MutableLiveData<FilterType> currentFilterType = new MutableLiveData<>(FilterType.OWNED); // Inicia con "Creados por mí"

    private final LiveData<List<Inventario>> filteredInventories;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    public enum FilterType { //
        OWNED,    // Creados por mí ("OWNR")
        SHARED,   // Compartidos (no "OWNR")
        ALL       // Todos
    }

    public InventorioListViewModel(@NonNull Application application) {
        super(application);

        InventarioBaseDatos db = InventarioBaseDatos.getDatabase(application);
        InventarioDao inventoryDao = db.inventoryDao();
        InventorioApiService apiService = RetrofitClient.getInventoryApiService();

        inventoryRepository = new InventarioRepository(inventoryDao, apiService);

        // Dispara la carga de inventarios del usuario 1 desde la API (esto pq no hay mas usuarios).
        // Esto insertará/actualizará los inventarios en la base de datos local.
        _isLoading.setValue(true);
        inventoryRepository.getInventoriesByUserId(1);
        Log.d(TAG, "Solicitando inventarios para el usuario ID: 1");

        // allCachedInventories observa la base de datos local.
        // Recibe actualizaciones cada vez que Room se actualiza (por getInventories() o getInventoriesByUserId()).
        allCachedInventories = inventoryRepository.getInventories();

        // filteredInventories se actualiza cuando cambia allCachedInventories o currentFilterType
        filteredInventories = Transformations.switchMap(currentFilterType, filterType -> {
            Log.d(TAG, "Transformations.switchMap - FilterType cambiado a: " + filterType);
            MediatorLiveData<List<Inventario>> result = new MediatorLiveData<>();
            result.addSource(allCachedInventories, inventories -> {
                Log.d(TAG, "MediatorLiveData - Recibida lista de inventarios (todos). Cantidad: " + (inventories != null ? inventories.size() : "null") + ", Tipo de filtro actual: " + filterType);
                if (inventories == null) {
                    result.setValue(null);
                    return;
                }
                List<Inventario> filteredList = new ArrayList<>();
                switch (filterType) {
                    case OWNED:
                        filteredList = inventories.stream()
                                .filter(inv -> {
                                    boolean isOwner = "OWNR".equals(inv.getRangoColaborador());
                                    Log.d(TAG, "Filtro OWNED - ID: " + inv.getIdInventario() + ", Descripción: " + inv.getDescripcionInventario() + ", Rango: '" + inv.getRangoColaborador() + "', ¿Es OWNED?: " + isOwner);
                                    return isOwner;
                                })
                                .collect(Collectors.toList());
                        break;
                    case SHARED:
                        filteredList = inventories.stream()
                                .filter(inv -> {
                                    boolean isShared = !"OWNR".equals(inv.getRangoColaborador());
                                    Log.d(TAG, "Filtro SHARED - ID: " + inv.getIdInventario() + ", Descripción: " + inv.getDescripcionInventario() + ", Rango: '" + inv.getRangoColaborador() + "', ¿Es SHARED?: " + isShared);
                                    return isShared;
                                })
                                .collect(Collectors.toList());
                        break;
                    case ALL:
                    default:
                        filteredList = new ArrayList<>(inventories);
                        Log.d(TAG, "Filtro ALL aplicado.");
                        break;
                }
                Log.d(TAG, "Lista filtrada para " + filterType + ". Cantidad: " + filteredList.size());
                result.setValue(filteredList);
            });
            return result;
        });

        // Este observador es para manejar el mensaje de "No data" basado en la lista filtrada
        filteredInventories.observeForever(inventories -> {
            Log.d(TAG, "Observador de filteredInventories - Cantidad: " + (inventories != null ? inventories.size() : "null"));
            if (inventories == null || inventories.isEmpty()) {
                _errorMessage.postValue("No se encontraron inventarios para este tipo de filtro.");
                Log.d(TAG, "Mensaje de error: 'No se encontraron inventarios para este tipo de filtro.'");
            } else {
                _errorMessage.postValue(null);
                Log.d(TAG, "Mensaje de error limpiado.");
            }
            _isLoading.postValue(false);
            Log.d(TAG, "isLoading puesto a false.");
        });
    }

    public LiveData<List<Inventario>> getInventariosDisplay() {
        return filteredInventories;
    }

    public void setFilterType(FilterType type) {
        Log.d(TAG, "setFilterType llamado con: " + type);
        currentFilterType.setValue(type);
    }

    private void loadInventoriesForCurrentUser(int userId) {
        _isLoading.setValue(true);
        inventoryRepository.getInventoriesByUserId(userId, new InventarioRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                _isLoading.postValue(false);
                _errorMessage.postValue(null);
                Log.d(TAG, "Inventarios cargados exitosamente para el usuario " + userId);
            }

            @Override
            public void onFailure(String message) {
                _isLoading.postValue(false);
                _errorMessage.postValue(message);
                Log.e(TAG, "Error al cargar inventarios para el usuario " + userId + ": " + message);
            }
        });
        Log.d(TAG, "Solicitando inventarios para el usuario ID: " + userId);
    }

    public void createNewInventario(String description, int userId) {
        _isLoading.postValue(true);
        inventoryRepository.createInventarioOWNR(description, userId, new InventarioRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                _isLoading.postValue(false);
                _errorMessage.postValue(null);
                loadInventoriesForCurrentUser(userId);
                Log.d(TAG, "Inventario creado exitosamente. Refrescando lista.");
            }

            @Override
            public void onFailure(String message) {
                _isLoading.postValue(false);
                _errorMessage.postValue(message);
                Log.e(TAG, "Error al crear inventario: " + message);
            }
        });
    }
}