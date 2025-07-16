package com.example.inventario2025.ui.dialogos;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Colaborador;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.data.local.entities.Usuario;
import com.example.inventario2025.databinding.DialogColaboradoresBinding;
import com.example.inventario2025.ui.adapters.ColaboradorAdapter;
import com.example.inventario2025.ui.listaInventorio.InventorioListViewModel;
import com.example.inventario2025.utils.SharedPrefManager;
import com.example.inventario2025.utils.ToastUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ColaboradoresDialogFragment extends DialogFragment {

    private static final String ARG_INVENTARIO = "inventario_obj";
    private InventorioListViewModel inventarioListViewModel;
    private DialogColaboradoresBinding binding;
    private ColaboradorAdapter colaboradorAdapter;
    private Inventario currentInventario;
    private Usuario usuarioSeleccionado = null;

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

        setupTitle();
        setupRecyclerView();
        setupObservers();
        setupAutoComplete();

        if (currentInventario != null) {
            inventarioListViewModel.loadColaboradores(currentInventario.getIdInventario());
            inventarioListViewModel.loadAllActiveUsers();
        }
    }

    private void setupTitle() {
        if (currentInventario != null) {
            binding.textViewColaboradoresTitle.setText("Colaboradores de: " + currentInventario.getDescripcionInventario());
        } else {
            binding.textViewColaboradoresTitle.setText("Colaboradores");
            binding.textViewColaboradoresError.setText("Error crítico: No se pudo obtener la información del inventario.");
            binding.textViewColaboradoresError.setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclerView() {
        colaboradorAdapter = new ColaboradorAdapter();
        binding.recyclerViewColaboradores.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewColaboradores.setAdapter(colaboradorAdapter);

        colaboradorAdapter.setOnColaboradorActionListener(colaborador -> {
            if (currentInventario != null) {
                if ("OWNR".equalsIgnoreCase(currentInventario.getRangoColaborador())) {
                    inventarioListViewModel.deleteColaborador(colaborador.getIdColaboradores(), currentInventario.getIdInventario());
                } else {
                    ToastUtils.showWarningToast(getParentFragmentManager(), "Solo el propietario del inventario puede eliminar colaboradores.");
                }
            } else {
                ToastUtils.showErrorToast(getParentFragmentManager(), "No se puede eliminar. Información de inventario no disponible.");
            }
        });
    }

    private void setupAutoComplete() {
        AutoCompleteTextView autoComplete = binding.autoCompleteTextViewUsername;
        binding.buttonAddCollab.setEnabled(false);

        inventarioListViewModel.allActiveUsers.observe(getViewLifecycleOwner(), usuarios -> {
            if (usuarios == null) return;
            List<String> usernames = usuarios.stream().map(Usuario::getUsername).collect(Collectors.toList());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, usernames);
            autoComplete.setAdapter(adapter);
        });

        autoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUsername = (String) parent.getItemAtPosition(position);

            usuarioSeleccionado = inventarioListViewModel.allActiveUsers.getValue().stream()
                    .filter(u -> u.getUsername().equals(selectedUsername))
                    .findFirst()
                    .orElse(null);

            if (usuarioSeleccionado != null) {
                boolean yaEsColaborador = false;
                List<Colaborador> colaboradoresActuales = inventarioListViewModel.colaboradores.getValue();
                if (colaboradoresActuales != null) {
                    yaEsColaborador = colaboradoresActuales.stream()
                            .anyMatch(c -> c.getUsername().equalsIgnoreCase(usuarioSeleccionado.getUsername()));
                }

                if (yaEsColaborador) {
                    ToastUtils.showWarningToast(getParentFragmentManager(), "Este usuario ya es un colaborador.");
                    binding.buttonAddCollab.setEnabled(false);
                } else {
                    binding.buttonAddCollab.setEnabled(true);
                }
            } else {
                binding.buttonAddCollab.setEnabled(false);
            }
        });

        autoComplete.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (usuarioSeleccionado == null || !s.toString().equals(usuarioSeleccionado.getUsername())) {
                    usuarioSeleccionado = null;
                    binding.buttonAddCollab.setEnabled(false);
                }
            }
        });

        binding.buttonAddCollab.setOnClickListener(v -> {
            if (usuarioSeleccionado != null && currentInventario != null) {
                inventarioListViewModel.addColaborador(currentInventario.getIdInventario(), usuarioSeleccionado.getIdUsuario());
                autoComplete.setText("");
                usuarioSeleccionado = null;
                binding.buttonAddCollab.setEnabled(false);
            } else {
                ToastUtils.showWarningToast(getParentFragmentManager(), "Debes seleccionar un usuario válido de la lista.");
            }
        });
    }

    private void setupObservers() {
        inventarioListViewModel.colaboradores.observe(getViewLifecycleOwner(), colaboradores -> {
            colaboradorAdapter.setColaboradorList(colaboradores);
            if (colaboradores == null || colaboradores.isEmpty()) {
                binding.textViewColaboradoresError.setText("No hay colaboradores en este inventario.");
                binding.textViewColaboradoresError.setVisibility(View.VISIBLE);
            } else {
                binding.textViewColaboradoresError.setVisibility(View.GONE);
            }
        });

        inventarioListViewModel.isColaboradoresLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBarColaboradores.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        inventarioListViewModel.colaboradoresSuccessMessage.observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty()) {
                ToastUtils.showSuccessToast(getParentFragmentManager(), successMessage);
                inventarioListViewModel.clearColaboradoresSuccessMessage();
            }
        });

        inventarioListViewModel.colaboradoresErrorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                ToastUtils.showErrorToast(getParentFragmentManager(), errorMessage);
                inventarioListViewModel.clearColaboradoresErrorMessage();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}