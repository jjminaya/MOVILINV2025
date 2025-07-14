package com.example.inventario2025.utils;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class ToastUtils {

    public static final int DURATION_SUCCESS_SHORT = 2000;
    public static final int DURATION_ERROR_WARNING_LONG = 3500;
    public static final int DURATION_INFO = 2500;

    public static final String TAG_SUCCESS = "CustomToastSuccess";
    public static final String TAG_ERROR = "CustomToastError";
    public static final String TAG_WARNING = "CustomToastWarning";
    public static final String TAG_INFO = "CustomToastInfo";

    public static void showSuccessToast(FragmentManager fragmentManager, String message) {
        dismissCurrentToast(fragmentManager);
        CustomToast.newInstance(CustomToast.Type.SUCCESS, message, DURATION_SUCCESS_SHORT)
                .show(fragmentManager, TAG_SUCCESS);
    }

    public static void showErrorToast(FragmentManager fragmentManager, String message) {
        dismissCurrentToast(fragmentManager);
        CustomToast.newInstance(CustomToast.Type.ERROR, message, DURATION_ERROR_WARNING_LONG)
                .show(fragmentManager, TAG_ERROR);
    }

    public static void showWarningToast(FragmentManager fragmentManager, String message) {
        dismissCurrentToast(fragmentManager);
        CustomToast.newInstance(CustomToast.Type.WARNING, message, DURATION_ERROR_WARNING_LONG)
                .show(fragmentManager, TAG_WARNING);
    }

    public static void showInfoToast(FragmentManager fragmentManager, String message) {
        dismissCurrentToast(fragmentManager);
        CustomToast.newInstance(CustomToast.Type.INFO, message, DURATION_INFO)
                .show(fragmentManager, TAG_INFO);
    }

    private static void dismissCurrentToast(FragmentManager fragmentManager) {
        String[] tags = {TAG_SUCCESS, TAG_ERROR, TAG_WARNING, TAG_INFO};
        for (String tag : tags) {
            DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(tag);
            if (fragment != null) {
                fragment.dismissAllowingStateLoss();
            }
        }
    }
}
