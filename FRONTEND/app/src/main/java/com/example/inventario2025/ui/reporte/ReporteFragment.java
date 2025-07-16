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
import com.example.inventario2025.data.remote.api.ApiClient;
import com.example.inventario2025.data.remote.api.MovimientoService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReporteFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReporteAdapter adapter;
    private Button btnSeleccionarFecha;
    private MovimientoService movimientoService;

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
        movimientoService = ApiClient.getClient().create(MovimientoService.class);

        btnSeleccionarFecha.setOnClickListener(v -> mostrarSelectorFecha());
    }

    private void mostrarSelectorFecha() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String fechaSeleccionada = sdf.format(calendar.getTime());
                    obtenerMovimientosPorFecha(fechaSeleccionada);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void obtenerMovimientosPorFecha(String fecha) {
        movimientoService.getMovimientos().enqueue(new Callback<List<ReporteMovimiento>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReporteMovimiento>> call, @NonNull Response<List<ReporteMovimiento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReporteMovimiento> movimientos = response.body();
                    adapter = new ReporteAdapter(movimientos);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReporteMovimiento>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
