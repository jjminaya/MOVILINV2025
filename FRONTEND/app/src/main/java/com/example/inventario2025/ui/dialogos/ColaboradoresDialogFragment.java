package com.example.inventario2025.ui.dialogos;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.data.local.entities.Colaborador;
import com.example.inventario2025.databinding.DialogColaboradoresBinding;
import com.example.inventario2025.ui.adapters.ColaboradorAdapter;
import com.example.inventario2025.ui.listaInventorio.InventorioListViewModel;

import java.util.List;

public class ColaboradoresDialogFragment extends DialogFragment {

    private static final String ARG_INVENTARIO = "inventario_obj";
    private InventorioListViewModel inventarioListViewModel;
    private DialogColaboradoresBinding binding;
    private ColaboradorAdapter colaboradorAdapter;

    private Inventario currentInventario;

    public static ColaboradoresDialogFragment newInstance(Inventario inventario) {
        ColaboradoresDialogFragment fragment = new ColaboradoresDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INVENTARIO, inventario);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentInventario = (Inventario) getArguments().getSerializable(ARG_INVENTARIO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogColaboradoresBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inventarioListViewModel = new ViewModelProvider(requireActivity()).get(InventorioListViewModel.class);

        // Configurar el título del diálogo
        binding.textViewColaboradoresTitle.setText("Colaboradores de: " + currentInventario.getDescripcionInventario());

        // Configurar RecyclerView
        colaboradorAdapter = new ColaboradorAdapter();
        binding.recyclerViewColaboradores.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewColaboradores.setAdapter(colaboradorAdapter);

        // Implementar el listener para el botón de eliminar colaborador
        colaboradorAdapter.setOnColaboradorActionListener(new ColaboradorAdapter.OnColaboradorActionListener() {
            @Override
            public void onDeleteColaboradorClick(Colaborador colaborador) {
                Toast.makeText(getContext(), "Eliminando usuario: " + colaborador.getUsername(), Toast.LENGTH_SHORT).show();
            }
        });

        // Observar la lista de colaboradores del ViewModel
        inventarioListViewModel.colaboradores.observe(getViewLifecycleOwner(), colaboradores -> {
            colaboradorAdapter.setColaboradorList(colaboradores);
            // Mostrar/ocultar mensaje de no data o error
            if (colaboradores == null || colaboradores.isEmpty()) {
                binding.textViewColaboradoresError.setText("No hay colaboradores para este inventario.");
                binding.textViewColaboradoresError.setVisibility(View.VISIBLE);
            } else {
                binding.textViewColaboradoresError.setVisibility(View.GONE);
            }
        });

        // Observar el estado de carga de colaboradores
        inventarioListViewModel.isColaboradoresLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBarColaboradores.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.recyclerViewColaboradores.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            binding.textViewColaboradoresError.setVisibility(View.GONE); // Ocultar error mientras carga
        });

        // Observar mensajes de error de colaboradores
        inventarioListViewModel.colaboradoresErrorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                binding.textViewColaboradoresError.setText(errorMessage);
                binding.textViewColaboradoresError.setVisibility(View.VISIBLE);
                binding.recyclerViewColaboradores.setVisibility(View.GONE);
            } else {
                binding.textViewColaboradoresError.setVisibility(View.GONE);
            }
        });

        // Lógica para el botón "Agregar Colaborador"
        binding.buttonAddCollab.setOnClickListener(v -> {
            String username = binding.editTextUsername.getText().toString().trim();
            if (!username.isEmpty()) {
                Toast.makeText(getContext(), "Intentando agregar el usuario: " + username, Toast.LENGTH_SHORT).show();
                binding.editTextUsername.setText("");
            } else {
                binding.textInputLayoutUsername.setError("El username no puede estar vacío");
            }
        });

        // Cargar colaboradores al abrir el diálogo
        if (currentInventario != null) {
            inventarioListViewModel.loadColaboradores(currentInventario.getIdInventario());
        } else {
            binding.textViewColaboradoresError.setText("Error: No se pudo obtener el inventario.");
            binding.textViewColaboradoresError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}