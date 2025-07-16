package com.example.inventario2025.ui.dialogos;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.inventario2025.R;
import com.google.android.material.button.MaterialButton;

public class ConfirmationDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "dialog_title";
    private static final String ARG_MESSAGE = "dialog_message";
    private static final String ARG_ITEM_ID = "item_id";
    private static final String ARG_ICON_RES_ID = "icon_res_id";

    public interface ConfirmationDialogListener {
        void onConfirmAction(int itemId);
    }

    private ConfirmationDialogListener listener;
    private int itemIdToDelete;
    private int iconResId;

    public ConfirmationDialogFragment() {
    }

    public static ConfirmationDialogFragment newInstance(String title, String message, int itemId, int iconResId) {
        ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putInt(ARG_ITEM_ID, itemId);
        args.putInt(ARG_ICON_RES_ID, iconResId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setConfirmationDialogListener(ConfirmationDialogListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView iconImageView = view.findViewById(R.id.dialog_icon);
        TextView titleTextView = view.findViewById(R.id.dialog_title);
        TextView messageTextView = view.findViewById(R.id.dialog_message);
        MaterialButton confirmButton = view.findViewById(R.id.button_confirm);
        MaterialButton cancelButton = view.findViewById(R.id.button_cancel);

        if (getArguments() != null) {
            titleTextView.setText(getArguments().getString(ARG_TITLE));
            messageTextView.setText(getArguments().getString(ARG_MESSAGE));
            itemIdToDelete = getArguments().getInt(ARG_ITEM_ID);
            iconResId = getArguments().getInt(ARG_ICON_RES_ID, 0);

            if (iconResId != 0) {
                iconImageView.setImageResource(iconResId);
                iconImageView.setVisibility(View.VISIBLE);
            } else {
                iconImageView.setVisibility(View.GONE);
            }
        }

        confirmButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmAction(itemIdToDelete);
            }
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setGravity(android.view.Gravity.CENTER);
        }
    }
}