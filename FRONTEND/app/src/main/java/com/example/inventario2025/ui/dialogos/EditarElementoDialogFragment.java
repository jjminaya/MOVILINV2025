package com.example.inventario2025.ui.dialogos;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.inventario2025.data.local.entities.Elemento;
import com.example.inventario2025.databinding.DialogEditarElementoBinding;

public class EditarElementoDialogFragment extends DialogFragment {

    private static final String ARG_ELEMENTO = "elemento";
    private DialogEditarElementoBinding binding;
    private Elemento elemento;
    private OnElementoUpdatedListener listener;

    public interface OnElementoUpdatedListener {
        void onElementoUpdated(Elemento elemento);
    }

    public static EditarElementoDialogFragment newInstance(Elemento elemento) {
        EditarElementoDialogFragment fragment = new EditarElementoDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ELEMENTO, elemento);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnElementoUpdatedListener(OnElementoUpdatedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogEditarElementoBinding.inflate(LayoutInflater.from(getContext()));

        if (getArguments() != null) {
            elemento = (Elemento) getArguments().getSerializable(ARG_ELEMENTO);
            cargarDatosEnCampos();
        }

        return new AlertDialog.Builder(requireContext())
                .setTitle("Editar Elemento")
                .setView(binding.getRoot())
                .setPositiveButton("Guardar", (dialog, which) -> {
                    guardarCambios();
                })
                .setNegativeButton("Cancelar", null)
                .create();
    }

    private void cargarDatosEnCampos() {
        binding.etDescripcion.setText(elemento.getDescripcionElemento());
        binding.etMarca.setText(elemento.getMarcaElemento());
        binding.etModelo.setText(elemento.getModeloElemento());
        binding.etColor.setText(elemento.getColorElemento());
        binding.etEstadoFisico.setText(elemento.getEstadoElemento());
    }

    private void guardarCambios() {
        elemento.setDescripcionElemento(binding.etDescripcion.getText().toString());
        elemento.setMarcaElemento(binding.etMarca.getText().toString());
        elemento.setModeloElemento(binding.etModelo.getText().toString());
        elemento.setColorElemento(binding.etColor.getText().toString());
        elemento.setEstadoElemento(binding.etEstadoFisico.getText().toString());

        if (listener != null) {
            listener.onElementoUpdated(elemento);
        }
    }
}

