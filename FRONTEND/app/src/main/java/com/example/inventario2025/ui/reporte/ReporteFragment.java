package com.example.inventario2025.ui.reporte;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.inventario2025.R;

import java.util.ArrayList;
import java.util.List;

public class ReporteFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReporteAdapter adapter;
    private List<ReporteItem> reporteItems;

    public ReporteFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reporte, container, false); // reutilizas el mismo layout
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerReporte);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reporteItems = new ArrayList<>();
        reporteItems.add(new ReporteItem("Inventario #1", "07 de julio de 2025", "ACTIVO"));
        reporteItems.add(new ReporteItem("Inventario #2", "07 de julio de 2025", "ACTIVO"));
        reporteItems.add(new ReporteItem("Inventario #3", "07 de julio de 2025", "ACTIVO"));

        adapter = new ReporteAdapter(reporteItems);
        recyclerView.setAdapter(adapter);
    }
}