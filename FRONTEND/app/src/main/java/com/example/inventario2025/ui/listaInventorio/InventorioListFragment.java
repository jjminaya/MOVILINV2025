package com.example.inventario2025.ui.listaInventorio;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.example.inventario2025.ui.dialogos.ColaboradoresDialogFragment;
import com.example.inventario2025.ui.dialogos.EditarInventarioDialogFragment;
import com.google.android.material.card.MaterialCardView;
import androidx.appcompat.widget.PopupMenu;
import android.view.MenuItem;
import android.util.Log;

import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.databinding.InventorioListBinding;
import com.example.inventario2025.ui.adapters.InventarioAdapter;
import com.example.inventario2025.ui.dialogos.CrearInventarioDialogFragment;
import com.example.inventario2025.R;

import java.util.List;

public class InventorioListFragment extends Fragment {

    private static final String TAG = "InventarioListFragment";
    private InventorioListBinding binding;
    private InventorioListViewModel inventarioListViewModel;
    private InventarioAdapter inventarioAdapter;
    private MaterialCardView errorCardView;
    private TextView errorMessageTextView;
    private InventorioListViewModel.SortCriteria currentSelectedSort = InventorioListViewModel.SortCriteria.NONE;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = InventorioListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inventarioListViewModel = new ViewModelProvider(this).get(InventorioListViewModel.class);

        errorCardView = binding.errorCardView;
        errorMessageTextView = binding.errorMessageTextView;

        setupRecyclerView();
        setupObservers();
        setupClickListeners();
        setupSearchListener();

        inventarioListViewModel.setFilterType(InventorioListViewModel.FilterType.OWNED);
    }
    private void setupRecyclerView() {
        inventarioAdapter = new InventarioAdapter();
        binding.recyclerViewInventories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewInventories.setAdapter(inventarioAdapter);

        inventarioAdapter.setOnItemActionListener(new InventarioAdapter.OnItemActionListener() {
            @Override
            public void onEditClick(Inventario inventario) {
                EditarInventarioDialogFragment dialog = EditarInventarioDialogFragment.newInstance(inventario);
                dialog.show(getChildFragmentManager(), "EditInventoryDialog");
            }

            @Override
            public void onAddCollaboratorClick(Inventario inventario) {
                ColaboradoresDialogFragment dialog = ColaboradoresDialogFragment.newInstance(inventario);
                dialog.show(getChildFragmentManager(), "ColaboradoresDialog");
            }

            @Override
            public void onDeleteClick(Inventario inventario) {
                Toast.makeText(getContext(), "Eliminar: " + inventario.getDescripcionInventario(), Toast.LENGTH_SHORT).show();
                // Aqui ira la logica para eliminar el inventario
            }
        });
    }

    private void setupObservers() {
        inventarioListViewModel.getInventariosDisplay().observe(getViewLifecycleOwner(), inventories -> {
            inventarioAdapter.setInventarioList(inventories);
            updateUiVisibility();
        });

        // Observar los mensajes de error del ViewModel
        inventarioListViewModel.errorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            updateUiVisibility();
        });

        // Observar si no hay datos encontrados específicamente (usado por el ViewModel)
        inventarioListViewModel.noDataFound.observe(getViewLifecycleOwner(), noData -> {
            updateUiVisibility();
        });

        // Observar el estado de carga del ViewModel
        inventarioListViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            updateUiVisibility();
        });

        inventarioListViewModel.getCurrentSortCriteria().observe(getViewLifecycleOwner(), criteria -> {
            currentSelectedSort = criteria;
            android.util.Log.d(TAG, "Criterio de ordenamiento actualizado en Fragment por ViewModel: " + criteria);
        });
    }

    private void setupClickListeners() {
        binding.btnCreateInventory.setOnClickListener(v -> {
            CrearInventarioDialogFragment dialog = CrearInventarioDialogFragment.newInstance();
            dialog.show(getChildFragmentManager(), "CreateInventoryDialog");
        });

        // Listener para los botones de filtro
        binding.toggleButtonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                binding.searchEditText.setText("");
                inventarioListViewModel.setSearchQuery("");

                inventarioListViewModel.setSortCriteria(InventorioListViewModel.SortCriteria.NONE);
                if (checkedId == R.id.btn_my_inventories) {
                    inventarioListViewModel.setFilterType(InventorioListViewModel.FilterType.OWNED);
                    Toast.makeText(getContext(), "Mostrando inventarios 'Creados por mí'", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.btn_shared_inventories) {
                    inventarioListViewModel.setFilterType(InventorioListViewModel.FilterType.SHARED);
                    Toast.makeText(getContext(), "Mostrando inventarios 'Compartidos'", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.sortButton.setOnClickListener(this::showSortPopupMenu);
    }

    private void setupSearchListener() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                inventarioListViewModel.setSearchQuery(s.toString());
            }
        });
    }

    private void updateUiVisibility() {
        boolean isLoading = inventarioListViewModel.isLoading.getValue() != null && inventarioListViewModel.isLoading.getValue();
        String currentErrorMessage = inventarioListViewModel.errorMessage.getValue();
        boolean noDataFound = inventarioListViewModel.noDataFound.getValue() != null && inventarioListViewModel.noDataFound.getValue();
        List<Inventario> currentInventories = inventarioListViewModel.getInventariosDisplay().getValue();
        boolean hasData = currentInventories != null && !currentInventories.isEmpty();

        binding.recyclerViewInventories.setVisibility(View.GONE);
        errorCardView.setVisibility(View.GONE);
        binding.textViewNoData.setVisibility(View.GONE);

        if (isLoading) {
        } else {
            if (currentErrorMessage != null && !currentErrorMessage.isEmpty()) {
                errorMessageTextView.setText(currentErrorMessage);
                errorCardView.setVisibility(View.VISIBLE);
            } else if (noDataFound) {
                binding.textViewNoData.setText("No hay inventarios disponibles.");
                binding.textViewNoData.setVisibility(View.VISIBLE);
            } else if (hasData) {
                binding.recyclerViewInventories.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showSortPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(requireContext(), view);
        popup.getMenuInflater().inflate(R.menu.filtro_menu, popup.getMenu());
        popup.getMenu().setGroupCheckable(R.id.sort_group, true, true);
        Log.d(TAG, "showSortPopupMenu: currentSelectedSort al abrir el menú: " + currentSelectedSort);

        for (int i = 0; i < popup.getMenu().size(); i++) {
            MenuItem item = popup.getMenu().getItem(i);
            item.setCheckable(true);
            InventorioListViewModel.SortCriteria itemCriteria = InventorioListViewModel.SortCriteria.NONE;

            // Determinar el criterio asociado a cada ID de menú
            if (item.getItemId() == R.id.action_sort_desc_az) {
                itemCriteria = InventorioListViewModel.SortCriteria.DESCRIPTION_ASC;
            } else if (item.getItemId() == R.id.action_sort_desc_za) {
                itemCriteria = InventorioListViewModel.SortCriteria.DESCRIPTION_DESC;
            } else if (item.getItemId() == R.id.action_sort_elem_1n) {
                itemCriteria = InventorioListViewModel.SortCriteria.ELEMENTS_ASC;
            } else if (item.getItemId() == R.id.action_sort_elem_n1) {
                itemCriteria = InventorioListViewModel.SortCriteria.ELEMENTS_DESC;
            }

            item.setChecked(false); // Desmarcar explícitamente todos los ítems

            if (currentSelectedSort == itemCriteria) {
                item.setChecked(true);
                Log.d(TAG, "showSortPopupMenu: Marcando ítem: " + item.getTitle() + " con criterio: " + itemCriteria);
            }
        }

        popup.setOnMenuItemClickListener(item -> {
            InventorioListViewModel.SortCriteria selectedCriteria = InventorioListViewModel.SortCriteria.NONE;

            // Determinar el criterio asociado al ítem clicado
            if (item.getItemId() == R.id.action_sort_desc_az) {
                selectedCriteria = InventorioListViewModel.SortCriteria.DESCRIPTION_ASC;
            } else if (item.getItemId() == R.id.action_sort_desc_za) {
                selectedCriteria = InventorioListViewModel.SortCriteria.DESCRIPTION_DESC;
            } else if (item.getItemId() == R.id.action_sort_elem_1n) {
                selectedCriteria = InventorioListViewModel.SortCriteria.ELEMENTS_ASC;
            } else if (item.getItemId() == R.id.action_sort_elem_n1) {
                selectedCriteria = InventorioListViewModel.SortCriteria.ELEMENTS_DESC;
            } else {
                return false;
            }

            Log.d(TAG, "onMenuItemClick: Criterio clicado: " + selectedCriteria + ", currentSelectedSort (antes de ViewModel): " + currentSelectedSort);

            if (currentSelectedSort == selectedCriteria) {
                // Caso 1: El usuario hizo clic en la opción que YA ESTABA ACTIVA.
                inventarioListViewModel.setSortCriteria(InventorioListViewModel.SortCriteria.NONE);
                item.setChecked(false);
                Toast.makeText(getContext(), "Ordenamiento " + item.getTitle() + " desactivado.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onMenuItemClick: Desactivando ordenamiento.");
            } else {
                // Caso 2: El usuario hizo clic en una opción DIFERENTE (o ninguna estaba activa).
                inventarioListViewModel.setSortCriteria(selectedCriteria);
                item.setChecked(true);
                Toast.makeText(getContext(), "Ordenado: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onMenuItemClick: Activando ordenamiento: " + selectedCriteria);
            }
            return true;
        });
        popup.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}