package com.example.inventario2025.ui.listaElementos;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.MediatorLiveData;
import android.util.Log;

import com.example.inventario2025.data.local.InventarioBaseDatos;
import com.example.inventario2025.data.local.dao.ElementoDao;
import com.example.inventario2025.data.local.dao.InventarioDao;
import com.example.inventario2025.data.local.entities.Elemento;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.data.repository.ElementoRepository;
import com.example.inventario2025.data.remote.RetrofitClient;
import com.example.inventario2025.data.remote.api.InventorioApiService;
import com.example.inventario2025.data.repository.InventarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ElementosListViewModel extends AndroidViewModel {

    private static final String TAG = "ElementosListViewModel";
    private final ElementoRepository elementoRepository;
    private final InventarioRepository inventarioRepository;
    private Inventario currentInventario;

    private final MutableLiveData<List<Elemento>> _elementos = new MutableLiveData<>();
    public LiveData<List<Elemento>> elementos = _elementos;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<String> _successMessage = new MutableLiveData<>();
    public LiveData<String> successMessage = _successMessage;

    private final MutableLiveData<String> _infoMessage = new MutableLiveData<>();
    public LiveData<String> infoMessage = _infoMessage;

    private final MutableLiveData<Boolean> _deleteElementoSuccess = new MutableLiveData<>();
    public LiveData<Boolean> deleteElementoSuccess = _deleteElementoSuccess;

    private final MutableLiveData<Boolean> _createElementoSuccess = new MutableLiveData<>();
    public LiveData<Boolean> createElementoSuccess = _createElementoSuccess;

    private final MutableLiveData<Boolean> _updateElementoSuccess = new MutableLiveData<>();
    public LiveData<Boolean> updateElementoSuccess = _updateElementoSuccess;

    private int currentInventarioId;

    // LiveData para la lista filtrada/buscada
    private final MediatorLiveData<List<Elemento>> _filteredElements = new MediatorLiveData<>();
    public LiveData<List<Elemento>> filteredElements = _filteredElements;
    private final MutableLiveData<String> _searchTerm = new MutableLiveData<>();
    public LiveData<String> searchTerm = _searchTerm;

    public ElementosListViewModel(@NonNull Application application) {
        super(application);
        InventarioBaseDatos database = InventarioBaseDatos.getDatabase(application);
        ElementoDao elementoDao = database.elementoDao();
        InventarioDao inventarioDao = database.inventoryDao();
        InventorioApiService inventorioApiService = RetrofitClient.getInventoryApiService();

        elementoRepository = new ElementoRepository(elementoDao, inventorioApiService);
        inventarioRepository = new InventarioRepository(inventarioDao, inventorioApiService);

        _filteredElements.addSource(_elementos, elements ->
                applyFilter(_elementos.getValue(), _searchTerm.getValue()));
        _filteredElements.addSource(_searchTerm, searchTerm ->
                applyFilter(_elementos.getValue(), _searchTerm.getValue()));
    }

    public void setCurrentInventario(Inventario inventario) {
        this.currentInventario = inventario;
        if (inventario != null) {
            loadElementsForInventario(inventario.getIdInventario());
        }
    }

    public void loadElementsForInventario(int inventarioId) {
        _isLoading.postValue(true);
        _errorMessage.postValue(null);
        _infoMessage.postValue("Cargando elementos...");

        elementoRepository.getElementosByInventarioId(inventarioId, new ElementoRepository.OnElementosLoadedListener() {
            @Override
            public void onElementosLoaded(List<Elemento> elementos) {
                _isLoading.postValue(false);
                _elementos.postValue(elementos);
                _infoMessage.postValue(null);
                Log.d(TAG, "Elementos cargados exitosamente.");
            }

            @Override
            public void onElementosLoadFailed(String message) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error al cargar elementos: " + message);
                _infoMessage.postValue(null);
                Log.e(TAG, "Fallo al cargar elementos: " + message);
            }
        });
    }

    public void addElemento(Elemento elemento) {
        _isLoading.postValue(true);
        _errorMessage.postValue(null);
        _successMessage.postValue(null);
        _infoMessage.postValue("Agregando elemento...");

        elementoRepository.createElemento(elemento, new ElementoRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                _isLoading.postValue(false);
                _createElementoSuccess.postValue(true);
                updateInventarioCount(1);
                loadElementsForInventario(currentInventario.getIdInventario());
                _infoMessage.postValue(null);
                _successMessage.postValue("Elemento agregado correctamente.");
            }

            @Override
            public void onFailure(String message) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error al agregar elemento: " + message);
                _createElementoSuccess.postValue(false);
                _infoMessage.postValue(null);
                Log.e(TAG, "Fallo al agregar elemento: " + message);
            }
        });
    }

    public void updateElemento(Elemento elemento) {
        _isLoading.postValue(true);
        _errorMessage.postValue(null);
        _successMessage.postValue(null);
        _infoMessage.postValue("Actualizando elemento...");

        elementoRepository.updateElemento(elemento.getIdElemento(), elemento, new ElementoRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                _isLoading.postValue(false);
                _updateElementoSuccess.postValue(true);
                loadElementsForInventario(currentInventarioId);
                _infoMessage.postValue(null);
                _successMessage.postValue("Elemento actualizado correctamente.");
                Log.d(TAG, "Elemento actualizado exitosamente.");
            }

            @Override
            public void onFailure(String message) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error al actualizar elemento: " + message);
                _updateElementoSuccess.postValue(false);
                _infoMessage.postValue(null);
                Log.e(TAG, "Fallo al actualizar elemento: " + message);
            }
        });
    }

    public void deleteElemento(int idElemento) {
        _isLoading.postValue(true);
        _errorMessage.postValue(null);
        _successMessage.postValue(null);
        _infoMessage.postValue("Eliminando elemento...");

        elementoRepository.deleteElemento(idElemento, new ElementoRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                _isLoading.postValue(false);
                _deleteElementoSuccess.postValue(true);
                updateInventarioCount(-1);
                loadElementsForInventario(currentInventario.getIdInventario());
                _infoMessage.postValue(null);
                _successMessage.postValue("Elemento eliminado correctamente.");
            }

            @Override
            public void onFailure(String message) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error al eliminar elemento: " + message);
                _deleteElementoSuccess.postValue(false);
                _infoMessage.postValue(null);
                Log.e(TAG, "Fallo al eliminar elemento: " + message);
            }
        });
    }

    private void updateInventarioCount(int change) {
        if (currentInventario == null) return;

        int nuevoConteo = currentInventario.getElementosInventario() + change;
        currentInventario.setElementosInventario(nuevoConteo); // Actualizamos el objeto localmente

        inventarioRepository.updateInventarioElementoCount(currentInventario.getIdInventario(), nuevoConteo, new InventarioRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Conteo de inventario actualizado exitosamente a " + nuevoConteo);
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "Fallo al actualizar conteo de inventario: " + message);
            }
        });
    }

    public void clearErrorMessage() {
        _errorMessage.postValue(null);
    }

    public void clearSuccessMessage() {
        _successMessage.postValue(null);
    }

    public void clearInfoMessage() {
        _infoMessage.postValue(null);
    }

    public void clearDeleteElementoSuccess() {
        _deleteElementoSuccess.postValue(null);
    }

    public void clearCreateElementoSuccess() {
        _createElementoSuccess.postValue(null);
    }

    public void clearUpdateElementoSuccess() {
        _updateElementoSuccess.postValue(null);
    }

    public LiveData<List<Elemento>> getElements() {
        return _elementos;
    }

    public LiveData<String> getErrorMessage() {
        return _errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return _successMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }

    public void searchElements(String query) {
        _searchTerm.postValue(query);
    }

    private void applyFilter(List<Elemento> originalList, String query) {
        if (originalList == null || originalList.isEmpty()) {
            _filteredElements.postValue(new ArrayList<>());
            return;
        }

        if (query == null || query.isEmpty()) {
            _filteredElements.postValue(originalList);
            return;
        }

        String lowerCaseQuery = query.toLowerCase().trim();
        List<Elemento> filteredList = originalList.stream()
                .filter(elemento ->
                        // Busca en la descripción
                        (elemento.getDescripcionElemento() != null && elemento.getDescripcionElemento().toLowerCase().contains(lowerCaseQuery)) ||
                                // Busca en el uniCode
                                (elemento.getUniCodeElemento() != null && elemento.getUniCodeElemento().toLowerCase().contains(lowerCaseQuery))
                )
                .collect(Collectors.toList());
        _filteredElements.postValue(filteredList);
    }
}
