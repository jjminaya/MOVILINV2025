package com.example.inventario2025.ui.reporte;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventario2025.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReporteFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReporteAdapter adapter;
    private Button btnSeleccionarFecha;

    public ReporteFragment() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reporte, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerReporte);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnSeleccionarFecha = view.findViewById(R.id.btnSeleccionarFecha);

        // Cargar datos de ejemplo al iniciar
        cargarMovimientosLocales();

        btnSeleccionarFecha.setOnClickListener(v -> mostrarSelectorFecha());
    }

    private void mostrarSelectorFecha() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String fechaSeleccionada = sdf.format(calendar.getTime());
                    filtrarMovimientosPorFecha(fechaSeleccionada);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private List<ReporteMovimiento> listaMock;

    private void cargarMovimientosLocales() {
        listaMock = new ArrayList<>();
        listaMock.add(new ReporteMovimiento(1, "2025-07-15", "Juan Pérez", "Elemento", "Crear", "Se agregó una Laptop Lenovo al inventario."));
        listaMock.add(new ReporteMovimiento(2, "2025-07-15", "María López", "Inventario", "Modificar", "Cambio de ubicación de proyector Epson."));
        listaMock.add(new ReporteMovimiento(3, "2025-07-14", "Carlos Ruiz", "Elemento", "Eliminar", "Se retiró un monitor LG dañado."));
        listaMock.add(new ReporteMovimiento(4, "2025-07-13", "Lucía Gómez", "Inventario", "Crear", "Nuevo inventario en Oficina Principal."));
        listaMock.add(new ReporteMovimiento(5, "2025-07-13", "Luis Díaz", "Elemento", "Modificar", "Actualización del router TP-Link."));
        listaMock.add(new ReporteMovimiento(6, "2025-07-12", "Ana Torres", "Elemento", "Crear", "Ingreso de teclado Logitech."));
        listaMock.add(new ReporteMovimiento(7, "2025-07-11", "Pedro Mendoza", "Elemento", "Eliminar", "Se eliminó un mouse en mal estado."));

        adapter = new ReporteAdapter(listaMock);
        recyclerView.setAdapter(adapter);
    }

    private void filtrarMovimientosPorFecha(String fecha) {
        List<ReporteMovimiento> filtrados = new ArrayList<>();
        for (ReporteMovimiento m : listaMock) {
            if (m.getFecha().equals(fecha)) {
                filtrados.add(m);
            }
        }

        adapter = new ReporteAdapter(filtrados);
        recyclerView.setAdapter(adapter);
    }
}
