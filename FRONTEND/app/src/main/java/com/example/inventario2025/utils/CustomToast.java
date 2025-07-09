package com.example.inventario2025.utils;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.inventario2025.R;

public class CustomToast extends DialogFragment {

    private static final String ARG_TYPE = "toast_type";
    private static final String ARG_MESSAGE = "toast_message";
    private static final String ARG_DURATION = "toast_duration";

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO
    }

    private Type type;
    private String message;
    private int duration;

    private Handler handler;
    private Runnable dismissRunnable;
    private ProgressBar progressBar;
    private Handler progressHandler;
    private Runnable progressRunnable;

    public static CustomToast newInstance(Type type, String message, int duration) {
        CustomToast fragment = new CustomToast();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, type);
        args.putString(ARG_MESSAGE, message);
        args.putInt(ARG_DURATION, duration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = (Type) getArguments().getSerializable(ARG_TYPE);
            message = getArguments().getString(ARG_MESSAGE);
            duration = getArguments().getInt(ARG_DURATION);
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomToastDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_custom_toast, container, false);

        LinearLayout backgroundLayout = view.findViewById(R.id.toast_background_layout);
        ImageView iconView = view.findViewById(R.id.toast_icon);
        TextView titleView = view.findViewById(R.id.toast_title);
        TextView messageView = view.findViewById(R.id.toast_message);
        progressBar = view.findViewById(R.id.toast_progress_bar);

        int backgroundColorResId;
        int iconResId;
        String titleText;

        switch (type) {
            case SUCCESS:
                backgroundColorResId = R.color.toast_success_background;
                iconResId = R.drawable.check_24px;
                titleText = "Éxito";
                break;
            case ERROR:
                backgroundColorResId = R.color.toast_error_background;
                iconResId = R.drawable.error_24px;
                titleText = "Error";
                break;
            case WARNING:
                backgroundColorResId = R.color.toast_warning_background;
                iconResId = R.drawable.warning_24px;
                titleText = "Advertencia";
                break;
            case INFO:
            default:
                backgroundColorResId = R.color.toast_info_background;
                iconResId = R.drawable.info_24px;
                titleText = "Información";
                break;
        }

        GradientDrawable drawable = (GradientDrawable) backgroundLayout.getBackground();
        if (drawable != null) {
            drawable.setColor(ContextCompat.getColor(requireContext(), backgroundColorResId));
        } else {
            backgroundLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), backgroundColorResId));
        }

        iconView.setImageResource(iconResId);
        titleView.setText(titleText);
        messageView.setText(message);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.gravity = Gravity.TOP | Gravity.END;
                layoutParams.x = 16;
                layoutParams.y = 16;
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(layoutParams);
            }
        }

        // Iniciar el temporizador para ocultar el toast
        handler = new Handler();
        dismissRunnable = () -> {
            if (isAdded()) {
                dismiss();
            }
        };
        handler.postDelayed(dismissRunnable, duration);

        // Iniciar la barra de progreso
        progressBar.setMax(duration);
        progressBar.setProgress(duration);
        progressHandler = new Handler();
        final int updateInterval = 100;
        progressRunnable = new Runnable() {
            int remainingTime = duration;
            @Override
            public void run() {
                remainingTime -= updateInterval;
                if (remainingTime > 0) {
                    progressBar.setProgress(remainingTime);
                    progressHandler.postDelayed(this, updateInterval);
                } else {
                    progressBar.setProgress(0);
                }
            }
        };
        progressHandler.postDelayed(progressRunnable, updateInterval);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacks(dismissRunnable);
        }
        if (progressHandler != null) {
            progressHandler.removeCallbacks(progressRunnable);
        }
    }
}