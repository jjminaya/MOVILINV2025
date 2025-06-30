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

import com.example.inventario2025.databinding.InventorioListBinding;
import com.example.inventario2025.ui.adapters.InventarioAdapter;
import com.example.inventario2025.ui.dialogos.CrearInventarioDialogFragment;

import java.util.ArrayList;

public class InventorioListFragment extends Fragment {

    private InventorioListBinding binding;
    private InventorioListViewModel inventarioListViewModel;
    private InventarioAdapter inventarioAdapter;

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

        setupRecyclerView();
        setupObservers();
        setupClickListeners();

        // Establecer el filtro inicial a "Creados por mí" (OWNED) al iniciar el fragmento
        binding.toggleButtonGroup.check(binding.btnMyInventories.getId());
    }

    private void setupRecyclerView() {
        // CORRECCIÓN 1: Instanciar InventarioAdapter sin argumentos
        inventarioAdapter = new InventarioAdapter();
        binding.recyclerViewInventories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewInventories.setAdapter(inventarioAdapter);
    }

    private void setupObservers() {
        // Observar la lista de inventarios FILTRADA que viene del ViewModel
        inventarioListViewModel.getInventariosDisplay().observe(getViewLifecycleOwner(), inventories -> {
            inventarioAdapter.setInventarioList(inventories);
            binding.textViewNoData.setVisibility(inventories == null || inventories.isEmpty() ? View.VISIBLE : View.GONE);
        });

        // Observar el estado de carga
        inventarioListViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (inventarioListViewModel.errorMessage.getValue() == null || inventarioListViewModel.errorMessage.getValue().isEmpty()) {
                binding.recyclerViewInventories.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            } else {
                binding.recyclerViewInventories.setVisibility(View.GONE);
            }
            binding.textViewError.setVisibility(View.GONE);
        });

        // Observar mensajes de error
        inventarioListViewModel.errorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                binding.textViewError.setText(errorMessage);
                binding.textViewError.setVisibility(View.VISIBLE);
                binding.recyclerViewInventories.setVisibility(View.GONE);
                binding.textViewNoData.setVisibility(View.GONE);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                binding.textViewError.setVisibility(View.GONE);
                if (inventarioListViewModel.getInventariosDisplay().getValue() != null &&
                        inventarioListViewModel.getInventariosDisplay().getValue().isEmpty()) {
                    binding.textViewNoData.setVisibility(View.VISIBLE);
                    binding.recyclerViewInventories.setVisibility(View.GONE);
                } else {
                    binding.textViewNoData.setVisibility(View.GONE);
                    binding.recyclerViewInventories.setVisibility(View.VISIBLE);
                }
            }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}