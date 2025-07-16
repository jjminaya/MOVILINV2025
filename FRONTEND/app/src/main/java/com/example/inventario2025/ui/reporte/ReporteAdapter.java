package com.example.inventario2025.ui.reporte;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventario2025.R;

import java.util.List;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ViewHolder>{
    private List<ReporteItem> ReporteList;

    public ReporteAdapter(List<ReporteItem> ReporteList) {
        this.ReporteList = ReporteList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitulo, txtFecha, txtDetalle;

        public ViewHolder(View view) {
            super(view);
            txtTitulo = view.findViewById(R.id.txtTitulo);
            txtFecha = view.findViewById(R.id.txtFecha);
            txtDetalle = view.findViewById(R.id.txtDetalle);
        }
    }

    @Override
    public ReporteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReporteItem item = ReporteList.get(position);
        holder.txtTitulo.setText(item.getTitulo());
        holder.txtFecha.setText(item.getFecha());
        holder.txtDetalle.setText(item.getDetalle());
    }

    @Override
    public int getItemCount() {
        return ReporteList.size();
    }
}
