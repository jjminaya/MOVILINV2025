package com.example.inventario2025.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Colaborador;
import com.example.inventario2025.databinding.ItemColaboradorBinding;

import java.util.ArrayList;
import java.util.List;

public class ColaboradorAdapter extends RecyclerView.Adapter<ColaboradorAdapter.ColaboradorViewHolder> {

    private List<Colaborador> colaboradorList = new ArrayList<>();
    private OnColaboradorActionListener listener;

    // Interfaz para manejar acciones en un colaborador
    public interface OnColaboradorActionListener {
        void onDeleteColaboradorClick(Colaborador colaborador);
    }

    public void setOnColaboradorActionListener(OnColaboradorActionListener listener) {
        this.listener = listener;
    }

    public void setColaboradorList(List<Colaborador> colaboradorList) {
        this.colaboradorList = colaboradorList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ColaboradorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemColaboradorBinding binding = ItemColaboradorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ColaboradorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ColaboradorViewHolder holder, int position) {
        Colaborador colaborador = colaboradorList.get(position);
        holder.bind(colaborador, listener);
    }

    @Override
    public int getItemCount() {
        return colaboradorList.size();
    }

    public static class ColaboradorViewHolder extends RecyclerView.ViewHolder {
        private final ItemColaboradorBinding binding;

        public ColaboradorViewHolder(@NonNull ItemColaboradorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Colaborador colaborador, OnColaboradorActionListener listener) {
            Context context = binding.getRoot().getContext();

            String fullName = "";
            if (colaborador.getNombresPersona() != null && !colaborador.getNombresPersona().isEmpty()) {
                fullName += colaborador.getNombresPersona().split(" ")[0];
            }
            if (colaborador.getApellidosPersona() != null && !colaborador.getApellidosPersona().isEmpty()) {
                if (!fullName.isEmpty()) fullName += " ";
                fullName += colaborador.getApellidosPersona().split(" ")[0];
            }
            binding.textViewFullName.setText(fullName);
            binding.textViewUsername.setText(colaborador.getUsername());

            // Configurar la cápsula de Rango
            String rango = colaborador.getRangoColaborador();
            if ("OWNR".equalsIgnoreCase(rango)) {
                binding.chipRango.setText("DUEÑO");
                binding.chipRango.setChipBackgroundColorResource(R.color.owner_chip_background);
                binding.chipRango.setTextColor(ContextCompat.getColor(context, R.color.owner_chip_text));
                binding.buttonDeleteCollab.setVisibility(View.GONE);
            } else if ("COLAB".equalsIgnoreCase(rango)) {
                binding.chipRango.setText("COLABORADOR");
                binding.chipRango.setChipBackgroundColorResource(R.color.colab_chip_background);
                binding.chipRango.setTextColor(ContextCompat.getColor(context, R.color.colab_chip_text));
                binding.buttonDeleteCollab.setVisibility(View.VISIBLE);
            } else {  // Mostrar el rango tal cual si es desconocido
                binding.chipRango.setText(rango);
                binding.chipRango.setChipBackgroundColorResource(R.color.default_chip_background);
                binding.chipRango.setTextColor(ContextCompat.getColor(context, R.color.default_chip_text));
                binding.buttonDeleteCollab.setVisibility(View.VISIBLE);
            }

            // Asignar OnClickListener al botón de eliminar
            if (listener != null) {
                if (!"OWNR".equalsIgnoreCase(rango)) { // Solo permitir eliminar si no es OWNR
                    binding.buttonDeleteCollab.setOnClickListener(v -> {
                        listener.onDeleteColaboradorClick(colaborador);
                    });
                } else {
                    binding.buttonDeleteCollab.setOnClickListener(null); // Desactivar clic para OWNR
                }
            }
        }
    }
}