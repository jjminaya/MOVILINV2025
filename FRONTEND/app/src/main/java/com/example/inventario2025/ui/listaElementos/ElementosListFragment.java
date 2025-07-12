package com.example.inventario2025.ui.listaElementos;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.inventario2025.utils.ToastUtils;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.data.local.entities.Elemento;
import com.example.inventario2025.databinding.FragmentElementosListBinding;
import com.example.inventario2025.ui.adapters.ElementosAdapter;

import java.util.ArrayList;
import java.util.List;

public class ElementosListFragment extends Fragment {

    private FragmentElementosListBinding binding;
    private Inventario currentInventario;
    private ElementosAdapter elementosAdapter;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        if (currentInventario != null) {
            binding.inventoryTitleElements.setText("Elementos de: " + currentInventario.getDescripcionInventario());
            loadSampleElements(currentInventario.getIdInventario());
        } else {
            binding.inventoryTitleElements.setText("Elementos");
            ToastUtils.showErrorToast(getParentFragmentManager(), "No se ha seleccionado un inventario para ver sus elementos.");

            binding.recyclerViewElements.setVisibility(View.GONE);
            binding.elementsTextViewNoData.setVisibility(View.GONE);
            binding.elementsErrorCardView.setVisibility(View.GONE);
            binding.elementsProgressBar.setVisibility(View.GONE);
        }

        setupClickListeners();
        setupSearchListener();
    }

    private void setupRecyclerView() {
        elementosAdapter = new ElementosAdapter();
        binding.recyclerViewElements.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewElements.setAdapter(elementosAdapter);

        elementosAdapter.setOnElementoActionListener(new ElementosAdapter.OnElementoActionListener() {
            @Override
            public void onEditElementClick(Elemento elemento) {
                Toast.makeText(getContext(), "Editar elemento: " + elemento.getDescripcionElemento(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrintCodeClick(Elemento elemento) {
                Toast.makeText(getContext(), "Imprimir unicdoe de: " + elemento.getDescripcionElemento(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteElementClick(Elemento elemento) {
                Toast.makeText(getContext(), "Eliminar elemento: " + elemento.getDescripcionElemento(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(Elemento elemento) {
                Toast.makeText(getContext(), "Clic en elemento: " + elemento.getDescripcionElemento(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        // Configurar el listener para el botón de la cámara (ejemplo)
        binding.searchElementsEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_24px, 0, R.drawable.photo_camera_24px, 0);
        binding.searchElementsEditText.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Botón de cámara clicado", Toast.LENGTH_SHORT).show();
        });

        // Configurar el listener para el botón de filtro (ejemplo)
        binding.filterElementsButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Botón de filtro clicado", Toast.LENGTH_SHORT).show();
        });

        binding.btnAddElement.setOnClickListener(v -> {
            if (currentInventario != null) {
                Toast.makeText(getContext(), "Agregando nuevo elemento al inventario: " + currentInventario.getDescripcionInventario(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Selecciona un inventario para agregar elementos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchListener() {
    }

    private void loadSampleElements(int inventarioId) {
        List<Elemento> sampleElements = new ArrayList<>();
        if (inventarioId == 1) { // ESTO SE BORRA CUANDO VENGA DESDE LA API
            sampleElements.add(new Elemento(101, 1, "Teclado mecánico RGB", 5, 1));
            sampleElements.add(new Elemento(102, 1, "Monitor curvo 27 pulgadas", 2, 1));
            sampleElements.add(new Elemento(103, 1, "Mouse ergonómico inalámbrico", 10, 1));
        } else if (inventarioId == 2) {
            sampleElements.add(new Elemento(201, 2, "Impresora multifuncional", 1, 1));
            sampleElements.add(new Elemento(202, 2, "Paquete de papel A4", 50, 1));
        } else {
            // No hay elementos para este inventario de prueba
        }

        if (sampleElements.isEmpty()) {
            binding.elementsTextViewNoData.setVisibility(View.VISIBLE);
            binding.recyclerViewElements.setVisibility(View.GONE);
        } else {
            elementosAdapter.setElementoList(sampleElements);
            binding.elementsTextViewNoData.setVisibility(View.GONE);
            binding.recyclerViewElements.setVisibility(View.VISIBLE);
        }
        binding.elementsProgressBar.setVisibility(View.GONE);
        binding.elementsErrorCardView.setVisibility(View.GONE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
