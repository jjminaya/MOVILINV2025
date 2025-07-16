package com.example.inventario2025.ui.listaElementos;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.inventario2025.databinding.FragmentDetalleElementoBinding;
import com.example.inventario2025.data.local.entities.Elemento;

public class DetalleElementoFragment extends Fragment {

    private FragmentDetalleElementoBinding binding;
    private Elemento elemento;

    public DetalleElementoFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            elemento = (Elemento) getArguments().getSerializable("elemento");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalleElementoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (elemento != null) {
            binding.tvCodigo.setText("Código único: " + valorSeguro(elemento.getUniCodeElemento()));
            binding.tvDescripcion.setText("Descripción: " + valorSeguro(elemento.getDescripcionElemento()));
            binding.tvMarca.setText("Marca: " + valorSeguro(elemento.getMarcaElemento()));
            binding.tvModelo.setText("Modelo: " + valorSeguro(elemento.getModeloElemento()));
            binding.tvColor.setText("Color: " + valorSeguro(elemento.getColorElemento()));
            binding.tvEstadoFisico.setText("Estado físico: " + valorSeguro(elemento.getEstadoElemento()));
            binding.tvEstadoSistema.setText("Estado en sistema: " + (elemento.getEstado() == 1 ? "Activo" : "Inactivo"));
        } else {
            binding.tvTitulo.setText("Elemento no encontrado");
        }
    }

    private String valorSeguro(String texto) {
        return texto != null && !texto.trim().isEmpty() ? texto : "(Sin datos)";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


