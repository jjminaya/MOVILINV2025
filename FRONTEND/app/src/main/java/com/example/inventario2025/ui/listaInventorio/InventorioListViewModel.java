package com.example.inventario2025.ui.listaInventorio;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.inventario2025.data.local.entities.Colaborador;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.data.local.entities.Usuario;
import com.example.inventario2025.data.repository.InventarioRepository;
import com.example.inventario2025.data.local.InventarioBaseDatos;
import com.example.inventario2025.data.local.dao.InventarioDao;
import com.example.inventario2025.data.remote.RetrofitClient;
import com.example.inventario2025.data.remote.api.InventorioApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import android.util.Log;

public class InventorioListViewModel extends AndroidViewModel {

    private static final String TAG = "InventarioViewModel"; // Tag para logs
    private final InventarioRepository inventoryRepository;

    private final LiveData<List<Inventario>> allCachedInventories;
    private final MutableLiveData<FilterType> currentFilterType = new MutableLiveData<>(FilterType.OWNED); // Inicia con "Creados por mí"
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>(""); // LiveData para la cadena de búsqueda
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(true);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;
    private final MutableLiveData<Boolean> _noDataFound = new MutableLiveData<>(false);
    public LiveData<Boolean> noDataFound = _noDataFound;
    private final MutableLiveData<SortCriteria> currentSortCriteria = new MutableLiveData<>(SortCriteria.NONE);
    private final MediatorLiveData<List<Inventario>> inventoriesDisplay = new MediatorLiveData<>();
    private final MutableLiveData<List<Colaborador>> _colaboradores = new MutableLiveData<>();
    public LiveData<List<Colaborador>> colaboradores = _colaboradores;
    private final MutableLiveData<Boolean> _isColaboradoresLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isColaboradoresLoading = _isColaboradoresLoading;
    private final MutableLiveData<String> _colaboradoresErrorMessage = new MutableLiveData<>();
    public LiveData<String> colaboradoresErrorMessage = _colaboradoresErrorMessage;

    private final MutableLiveData<String> _colaboradoresSuccessMessage = new MutableLiveData<>();
    public LiveData<String> colaboradoresSuccessMessage = _colaboradoresSuccessMessage;
    private final MutableLiveData<Boolean> _userVerificationSuccess = new MutableLiveData<>();
    public LiveData<Boolean> userVerificationSuccess = _userVerificationSuccess;
    private final MutableLiveData<String> _infoMessage = new MutableLiveData<>();
    public LiveData<String> infoMessage = _infoMessage;
    private final MutableLiveData<Integer> _currentUserId = new MutableLiveData<>();
    public LiveData<Integer> currentUserId = _currentUserId; //se usará para saber usuario actual
    private final MutableLiveData<Boolean> _deleteInventarioSuccess = new MutableLiveData<>();
    public LiveData<Boolean> getDeleteInventarioSuccess() { return _deleteInventarioSuccess; }

    public enum FilterType {
        OWNED,
        SHARED,
        ALL
    }

    public enum SortCriteria {
        NONE,
        DESCRIPTION_ASC,
        DESCRIPTION_DESC,
        ELEMENTS_ASC,
        ELEMENTS_DESC
    }

    public InventorioListViewModel(@NonNull Application application) {
        super(application);

        InventarioBaseDatos db = InventarioBaseDatos.getDatabase(application);
        InventarioDao inventoryDao = db.inventoryDao();
        InventorioApiService apiService = RetrofitClient.getInventoryApiService();
        inventoryRepository = new InventarioRepository(inventoryDao, apiService);

        allCachedInventories = inventoryRepository.getInventoriesByUserId(1);

        // Configuración de MediatorLiveData para aplicar filtros y ordenamiento
        inventoriesDisplay.addSource(allCachedInventories, inventories -> {
            Log.d(TAG, "MediatorLiveData - Fuente allCachedInventories cambiada.");
            applyFilterAndSort();
        });
        inventoriesDisplay.addSource(currentFilterType, filterType -> {
            Log.d(TAG, "MediatorLiveData - Fuente currentFilterType cambiada a: " + filterType);
            applyFilterAndSort();
        });
        inventoriesDisplay.addSource(searchQuery, query -> {
            Log.d(TAG, "MediatorLiveData - Fuente searchQuery cambiada a: '" + query + "'");
            applyFilterAndSort();
        });
        inventoriesDisplay.addSource(currentSortCriteria, sortCriteria -> {
            Log.d(TAG, "MediatorLiveData - Fuente currentSortCriteria cambiada a: " + sortCriteria);
            applyFilterAndSort();
        });

        loadInventoriesForCurrentUser(1);
    }

    public void setCurrentUserId(int userId) {
        _currentUserId.postValue(userId);
        inventoryRepository.getInventoriesByUserId(userId);
    }

    private void applyFilterAndSort() {
        Log.d(TAG, "applyFilterAndSort() llamado. Filtro actual: " + currentFilterType.getValue() +
                ", Búsqueda actual: '" + searchQuery.getValue() +
                "', Ordenamiento actual: " + currentSortCriteria.getValue());

        List<Inventario> allInventories = allCachedInventories.getValue();
        FilterType currentType = currentFilterType.getValue();
        String currentSearchQuery = searchQuery.getValue();
        SortCriteria currentSort = currentSortCriteria.getValue();

        if (allInventories == null) {
            allInventories = new ArrayList<>();
            Log.d(TAG, "applyFilterAndSort - allInventories era nulo, inicializado como lista vacía.");
        }

        List<Inventario> tempFilteredList;

        // Paso 1: Filtrar por tipo (OWNED o SHARED)
        if (currentType == FilterType.OWNED) {
            tempFilteredList = allInventories.stream()
                    .filter(inventario -> "OWNR".equalsIgnoreCase(inventario.getRangoColaborador()))
                    .collect(Collectors.toList());
            Log.d(TAG, "applyFilterAndSort - Filtrado por OWNED. Cantidad: " + tempFilteredList.size());
        } else if (currentType == FilterType.SHARED) {
            tempFilteredList = allInventories.stream()
                    .filter(inventario -> "COLAB".equalsIgnoreCase(inventario.getRangoColaborador()))
                    .collect(Collectors.toList());
            Log.d(TAG, "applyFilterAndSort - Filtrado por SHARED. Cantidad: " + tempFilteredList.size());
        } else {
            tempFilteredList = new ArrayList<>(allInventories); // Crear una copia mutable
            Log.d(TAG, "applyFilterAndSort - Sin tipo de filtro específico, mostrando todos. Cantidad: " + tempFilteredList.size());
        }

        // Paso 2: Aplicar filtro de búsqueda
        if (currentSearchQuery != null && !currentSearchQuery.trim().isEmpty()) {
            String lowerCaseQuery = currentSearchQuery.trim().toLowerCase();
            int initialCount = tempFilteredList.size();
            tempFilteredList = tempFilteredList.stream()
                    .filter(inventario -> inventario.getDescripcionInventario().toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList());
            Log.d(TAG, "applyFilterAndSort - Búsqueda aplicada '" + lowerCaseQuery + "'. Antes: " + initialCount + ", Después: " + tempFilteredList.size());
        }

        // Paso 3: Aplicar ordenamiento
        if (currentSort != null && currentSort != SortCriteria.NONE) {
            Log.d(TAG, "applyFilterAndSort - Aplicando ordenamiento: " + currentSort.name());
            switch (currentSort) {
                case DESCRIPTION_ASC:
                    Collections.sort(tempFilteredList, Comparator.comparing(Inventario::getDescripcionInventario, String.CASE_INSENSITIVE_ORDER)); // CAMBIO AQUI: Usar String.CASE_INSENSITIVE_ORDER para comparar sin importar mayúsculas/minúsculas
                    break;
                case DESCRIPTION_DESC:
                    Collections.sort(tempFilteredList, Comparator.comparing(Inventario::getDescripcionInventario, String.CASE_INSENSITIVE_ORDER).reversed()); // CAMBIO AQUI: Usar String.CASE_INSENSITIVE_ORDER
                    break;
                case ELEMENTS_ASC:
                    Collections.sort(tempFilteredList, Comparator.comparingInt(Inventario::getElementosInventario));
                    break;
                case ELEMENTS_DESC:
                    Collections.sort(tempFilteredList, Comparator.comparingInt(Inventario::getElementosInventario).reversed());
                    break;
            }
        } else {
            Log.d(TAG, "applyFilterAndSort - Sin ordenamiento aplicado.");
        }

        if (Boolean.FALSE.equals(_isLoading.getValue())) {
            if (tempFilteredList.isEmpty()) {
                if (currentSearchQuery != null && !currentSearchQuery.trim().isEmpty()) {
                    _errorMessage.postValue("Oops... No se encontró inventario con este nombre en la lista '" + currentType.name() + "'.");
                    _noDataFound.postValue(true);
                } else {
                    _errorMessage.postValue("No se encontraron inventarios para este tipo de filtro.");
                    _noDataFound.postValue(true);
                }
            } else {
                _errorMessage.postValue(null);
                _noDataFound.postValue(false);
            }
        } else {
            _errorMessage.postValue(null);
            _noDataFound.postValue(false);
        }

        // Actualizar el LiveData observable por la UI
        inventoriesDisplay.setValue(tempFilteredList);
        Log.d(TAG, "inventoriesDisplay LiveData actualizado. Cantidad final: " + tempFilteredList.size());
    }

    public LiveData<List<Inventario>> getInventariosDisplay() {
        return inventoriesDisplay;
    }

    public void setFilterType(FilterType type) {
        Log.d(TAG, "setFilterType llamado con: " + type);
        currentFilterType.setValue(type);
        setSortCriteria(SortCriteria.NONE);
    }

    public void setSearchQuery(String query) {
        String newQuery = query != null ? query : "";
        if (!newQuery.equals(searchQuery.getValue())) {
            searchQuery.setValue(newQuery);
            Log.d(TAG, "setSearchQuery - LiveData searchQuery actualizado a: '" + newQuery + "'");
        } else {
            Log.d(TAG, "setSearchQuery - La consulta es la misma, omitiendo actualización: '" + newQuery + "'");
        }
    }

    public void setSortCriteria(SortCriteria criteria) {
        // Lógica para alternar el ordenamiento (si se selecciona el mismo, se desactiva)
        if (currentSortCriteria.getValue() == criteria) {
            currentSortCriteria.setValue(SortCriteria.NONE);
            Log.d(TAG, "setSortCriteria - Mismo criterio seleccionado, desactivando ordenamiento.");
        } else {
            currentSortCriteria.setValue(criteria);
            Log.d(TAG, "setSortCriteria - Criterio de ordenamiento establecido a: " + criteria);
        }
    }

    public LiveData<SortCriteria> getCurrentSortCriteria() {
        return currentSortCriteria;
    }

    public LiveData<String> getErrorMessage() { // Asegúrate de que este método exista si lo usas en el fragmento
        return _errorMessage;
    }

    public LiveData<Boolean> getNoDataFound() { // Asegúrate de que este método exista
        return _noDataFound;
    }

    public void loadInventoriesForCurrentUser(int userId) {
        _isLoading.postValue(true);
        _errorMessage.postValue(null);
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
        _errorMessage.postValue(null);
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

    public void updateInventario(int inventarioId, String newDescription) {
        _isLoading.postValue(true);
        inventoryRepository.updateInventario(inventarioId, newDescription, new InventarioRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                loadInventoriesForCurrentUser(1);
                _isLoading.postValue(false);
                _errorMessage.postValue(null);
                Log.d(TAG, "Inventario " + inventarioId + " actualizado exitosamente. Refrescando lista.");
            }

            @Override
            public void onFailure(String message) {
                _isLoading.postValue(false);
                _errorMessage.postValue(message);
                Log.e(TAG, "Error al actualizar inventario " + inventarioId + ": " + message);
            }
        });
    }

    // Metodo para cargar colaboradores en el ViewModel
    public void loadColaboradores(int inventarioId) {
        _isColaboradoresLoading.postValue(true);
        _colaboradoresErrorMessage.postValue(null); // Limpiar errores previos

        inventoryRepository.getColaboradoresByInventarioId(inventarioId, new InventarioRepository.OnColaboradoresLoadedListener() {
            @Override
            public void onColaboradoresLoaded(List<Colaborador> colaboradoresList) {
                _isColaboradoresLoading.postValue(false);
                _colaboradores.postValue(colaboradoresList);
                Log.d(TAG, "Colaboradores cargados para inventario " + inventarioId + ". Cantidad: " + colaboradoresList.size());
            }

            @Override
            public void onColaboradoresLoadFailed(String message) {
                _isColaboradoresLoading.postValue(false);
                _colaboradoresErrorMessage.postValue(message);
                _colaboradores.postValue(new ArrayList<>()); // Limpiar lista en caso de error
                Log.e(TAG, "Fallo al cargar colaboradores para inventario " + inventarioId + ": " + message);
            }
        });
    }

    public void addColaborador(int inventarioId, String username) {
        _isColaboradoresLoading.postValue(true);
        _colaboradoresErrorMessage.postValue(null);
        _colaboradoresSuccessMessage.postValue(null);
        _userVerificationSuccess.postValue(null);
        _infoMessage.postValue("Intentando agregar el usuario: " + username + "...");
        inventoryRepository.checkUserExists(username, new InventarioRepository.OnUserCheckListener() {
            @Override
            public void onUserChecked(Usuario usuario) {
                if (usuario.isValid() && usuario.getIdUsuario() != null) {
                    Log.d(TAG, "Usuario '" + username + "' existe con ID: " + usuario.getIdUsuario());
                    _userVerificationSuccess.postValue(true);

                    inventoryRepository.addColaborador(inventarioId, usuario.getIdUsuario(), "COLAB", new InventarioRepository.OnOperationCompleteListener() {
                        @Override
                        public void onSuccess() {
                            _isColaboradoresLoading.postValue(false);
                            loadColaboradores(inventarioId);
                            _colaboradoresSuccessMessage.postValue("Usuario '" + username + "' agregado correctamente."); // CAMBIO AQUI: Mensaje de éxito
                            Log.d(TAG, "Colaborador agregado exitosamente.");
                            _infoMessage.postValue(null);
                        }

                        @Override
                        public void onFailure(String message) {
                            _isColaboradoresLoading.postValue(false);
                            _colaboradoresErrorMessage.postValue("Error al agregar colaborador: " + message);
                            Log.e(TAG, "Error al agregar colaborador: " + message);
                            _infoMessage.postValue(null);
                        }
                    });
                } else {
                    _isColaboradoresLoading.postValue(false);
                    _colaboradoresErrorMessage.postValue("El usuario '" + username + "' no existe.");
                    Log.d(TAG, "El usuario '" + username + "' no existe.");
                    _userVerificationSuccess.postValue(false);
                    _infoMessage.postValue(null);
                }
            }

            @Override
            public void onUserCheckFailed(String message) {
                _isColaboradoresLoading.postValue(false);
                _colaboradoresErrorMessage.postValue("Fallo al verificar usuario: " + message);
                Log.e(TAG, "Fallo al verificar usuario: " + message);
                _userVerificationSuccess.postValue(false);
                _infoMessage.postValue(null);
            }
        });
    }

    public void deleteColaborador(int idColaborador, int inventarioId) {
        _isColaboradoresLoading.postValue(true);
        _colaboradoresErrorMessage.postValue(null);
        _colaboradoresSuccessMessage.postValue(null);
        _infoMessage.postValue("Eliminando colaborador...");

        inventoryRepository.deleteColaborador(idColaborador, new InventarioRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                _isColaboradoresLoading.postValue(false);
                loadColaboradores(inventarioId);
                _colaboradoresSuccessMessage.postValue("Colaborador eliminado correctamente.");
                Log.d(TAG, "Colaborador eliminado exitosamente.");
                _infoMessage.postValue(null);
            }

            @Override
            public void onFailure(String message) {
                _isColaboradoresLoading.postValue(false);
                _colaboradoresErrorMessage.postValue("Error al eliminar colaborador: " + message);
                Log.e(TAG, "Error al eliminar colaborador: " + message);
                _infoMessage.postValue(null);
            }
        });
    }

    public void deleteInventario(int inventarioId, int userId) { // CAMBIO AQUI: Añadir userId para recargar
        _isLoading.postValue(true);
        _errorMessage.postValue(null);
        _deleteInventarioSuccess.postValue(null);
        _infoMessage.postValue("Eliminando inventario...");

        inventoryRepository.deleteInventario(inventarioId, new InventarioRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                _isLoading.postValue(false);
                _deleteInventarioSuccess.postValue(true);
                loadInventoriesForCurrentUser(userId);
                _infoMessage.postValue(null);
                Log.d(TAG, "Inventario " + inventarioId + " eliminado exitosamente.");
            }

            @Override
            public void onFailure(String message) {
                _isLoading.postValue(false);
                _errorMessage.postValue(message);
                _deleteInventarioSuccess.postValue(false);
                _infoMessage.postValue(null);
                Log.e(TAG, "Fallo al eliminar inventario " + inventarioId + ": " + message);
            }
        });
    }

    public void clearUserVerificationSuccess() {
        _userVerificationSuccess.postValue(null);
    }

    public void clearInfoMessage() {
        _infoMessage.postValue(null);
    }

    public void clearColaboradoresErrorMessage() {
        _colaboradoresErrorMessage.postValue(null);
    }

    public void clearColaboradoresSuccessMessage() {
        _colaboradoresSuccessMessage.postValue(null);
    }

    public void clearDeleteInventarioSuccess() {
        _deleteInventarioSuccess.postValue(null);
    }

    public void clearErrorMessage() {
        _errorMessage.postValue(null);
    }
}