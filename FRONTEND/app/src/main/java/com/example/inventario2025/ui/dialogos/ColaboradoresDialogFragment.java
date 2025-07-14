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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.data.local.entities.Colaborador;
import com.example.inventario2025.databinding.DialogColaboradoresBinding;
import com.example.inventario2025.ui.adapters.ColaboradorAdapter;
import com.example.inventario2025.ui.listaInventorio.InventorioListViewModel;
import com.example.inventario2025.utils.ToastUtils;

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
        if (currentInventario != null) {
            binding.textViewColaboradoresTitle.setText("Colaboradores de: " + currentInventario.getDescripcionInventario());
        } else {
            binding.textViewColaboradoresTitle.setText("Colaboradores");
            binding.textViewColaboradoresError.setText("Error crítico: No se pudo obtener la información del inventario.");
            binding.textViewColaboradoresError.setVisibility(View.VISIBLE);
            ToastUtils.showErrorToast(getParentFragmentManager(), "No se pudo cargar la información del inventario."); // AQUI CAMBIO
        }

        // Configurar RecyclerView
        colaboradorAdapter = new ColaboradorAdapter();
        binding.recyclerViewColaboradores.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewColaboradores.setAdapter(colaboradorAdapter);

        // Implementar el listener para el botón de eliminar colaborador
        colaboradorAdapter.setOnColaboradorActionListener(new ColaboradorAdapter.OnColaboradorActionListener() {
            @Override
            public void onDeleteColaboradorClick(Colaborador colaborador) {
                if (currentInventario != null) {
                    //Integer currentUserId = inventarioListViewModel.currentUserId.getValue();
                    Integer currentUserId = 1;
                    if (currentUserId != null && currentUserId == 1) { //currentInventario.getIdUsuario()
                        inventarioListViewModel.deleteColaborador(colaborador.getIdColaboradores(), currentInventario.getIdInventario());
                    } else {
                        ToastUtils.showWarningToast(getParentFragmentManager(), "Solo el propietario del inventario puede eliminar colaboradores.");
                    }
                } else {
                    ToastUtils.showErrorToast(getParentFragmentManager(), "No se puede eliminar el colaborador. Información de inventario no disponible.");
                }
            }
        });

        // Observar la lista de colaboradores del ViewModel
        inventarioListViewModel.colaboradores.observe(getViewLifecycleOwner(), colaboradores -> {
            colaboradorAdapter.setColaboradorList(colaboradores);
            // Mostrar/ocultar mensaje de no data o error
            if (colaboradores == null || colaboradores.isEmpty()) {
                binding.textViewColaboradoresError.setText("No hay colaboradores para este inventario.");
                binding.textViewColaboradoresError.setVisibility(View.VISIBLE);
                binding.recyclerViewColaboradores.setVisibility(View.GONE);
            } else {
                binding.textViewColaboradoresError.setVisibility(View.GONE);
                binding.recyclerViewColaboradores.setVisibility(View.VISIBLE);
            }
        });

        // Observar el estado de carga de colaboradores
        inventarioListViewModel.isColaboradoresLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBarColaboradores.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.recyclerViewColaboradores.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            binding.textViewColaboradoresError.setVisibility(View.GONE); // Ocultar error mientras carga
        });

        // Observar mensajes de error de colaboradores y mostrarlos con el Toast personalizado
        inventarioListViewModel.colaboradoresErrorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                String userFriendlyMessage;
                if (errorMessage.toLowerCase().contains("503") || errorMessage.toLowerCase().contains("not found")) {
                    userFriendlyMessage = "Error: El recurso solicitado no fue encontrado. Intenta de nuevo más tarde.";
                } else if (errorMessage.toLowerCase().contains("network") || errorMessage.toLowerCase().contains("failed to connect")) {
                    userFriendlyMessage = "Error de conexión: No se pudo conectar al servidor. Revisa tu conexión a internet.";
                } else if (errorMessage.toLowerCase().contains("404") || errorMessage.toLowerCase().contains("el usuario") && errorMessage.toLowerCase().contains("no existe")) {
                    userFriendlyMessage = "No existe este usuario.";
                }
                else {
                    userFriendlyMessage = "Ocurrió un error inesperado: " + errorMessage;
                }

                if (inventarioListViewModel.colaboradores.getValue() == null || inventarioListViewModel.colaboradores.getValue().isEmpty()) {
                    binding.textViewColaboradoresError.setText(userFriendlyMessage);
                    binding.textViewColaboradoresError.setVisibility(View.VISIBLE);
                    binding.recyclerViewColaboradores.setVisibility(View.GONE);
                } else {
                    binding.textViewColaboradoresError.setVisibility(View.GONE);
                }
                ToastUtils.showErrorToast(getParentFragmentManager(), userFriendlyMessage);
                inventarioListViewModel.clearColaboradoresErrorMessage();
            } else {
                binding.textViewColaboradoresError.setVisibility(View.GONE);
            }
        });

        inventarioListViewModel.userVerificationSuccess.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess != null && isSuccess) {
                binding.editTextUsername.setText("");
                binding.textInputLayoutUsername.setError(null);
            }
            inventarioListViewModel.clearUserVerificationSuccess();
        });

        // Observar mensajes de éxito del ViewModel
        inventarioListViewModel.colaboradoresSuccessMessage.observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty()) {
                ToastUtils.showSuccessToast(getParentFragmentManager(), successMessage);
                inventarioListViewModel.clearColaboradoresSuccessMessage();
            }
        });

        inventarioListViewModel.infoMessage.observe(getViewLifecycleOwner(), infoMessage -> {
            if (infoMessage != null && !infoMessage.isEmpty()) {
                ToastUtils.showInfoToast(getParentFragmentManager(), infoMessage);
                inventarioListViewModel.clearInfoMessage();
            }
        });

        // Lógica para el botón "Agregar Colaborador"
        binding.buttonAddCollab.setOnClickListener(v -> {
            String username = binding.editTextUsername.getText().toString().trim();
            if (!username.isEmpty() && currentInventario != null) {
                inventarioListViewModel.addColaborador(currentInventario.getIdInventario(), username);
            } else if (username.isEmpty()) {
                binding.textInputLayoutUsername.setError("El campo 'usuario' no puede estar vacío.");
                ToastUtils.showWarningToast(getParentFragmentManager(), "El campo 'usuario' no puede estar vacío.");
            } else {
                ToastUtils.showErrorToast(getParentFragmentManager(), "No se puede agregar el colaborador. Información de inventario no disponible.");
            }
        });

        // Cargar colaboradores al abrir el diálogo
        if (currentInventario != null) {
            inventarioListViewModel.loadColaboradores(currentInventario.getIdInventario());
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