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
import android.widget.ImageView;
import com.google.android.material.card.MaterialCardView;

import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.databinding.InventorioListBinding;
import com.example.inventario2025.ui.adapters.InventarioAdapter;
import com.example.inventario2025.ui.dialogos.CrearInventarioDialogFragment;

import java.util.List;

public class InventorioListFragment extends Fragment {

    private InventorioListBinding binding;
    private InventorioListViewModel inventarioListViewModel;
    private InventarioAdapter inventarioAdapter;
    private MaterialCardView errorCardView;
    private TextView errorMessageTextView;

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

        // Establecer el filtro inicial a "Creados por mí" (OWNED) al iniciar el fragmento
        inventarioListViewModel.setFilterType(InventorioListViewModel.FilterType.OWNED);
    }

    private void setupRecyclerView() {
        inventarioAdapter = new InventarioAdapter();
        binding.recyclerViewInventories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewInventories.setAdapter(inventarioAdapter);
    }

    private void setupObservers() {
        // Observar la lista de inventarios FILTRADA que viene del ViewModel
        inventarioListViewModel.getInventariosDisplay().observe(getViewLifecycleOwner(), inventories -> {
            inventarioAdapter.setInventarioList(inventories);
            updateUiVisibility();
        });

        // Observar el estado de carga
        inventarioListViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            updateUiVisibility();
        });

        // Observar mensajes de error
        inventarioListViewModel.errorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            updateUiVisibility();
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
                if (checkedId == binding.btnMyInventories.getId()) {
                    inventarioListViewModel.setFilterType(InventorioListViewModel.FilterType.OWNED);
                    Toast.makeText(getContext(), "Mostrando inventarios 'Creados por mí'", Toast.LENGTH_SHORT).show();
                } else if (checkedId == binding.btnSharedInventories.getId()) {
                    inventarioListViewModel.setFilterType(InventorioListViewModel.FilterType.SHARED);
                    Toast.makeText(getContext(), "Mostrando inventarios 'Compartidos'", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        List<Inventario> currentInventories = inventarioListViewModel.getInventariosDisplay().getValue();
        boolean hasData = currentInventories != null && !currentInventories.isEmpty();

        if (isLoading) {
            binding.recyclerViewInventories.setVisibility(View.GONE);
            errorCardView.setVisibility(View.GONE);
            binding.textViewNoData.setVisibility(View.GONE);
        } else {
            // Si no está cargando
            if (currentErrorMessage != null && !currentErrorMessage.isEmpty()) {
                errorMessageTextView.setText(currentErrorMessage);
                errorCardView.setVisibility(View.VISIBLE);
                binding.recyclerViewInventories.setVisibility(View.GONE);
                binding.textViewNoData.setVisibility(View.GONE);
            } else {
                // No hay mensaje de error
                errorCardView.setVisibility(View.GONE);

                if (hasData) {
                    // Hay datos para mostrar
                    binding.recyclerViewInventories.setVisibility(View.VISIBLE);
                    binding.textViewNoData.setVisibility(View.GONE);
                } else {
                    // No hay datos y no hay error (ej. lista vacía al inicio o después de un filtro sin búsqueda activa)
                    binding.recyclerViewInventories.setVisibility(View.GONE);
                    binding.textViewNoData.setVisibility(View.VISIBLE);
                    binding.textViewNoData.setText("No hay inventarios disponibles.");
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}