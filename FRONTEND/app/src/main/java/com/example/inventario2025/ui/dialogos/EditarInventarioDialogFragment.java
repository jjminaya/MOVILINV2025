package com.example.inventario2025.ui.dialogos;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.databinding.CrearInventarioBinding;
import com.example.inventario2025.ui.listaInventorio.InventorioListViewModel;

public class EditarInventarioDialogFragment extends DialogFragment {

    private static final String ARG_INVENTARIO_ID = "inventario_id";
    private static final String ARG_INVENTARIO_DESCRIPTION = "inventario_description";
    private InventorioListViewModel inventarioListViewModel;
    private CrearInventarioBinding binding;

    private int inventarioId;
    private String currentDescription;

    // Metodo para pasar el Inventario
    public static EditarInventarioDialogFragment newInstance(Inventario inventario) {
        EditarInventarioDialogFragment fragment = new EditarInventarioDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INVENTARIO_ID, inventario.getIdInventario());
        args.putString(ARG_INVENTARIO_DESCRIPTION, inventario.getDescripcionInventario());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            inventarioId = getArguments().getInt(ARG_INVENTARIO_ID);
            currentDescription = getArguments().getString(ARG_INVENTARIO_DESCRIPTION);
        }
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

        inventarioListViewModel = new ViewModelProvider(requireActivity()).get(InventorioListViewModel.class);

        binding.textViewDialogTitle.setText("Editar Inventario");
        binding.buttonAction.setText("Editar");

        if (currentDescription != null) {
            binding.editTextDescription.setText(currentDescription);
            binding.editTextDescription.setSelection(currentDescription.length());
        }

        binding.buttonCancel.setOnClickListener(v -> dismiss());

        binding.buttonAction.setOnClickListener(v -> {
            String newDescription = binding.editTextDescription.getText().toString().trim();
            if (!newDescription.isEmpty()) {
                // Llamar al ViewModel para actualizar el inventario
                inventarioListViewModel.updateInventario(inventarioId, newDescription);
                dismiss();
            } else {
                Toast.makeText(getContext(), "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show();
            }
        });

        // Observar mensajes de error del ViewModel
        inventarioListViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), "Error al editar: " + message, Toast.LENGTH_LONG).show();
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

            // Para que el teclado no oculte el input
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
