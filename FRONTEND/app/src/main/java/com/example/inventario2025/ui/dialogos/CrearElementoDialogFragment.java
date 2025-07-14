package com.example.inventario2025.ui.dialogos;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Elemento;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.databinding.DialogCrearElementoBinding;
import com.example.inventario2025.utils.ToastUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class CrearElementoDialogFragment extends DialogFragment {

    private static final String ARG_INVENTARIO = "inventario_obj";
    private DialogCrearElementoBinding binding;
    private Inventario currentInventario;
    private OnElementoCreatedListener listener;

    public interface OnElementoCreatedListener {
        void onElementoCreated(Elemento elemento);
    }

    public static CrearElementoDialogFragment newInstance(Inventario inventario) {
        CrearElementoDialogFragment fragment = new CrearElementoDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INVENTARIO, inventario);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnElementoCreatedListener(OnElementoCreatedListener listener) {
        this.listener = listener;
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
        binding = DialogCrearElementoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonCrearElemento.setOnClickListener(v -> {
            if (validateInputs()) {
                String unicode = Objects.requireNonNull(binding.editTextUnicode.getText()).toString().trim();
                String descripcion = Objects.requireNonNull(binding.editTextDescripcion.getText()).toString().trim();
                String marca = Objects.requireNonNull(binding.editTextMarca.getText()).toString().trim();
                String modelo = Objects.requireNonNull(binding.editTextModelo.getText()).toString().trim();
                String color = Objects.requireNonNull(binding.editTextColor.getText()).toString().trim();
                String estado = Objects.requireNonNull(binding.editTextEstado.getText()).toString().trim();

                // Validar que currentInventario no sea null
                if (currentInventario != null) {
                    int inventarioId = currentInventario.getIdInventario();
                    Elemento nuevoElemento = new Elemento(0, inventarioId, descripcion, 0, 1); // ID, cantidad y estado son placeholders si la API los maneja
                    nuevoElemento.setUniCodeElemento(unicode);
                    nuevoElemento.setMarcaElemento(marca);
                    nuevoElemento.setModeloElemento(modelo);
                    nuevoElemento.setColorElemento(color);
                    nuevoElemento.setEstadoElemento(estado);

                    if (listener != null) {
                        listener.onElementoCreated(nuevoElemento);
                    }
                    dismiss();
                } else {
                    ToastUtils.showErrorToast(getParentFragmentManager(), "Error: No se pudo obtener la información del inventario.");
                }
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        if (Objects.requireNonNull(binding.editTextUnicode.getText()).toString().trim().isEmpty()) {
            binding.textInputLayoutUnicode.setError("El código Unicode no puede estar vacío.");
            isValid = false;
        } else {
            binding.textInputLayoutUnicode.setError(null);
        }

        if (Objects.requireNonNull(binding.editTextDescripcion.getText()).toString().trim().isEmpty()) {
            binding.textInputLayoutDescripcion.setError("La descripción no puede estar vacía.");
            isValid = false;
        } else {
            binding.textInputLayoutDescripcion.setError(null);
        }

        if (Objects.requireNonNull(binding.editTextMarca.getText()).toString().trim().isEmpty()) {
            binding.textInputLayoutMarca.setError("La marca no puede estar vacía.");
            isValid = false;
        } else {
            binding.textInputLayoutMarca.setError(null);
        }

        if (Objects.requireNonNull(binding.editTextModelo.getText()).toString().trim().isEmpty()) {
            binding.textInputLayoutModelo.setError("El modelo no puede estar vacío.");
            isValid = false;
        } else {
            binding.textInputLayoutModelo.setError(null);
        }

        if (Objects.requireNonNull(binding.editTextColor.getText()).toString().trim().isEmpty()) {
            binding.textInputLayoutColor.setError("El color no puede estar vacío.");
            isValid = false;
        } else {
            binding.textInputLayoutColor.setError(null);
        }

        if (Objects.requireNonNull(binding.editTextEstado.getText()).toString().trim().isEmpty()) {
            binding.textInputLayoutEstado.setError("El estado no puede estar vacío.");
            isValid = false;
        } else {
            binding.textInputLayoutEstado.setError(null);
        }

        return isValid;
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
