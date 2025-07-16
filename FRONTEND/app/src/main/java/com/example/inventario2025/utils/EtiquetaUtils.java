package com.example.inventario2025.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Elemento;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class EtiquetaUtils {

    public static Bitmap crearBitmapDeEtiqueta(Context context, Elemento elemento) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.etiqueta_layout, null);

        ImageView logo = view.findViewById(R.id.logo_etiqueta);
        ImageView barcodeImage = view.findViewById(R.id.barcode_image);
        TextView unicodeText = view.findViewById(R.id.unicode_text);

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            String code = (elemento.getUniCodeElemento() != null && !elemento.getUniCodeElemento().isEmpty())
                    ? elemento.getUniCodeElemento()
                    : "NO-CODE";

            Bitmap bitmap = barcodeEncoder.encodeBitmap(code, BarcodeFormat.CODE_128, 400, 80);
            barcodeImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logo.setImageResource(R.drawable.login1);
        unicodeText.setText(elemento.getUniCodeElemento() != null ? elemento.getUniCodeElemento().toUpperCase() : "N/A");

        return viewToBitmap(view);
    }

    private static Bitmap viewToBitmap(View view) {
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
}