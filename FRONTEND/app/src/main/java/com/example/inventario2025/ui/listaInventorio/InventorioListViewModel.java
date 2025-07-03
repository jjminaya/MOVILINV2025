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
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>(""); // LiveData para la cadena de búsqueda
    private final MediatorLiveData<List<Inventario>> filteredInventories = new MediatorLiveData<>();

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

        loadInventoriesForCurrentUser(1);

        allCachedInventories = inventoryRepository.getInventoriesByUserId(1);

        filteredInventories.addSource(allCachedInventories, inventories -> {
            Log.d(TAG, "MediatorLiveData - Source allCachedInventories changed.");
            filterInventories();
        });
        filteredInventories.addSource(currentFilterType, filterType -> {
            Log.d(TAG, "MediatorLiveData - Source currentFilterType changed to: " + filterType);
            filterInventories();
        });
        filteredInventories.addSource(searchQuery, query -> {
            Log.d(TAG, "MediatorLiveData - Source searchQuery changed to: '" + query + "'");
            filterInventories();
        });
    }

    private void filterInventories() {
        Log.d(TAG, "filterInventories - Recibida lista de inventarios (todos). Cantidad: " + (allCachedInventories.getValue() != null ? allCachedInventories.getValue().size() : 0) + ", Tipo de filtro actual: " + currentFilterType.getValue() + ", Query de búsqueda: '" + searchQuery.getValue() + "'");

        List<Inventario> allInventories = allCachedInventories.getValue();
        FilterType currentType = currentFilterType.getValue();
        String currentSearchQuery = searchQuery.getValue();

        if (allInventories == null) {
            allInventories = new ArrayList<>();
            Log.d(TAG, "filterInventories - allInventories was null, initialized as empty list.");
        }

        List<Inventario> tempFilteredList = new ArrayList<>();

        // Paso 1: Filtrar por tipo (OWNED o SHARED)
        if (currentType == FilterType.OWNED) {
            tempFilteredList = allInventories.stream()
                    .filter(inventario -> "OWNR".equalsIgnoreCase(inventario.getRangoColaborador()))
                    .collect(Collectors.toList());
        } else if (currentType == FilterType.SHARED) {
            tempFilteredList = allInventories.stream()
                    .filter(inventario -> "COLAB".equalsIgnoreCase(inventario.getRangoColaborador()))
                    .collect(Collectors.toList());
        } else {
            tempFilteredList.addAll(allInventories);
        }

        // Paso 2: Aplicar filtro de búsqueda a la lista ya filtrada por tipo
        if (currentSearchQuery != null && !currentSearchQuery.trim().isEmpty()) {
            String lowerCaseQuery = currentSearchQuery.trim().toLowerCase();
            tempFilteredList = tempFilteredList.stream()
                    .filter(inventario -> inventario.getDescripcionInventario().toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList());
        }

        _isLoading.postValue(false);

        if (tempFilteredList.isEmpty()) {
            if (currentSearchQuery != null && !currentSearchQuery.trim().isEmpty()) {
                _errorMessage.postValue("Oops... No se encontró inventario con este nombre en la lista '" + currentType.name() + "'.");
                Log.d(TAG, "filterInventories - Error message: No search results.");
            } else {
                _errorMessage.postValue("No se encontraron inventarios para este tipo de filtro.");
                Log.d(TAG, "filterInventories - Error message: No inventories for filter type.");
            }
        } else {
            _errorMessage.postValue(null);
            Log.d(TAG, "filterInventories - Error message cleared, results found.");
        }

        // Actualizar el LiveData observable por la UI
        filteredInventories.setValue(tempFilteredList);
        Log.d(TAG, "filteredInventories LiveData updated. Final Count: " + tempFilteredList.size());
    }

    public LiveData<List<Inventario>> getInventariosDisplay() {
        return filteredInventories;
    }

    public void setFilterType(FilterType type) {
        Log.d(TAG, "setFilterType llamado con: " + type);
        currentFilterType.setValue(type);
    }

    public void setSearchQuery(String query) {
        if (!query.equals(searchQuery.getValue())) {
            searchQuery.setValue(query);
            Log.d(TAG, "Search query cambiado a: '" + query + "'");
        }
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