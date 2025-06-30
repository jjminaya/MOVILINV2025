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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CrearInventarioDialogFragment extends DialogFragment {

    private InventorioListViewModel viewModel;
    private TextInputLayout textInputLayoutDescription;
    private TextInputEditText editTextDescription;
    private Button buttonCreate;
    private Button buttonCancel;

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
        View view = inflater.inflate(R.layout.crear_inventario, container, false);
        textInputLayoutDescription = view.findViewById(R.id.textInputLayoutDescription);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonCreate = view.findViewById(R.id.buttonCreate);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(InventorioListViewModel.class);

        buttonCancel.setOnClickListener(v -> dismiss());

        buttonCreate.setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();
            if (!description.isEmpty()) {
                viewModel.createNewInventario(description, 1);
                dismiss();
            } else {
                textInputLayoutDescription.setError("La descripción no puede estar vacía");
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
}