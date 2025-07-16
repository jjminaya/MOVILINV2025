package com.example.inventario2025.ui.listaElementos;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import android.graphics.Bitmap;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Elemento;
import com.example.inventario2025.databinding.FragmentDetalleElementoBinding;

public class DetalleElementoDialogFragment extends DialogFragment {

    private FragmentDetalleElementoBinding binding;
    private Elemento elemento;

    public static DetalleElementoDialogFragment newInstance(Elemento elemento) {
        DetalleElementoDialogFragment fragment = new DetalleElementoDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("elemento", elemento);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, com.google.android.material.R.style.Theme_Material3_Light_Dialog);

        if (getArguments() != null) {
            elemento = (Elemento) getArguments().getSerializable("elemento");
        }
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentDetalleElementoBinding.inflate(inflater, container, false);

        if (elemento != null) {
            setTextForInclude(binding.getRoot(), R.id.atributoCodigo, "Código único", valorSeguro(elemento.getUniCodeElemento()));
            setTextForInclude(binding.getRoot(), R.id.atributoDescripcion, "Descripción", valorSeguro(elemento.getDescripcionElemento()));
            setTextForInclude(binding.getRoot(), R.id.atributoMarca, "Marca", valorSeguro(elemento.getMarcaElemento()));
            setTextForInclude(binding.getRoot(), R.id.atributoModelo, "Modelo", valorSeguro(elemento.getModeloElemento()));
            setTextForInclude(binding.getRoot(), R.id.atributoColor, "Color", valorSeguro(elemento.getColorElemento()));
            setTextForInclude(binding.getRoot(), R.id.atributoEstadoFisico, "Estado físico", valorSeguro(elemento.getEstadoElemento()));
            setTextForInclude(binding.getRoot(), R.id.atributoEstadoSistema, "Estado en sistema", (elemento.getEstado() == 1 ? "Activo" : "Inactivo"));
        }
        if (elemento != null) {
            String codigo = valorSeguro(elemento.getUniCodeElemento());
            Bitmap codigoBarrasBitmap = generarCodigoBarras(codigo);
            if (codigoBarrasBitmap != null) {
                binding.imgCodigoBarras.setImageBitmap(codigoBarrasBitmap);
            }
        }
        binding.btnCerrar.setOnClickListener(v -> dismiss());

        return binding.getRoot();
    }


    private void setTextForInclude(View root, int includeId, String titulo, String valor) {
        View includeView = root.findViewById(includeId);
        if (includeView != null) {
            TextView tvLabel = includeView.findViewById(R.id.label);
            TextView tvValue = includeView.findViewById(R.id.text);
            tvLabel.setText(titulo);
            tvValue.setText(valor);
        }
    }

    private String valorSeguro(String texto) {
        return texto != null && !texto.trim().isEmpty() ? texto : "(Sin datos)";
    }

    private Bitmap generarCodigoBarras(String codigo) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(codigo, BarcodeFormat.CODE_128, 600, 150);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Ajuste de tamaño al 90% del ancho de pantalla
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}



