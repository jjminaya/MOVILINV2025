package com.example.inventario2025.ui.dialogos;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.inventario2025.R;
import com.example.inventario2025.ui.listaInventorio.InventorioListViewModel;
import com.example.inventario2025.databinding.CrearInventarioBinding;
import android.widget.Toast;

public class CrearInventarioDialogFragment extends DialogFragment {

    private InventorioListViewModel viewModel;
    private CrearInventarioBinding binding;

    public CrearInventarioDialogFragment() {
    }

    public static CrearInventarioDialogFragment newInstance() {
        return new CrearInventarioDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CrearInventarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(InventorioListViewModel.class);

        binding.textViewDialogTitle.setText("Crear Nuevo Inventario");
        binding.buttonAction.setText("Crear");

        binding.buttonCancel.setOnClickListener(v -> dismiss());

        binding.buttonAction.setOnClickListener(v -> {
            String description = binding.editTextDescription.getText().toString().trim();
            if (!description.isEmpty()) {
                viewModel.createNewInventario(description, 1);
                dismiss();
            } else {
                binding.textInputLayoutDescription.setError("La descripción no puede estar vacía");
            }
        });
        // Observar mensajes de error del ViewModel
        viewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), "Error al crear inventario: " + message, Toast.LENGTH_LONG).show();
            }
        });
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