package com.example.inventario2025.utils;

import androidx.fragment.app.FragmentManager;

public class ToastUtils {

    public static final int DURATION_SHORT = 2000;
    public static final int DURATION_LONG = 3500;

    public static void showSuccessToast(FragmentManager fragmentManager, String message) {
        CustomToast.newInstance(CustomToast.Type.SUCCESS, message, DURATION_SHORT)
                .show(fragmentManager, "CustomToastSuccess");
    }

    public static void showErrorToast(FragmentManager fragmentManager, String message) {
        CustomToast.newInstance(CustomToast.Type.ERROR, message, DURATION_LONG)
                .show(fragmentManager, "CustomToastError");
    }

    public static void showWarningToast(FragmentManager fragmentManager, String message) {
        CustomToast.newInstance(CustomToast.Type.WARNING, message, DURATION_LONG)
                .show(fragmentManager, "CustomToastWarning");
    }

    public static void showInfoToast(FragmentManager fragmentManager, String message) {
        CustomToast.newInstance(CustomToast.Type.INFO, message, DURATION_SHORT)
                .show(fragmentManager, "CustomToastInfo");
    }
}
