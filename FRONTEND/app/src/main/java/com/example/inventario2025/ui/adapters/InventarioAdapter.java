package com.example.inventario2025.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Inventario;
import com.example.inventario2025.databinding.InventorioItemBinding;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class InventarioAdapter extends RecyclerView.Adapter<InventarioAdapter.InventarioViewHolder> {

    private List<Inventario> inventarioList = new ArrayList<>();
    private OnItemActionListener onItemActionListener;

    // Interfaz para manejar los clics de los botones de acción
    public interface OnItemActionListener {
        void onEditClick(Inventario inventario);
        void onAddCollaboratorClick(Inventario inventario);
        void onDeleteClick(Inventario inventario);
        void onItemClick(Inventario inventario);
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.onItemActionListener = listener;
    }

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

        holder.editButton.setOnClickListener(v -> {
            if (onItemActionListener != null) {
                onItemActionListener.onEditClick(currentInventario);
            }
        });

        holder.addCollaboratorButton.setOnClickListener(v -> {
            if (onItemActionListener != null) {
                onItemActionListener.onAddCollaboratorClick(currentInventario);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (onItemActionListener != null) {
                onItemActionListener.onDeleteClick(currentInventario);
            }
        });

        holder.toggleActionsButton.setOnClickListener(v -> {
            if (holder.actionButtonsContainer.getVisibility() == View.VISIBLE) {
                holder.actionButtonsContainer.setVisibility(View.GONE);
                holder.toggleActionsButton.setImageResource(R.drawable.expand_all_24px);
            } else {
                holder.actionButtonsContainer.setVisibility(View.VISIBLE);
                holder.toggleActionsButton.setImageResource(R.drawable.collapse_all_24px);
            }
        });

        // NUEVO: Listener para el clic en el MaterialCardView completo
        holder.materialCardView.setOnClickListener(v -> {
            if (onItemActionListener != null) {
                onItemActionListener.onItemClick(currentInventario);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inventarioList.size();
    }

    public static class InventarioViewHolder extends RecyclerView.ViewHolder {
        private final TextView idTextView;
        private final TextView descripcionTextView;
        private final TextView elementosTextView;
        private final ImageButton editButton;
        private final ImageButton addCollaboratorButton;
        private final ImageButton deleteButton;
        private final ImageButton toggleActionsButton;
        private final LinearLayout actionButtonsContainer;
        private final MaterialCardView materialCardView;

        // Constructor que recibe el objeto de binding
        public InventarioViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.inventory_id_text_view);
            descripcionTextView = itemView.findViewById(R.id.inventory_description_text_view);
            elementosTextView = itemView.findViewById(R.id.inventory_elements_text_view);
            editButton = itemView.findViewById(R.id.button_edit_inventory);
            addCollaboratorButton = itemView.findViewById(R.id.button_add_collaborator);
            deleteButton = itemView.findViewById(R.id.button_delete_inventory);
            toggleActionsButton = itemView.findViewById(R.id.button_toggle_actions);
            actionButtonsContainer = itemView.findViewById(R.id.action_buttons_container);
            materialCardView = (MaterialCardView) itemView;
        }

        // Metodo bind que recibe el Inventario y el listener
        public void bind(Inventario inventario) {
            idTextView.setText("ID: " + inventario.getIdInventario());
            descripcionTextView.setText(inventario.getDescripcionInventario());
            elementosTextView.setText("Elementos: " + inventario.getElementosInventario());
        }
    }
}