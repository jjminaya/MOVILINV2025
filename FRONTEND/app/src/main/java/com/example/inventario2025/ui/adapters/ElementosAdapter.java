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
import com.example.inventario2025.data.local.entities.Elemento;
import com.example.inventario2025.databinding.ItemElementoBinding;

import java.util.ArrayList;
import java.util.List;

public class ElementosAdapter extends RecyclerView.Adapter<ElementosAdapter.ElementoViewHolder> {

    private List<Elemento> elementoList = new ArrayList<>();
    private OnElementoActionListener onItemActionListener;

    public interface OnElementoActionListener {
        void onEditElementClick(Elemento elemento);
        void onPrintCodeClick(Elemento elemento);
        void onDeleteElementClick(Elemento elemento);
        void onItemClick(Elemento elemento);
    }

    public void setOnElementoActionListener(OnElementoActionListener listener) {
        this.onItemActionListener = listener;
    }

    public void setElementoList(List<Elemento> elementoList) {
        this.elementoList = elementoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ElementoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_elemento, parent, false);
        return new ElementoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementoViewHolder holder, int position) {
        Elemento currentElemento = elementoList.get(position);
        holder.bind(currentElemento);

        holder.editButton.setOnClickListener(v -> {
            if (onItemActionListener != null) {
                onItemActionListener.onEditElementClick(currentElemento);
            }
        });

        holder.printCodeButton.setOnClickListener(v -> {
            if (onItemActionListener != null) {
                onItemActionListener.onPrintCodeClick(currentElemento);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (onItemActionListener != null) {
                onItemActionListener.onDeleteElementClick(currentElemento);
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

        // Listener para el clic en el ítem completo
        holder.itemView.setOnClickListener(v -> {
            if (onItemActionListener != null) {
                onItemActionListener.onItemClick(currentElemento);
            }
        });
    }

    @Override
    public int getItemCount() {
        return elementoList.size();
    }

    public static class ElementoViewHolder extends RecyclerView.ViewHolder {
        private final TextView idTextView;
        private final TextView descripcionTextView;
        private final ImageButton editButton;
        private final ImageButton printCodeButton;
        private final ImageButton deleteButton;
        private final ImageButton toggleActionsButton;
        private final LinearLayout actionButtonsContainer;

        public ElementoViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.element_id_text_view);
            descripcionTextView = itemView.findViewById(R.id.element_description_text_view);
            editButton = itemView.findViewById(R.id.button_edit_element);
            printCodeButton = itemView.findViewById(R.id.button_print_code_element);
            deleteButton = itemView.findViewById(R.id.button_delete_element);
            toggleActionsButton = itemView.findViewById(R.id.button_toggle_element_actions);
            actionButtonsContainer = itemView.findViewById(R.id.element_action_buttons_container);
        }

        public void bind(Elemento elemento) {
            idTextView.setText("ID: " + elemento.getIdElemento());
            descripcionTextView.setText(elemento.getDescripcionElemento());
        }
    }
}
