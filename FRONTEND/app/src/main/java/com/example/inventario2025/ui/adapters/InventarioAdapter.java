package com.example.inventario2025.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.databinding.InventorioItemBinding;

import java.util.ArrayList;
import java.util.List;

public class InventarioAdapter extends RecyclerView.Adapter<InventarioAdapter.InventarioViewHolder> {

    private List<Inventario> inventarioList = new ArrayList<>();
    private OnItemActionListener listener;

    // Interfaz para manejar los clics de los botones de acción
    public interface OnItemActionListener {
        void onEditClick(Inventario inventario);
        void onAddCollaboratorClick(Inventario inventario);
        void onDeleteClick(Inventario inventario);
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.listener = listener;
    }

    public void setInventarioList(List<Inventario> inventarioList) {
        this.inventarioList = inventarioList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        InventorioItemBinding binding = InventorioItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new InventarioViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InventarioViewHolder holder, int position) {
        Inventario currentInventario = inventarioList.get(position);
        holder.bind(currentInventario, listener);
    }

    @Override
    public int getItemCount() {
        return inventarioList.size();
    }

    public static class InventarioViewHolder extends RecyclerView.ViewHolder {
        private final InventorioItemBinding binding;

        // Constructor que recibe el objeto de binding
        public InventarioViewHolder(@NonNull InventorioItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Metodo bind que recibe el Inventario y el listener
        public void bind(Inventario inventario, OnItemActionListener listener) {
            binding.inventoryIdTextView.setText("ID: " + inventario.getIdInventario());
            binding.inventoryDescriptionTextView.setText(inventario.getDescripcionInventario());
            binding.inventoryElementsTextView.setText("Elementos: " + inventario.getElementosInventario());

            // Lógica para mostrar/ocultar botones de acción con animación
            binding.buttonToggleActions.setOnClickListener(v -> {
                Context context = v.getContext();

                if (binding.actionButtonsContainer.getVisibility() == View.GONE) {
                    // Mostrar botones con animación de deslizamiento hacia abajo
                    binding.actionButtonsContainer.setVisibility(View.VISIBLE);
                    binding.actionButtonsContainer.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animacion_cortina_abajo));
                    binding.buttonToggleActions.setImageResource(R.drawable.collapse_all_24px);
                } else {
                    // Ocultar botones con animación de deslizamiento hacia arriba
                    binding.actionButtonsContainer.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animacion_cortina_arriba));
                    binding.actionButtonsContainer.postDelayed(() -> {
                        binding.actionButtonsContainer.setVisibility(View.GONE);
                    }, 250); // duración de animación

                    binding.buttonToggleActions.setImageResource(R.drawable.expand_all_24px);
                }
            });

            // Asignar OnClickListeners a los botones de acción
            if (listener != null) {
                binding.buttonEditInventory.setOnClickListener(v -> listener.onEditClick(inventario));
                binding.buttonAddCollaborator.setOnClickListener(v -> listener.onAddCollaboratorClick(inventario));
                binding.buttonDeleteInventory.setOnClickListener(v -> listener.onDeleteClick(inventario));
            }
        }
    }
}