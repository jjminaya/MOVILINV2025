package com.example.inventario2025.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
// import android.widget.Toast; // lo usaremos mas adelante

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Inventario;

import java.util.ArrayList;
import java.util.List;

public class InventarioAdapter extends RecyclerView.Adapter<InventarioAdapter.InventarioViewHolder> {

    private List<Inventario> inventarioList = new ArrayList<>();

    public void setInventarioList(List<Inventario> inventarioList) {
        this.inventarioList = inventarioList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventorio_item, parent, false);
        return new InventarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventarioViewHolder holder, int position) {
        Inventario currentInventario = inventarioList.get(position);
        holder.bind(currentInventario);
    }

    @Override
    public int getItemCount() {
        return inventarioList.size();
    }

    public static class InventarioViewHolder extends RecyclerView.ViewHolder {
        private final TextView idTextView;
        private final TextView descripcionTextView;
        private final TextView elementosTextView;

        // private final TextView estadoTextView; //talvez lo usemos para algo

        public InventarioViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.inventory_id_text_view);
            descripcionTextView = itemView.findViewById(R.id.inventory_description_text_view);
            elementosTextView = itemView.findViewById(R.id.inventory_elements_text_view);
            // estadoTextView = itemView.findViewById(R.id.inventory_status_text_view);
        }

        public void bind(Inventario inventario) {
            idTextView.setText("ID: " + inventario.getIdInventario());
            descripcionTextView.setText(inventario.getDescripcionInventario());
            elementosTextView.setText("Elementos: " + inventario.getElementosInventario());
            // estadoTextView.setText("Estado: " + (inventario.getEstado() == 1 ? "Activo" : "Inactivo"));
        }
    }
}