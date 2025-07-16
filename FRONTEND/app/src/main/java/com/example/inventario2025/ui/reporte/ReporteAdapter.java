package com.example.inventario2025.ui.reporte;

import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.inventario2025.R;


import java.util.List;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ViewHolder> {

    private List<ReporteMovimiento> movimientoList;

    public ReporteAdapter(List<ReporteMovimiento> movimientoList) {
        this.movimientoList = movimientoList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtFecha, txtUsuario, txtAccion;
        TextView btnToggleDetalle;
        View layoutDetalle;

        public ViewHolder(View view) {
            super(view);
            txtTitulo = view.findViewById(R.id.txtTitulo);
            txtFecha = view.findViewById(R.id.txtFecha);
            txtUsuario = view.findViewById(R.id.txtUsuario);
            txtAccion = view.findViewById(R.id.txtAccion);
            btnToggleDetalle = view.findViewById(R.id.btnToggleDetalle);
            layoutDetalle = view.findViewById(R.id.layoutDetalles);
        }
    }

    @Override
    public ReporteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReporteMovimiento movimiento = movimientoList.get(position);

        // Mostrar un título sencillo combinando tipoObjeto y descripcion
        holder.txtTitulo.setText(movimiento.getAccion() + ": " + movimiento.getTipoObjeto());
        holder.txtFecha.setText(movimiento.getFecha());

        // Detalle expandido con usuario y acción
        holder.txtUsuario.setText("Usuario: " + movimiento.getUsuario());
        holder.txtAccion.setText("Descripción: " + movimiento.getDescripcion());

        holder.layoutDetalle.setVisibility(View.GONE);
        holder.btnToggleDetalle.setText("Ver detalle ▼");

        holder.btnToggleDetalle.setOnClickListener(v -> {
            if (holder.layoutDetalle.getVisibility() == View.VISIBLE) {
                collapse(holder.layoutDetalle);
                holder.btnToggleDetalle.setText("Ver detalle ▼");
            } else {
                expand(holder.layoutDetalle);
                holder.btnToggleDetalle.setText("Ocultar ▲");
            }
        });
    }

    @Override
    public int getItemCount() {
        return movimientoList.size();
    }

    // Métodos de animación
    private void expand(View view) {
        view.setVisibility(View.VISIBLE);
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int targetHeight = view.getMeasuredHeight();

        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int value = (Integer) animation.getAnimatedValue();
            view.getLayoutParams().height = value;
            view.requestLayout();
        });
        animator.start();
    }

    private void collapse(View view) {
        int initialHeight = view.getMeasuredHeight();

        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int value = (Integer) animation.getAnimatedValue();
            view.getLayoutParams().height = value;
            view.requestLayout();
            if (value == 0) {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }
}
