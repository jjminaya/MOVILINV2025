package com.example.inventario2025.ui.reporte;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventario2025.R;

import java.util.ArrayList;
import java.util.List;

public class ReporteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReporteAdapter adapter;
    private List<ReporteItem> ReporteItems;
    @Override
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);
        
        recyclerView = findViewById(R.id.recyclerReporte);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ReporteItems = new ArrayList<>();
        ReporteItems.add(new ReporteItem("Inventario #1", "07 de julio de 2025", "ACTIVO"));
        ReporteItems.add(new ReporteItem("Inventario #2", "07 de julio de 2025", "ACTIVO"));
        ReporteItems.add(new ReporteItem("Inventario #3", "07 de julio de 2025", "ACTIVO"));

        adapter = new ReporteAdapter(ReporteItems);
        recyclerView.setAdapter(adapter);
    }
}