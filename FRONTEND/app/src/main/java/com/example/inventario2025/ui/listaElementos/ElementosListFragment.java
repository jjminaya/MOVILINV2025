package com.example.inventario2025.ui.listaElementos;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.inventario2025.ui.dialogos.ConfirmationDialogFragment;
import com.example.inventario2025.ui.dialogos.CrearElementoDialogFragment;
import com.example.inventario2025.utils.ToastUtils;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.data.local.entities.Elemento;
import com.example.inventario2025.databinding.FragmentElementosListBinding;
import com.example.inventario2025.ui.adapters.ElementosAdapter;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ElementosListFragment extends Fragment implements
        ElementosAdapter.OnElementoActionListener,
        CrearElementoDialogFragment.OnElementoCreatedListener,
        ConfirmationDialogFragment.ConfirmationDialogListener{

    private FragmentElementosListBinding binding;
    private ElementosListViewModel elementosListViewModel;
    private ElementosAdapter elementosAdapter;
    private Inventario currentInventario;

    public ElementosListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentInventario = (Inventario) getArguments().getSerializable("inventario");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentElementosListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        elementosListViewModel = new ViewModelProvider(this).get(ElementosListViewModel.class);

        binding.recyclerViewElements.setLayoutManager(new LinearLayoutManager(getContext()));
        elementosAdapter = new ElementosAdapter();
        binding.recyclerViewElements.setAdapter(elementosAdapter);
        elementosAdapter.setOnElementoActionListener(this);

        if (currentInventario != null) {
            binding.inventoryTitleElements.setText("Elementos de: " + currentInventario.getDescripcionInventario());
            elementosListViewModel.setCurrentInventario(currentInventario);
        } else {
            binding.inventoryTitleElements.setText("Error: Inventario no encontrado.");
            binding.elementsNoDataCard.setVisibility(View.VISIBLE);
            binding.elementsErrorMessageTextView.setText("No se pudo cargar el inventario.");
        }

        elementosListViewModel.filteredElements.observe(getViewLifecycleOwner(), elementos -> {
            if (elementos != null && !elementos.isEmpty()) {
                elementosAdapter.setElementoList(elementos);
                binding.recyclerViewElements.setVisibility(View.VISIBLE);
                binding.elementsNoDataCard.setVisibility(View.GONE);
            } else {
                elementosAdapter.setElementoList(new ArrayList<>());
                binding.recyclerViewElements.setVisibility(View.GONE);
                binding.elementsNoDataCard.setVisibility(View.VISIBLE);
                if (elementosListViewModel.searchTerm.getValue() != null && !elementosListViewModel.searchTerm.getValue().isEmpty()) {
                    binding.elementsErrorMessageTextView.setText("Oops... No se encontró elemento con este nombre.");
                } else {
                    binding.elementsErrorMessageTextView.setText("No hay elementos en este inventario.");
                }
            }
        });

        elementosListViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.elementsProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        elementosListViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                ToastUtils.showErrorToast(getParentFragmentManager(), message);
                elementosListViewModel.clearErrorMessage();
            }
        });

        elementosListViewModel.successMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                ToastUtils.showSuccessToast(getParentFragmentManager(), message);
                elementosListViewModel.clearSuccessMessage();
            }
        });

        elementosListViewModel.infoMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                ToastUtils.showInfoToast(getParentFragmentManager(), message);
                elementosListViewModel.clearInfoMessage();
            }
        });

        elementosListViewModel.createElementoSuccess.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess != null && isSuccess) {
                elementosListViewModel.clearCreateElementoSuccess();
            }
        });

        elementosListViewModel.updateElementoSuccess.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess != null && isSuccess) {
                elementosListViewModel.clearUpdateElementoSuccess();
            }
        });

        elementosListViewModel.deleteElementoSuccess.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess != null && isSuccess) {
                elementosListViewModel.clearDeleteElementoSuccess();
            }
        });

        binding.searchElementsEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_24px, 0, R.drawable.photo_camera_24px, 0);
        binding.searchElementsEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            Drawable cameraDrawable = binding.searchElementsEditText.getCompoundDrawables()[DRAWABLE_RIGHT];

            if (cameraDrawable != null && event.getAction() == MotionEvent.ACTION_UP) {
                int clickableAreaWidth = (int) (48 * getResources().getDisplayMetrics().density);
                int clickableLeftBound = binding.searchElementsEditText.getRight() - clickableAreaWidth;

                if (event.getRawX() >= clickableLeftBound) {
                    NavHostFragment.findNavController(this).navigate(R.id.action_elementosListFragment_to_cameraScannerFragment);
                    return true;
                }
            }
            return false;
        });

        getParentFragmentManager().setFragmentResultListener("barcode_scan_request", this, (requestKey, bundle) -> {
            String barcode = bundle.getString("barcode_result");
            if (barcode != null) {
                binding.searchElementsEditText.setText(barcode);
            }
        });

        binding.btnAddElement.setOnClickListener(v -> {
            if (currentInventario != null) {
                CrearElementoDialogFragment dialogFragment = CrearElementoDialogFragment.newInstance(currentInventario);
                dialogFragment.setOnElementoCreatedListener(this);
                dialogFragment.show(getParentFragmentManager(), "CrearElementoDialogFragment");
            } else {
                ToastUtils.showErrorToast(getParentFragmentManager(), "No se puede agregar elemento. Información de inventario no disponible.");
            }
        });

        binding.searchElementsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                elementosListViewModel.searchElements(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onItemClick(Elemento elemento) {
        ToastUtils.showInfoToast(getParentFragmentManager(), "Elemento clicado: " + elemento.getDescripcionElemento());
    }

    @Override
    public void onEditElementClick(Elemento elemento) {
        //AQUI ENTRA JESUS
        ToastUtils.showInfoToast(getParentFragmentManager(), "Abriendo vista de editar: " + elemento.getDescripcionElemento());
    }

    @Override
    public void onPrintCodeClick(Elemento elemento) {
        // AQUI ENTRA JESUS
        ToastUtils.showInfoToast(getParentFragmentManager(), "Abriendo vista de impresion: " + elemento.getDescripcionElemento());
    }

    @Override
    public void onDeleteElementClick(Elemento elemento) {
        String title = "¿Estás seguro(a)?";
        String message = "Al aceptar eliminará el elemento \"" + elemento.getDescripcionElemento() + "\" de este inventario permanentemente.";

        ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
                title,
                message,
                elemento.getIdElemento(),
                R.drawable.warning_24px
        );
        dialogFragment.setConfirmationDialogListener(this);
        dialogFragment.show(getParentFragmentManager(), "DeleteElementoConfirmationDialog");
    }

    @Override
    public void onElementoCreated(Elemento elemento) {
        elementosListViewModel.addElemento(elemento);
    }

    // Implementación de la interfaz OnElementoUpdatedListener
//    @Override
//    public void onElementoUpdated(Elemento elemento) {
//        // AQUI ENTRA JESUS
//        elementosListViewModel.updateElemento(elemento);
//    }

    @Override
    public void onConfirmAction(int id) {
        elementosListViewModel.deleteElemento(id);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}