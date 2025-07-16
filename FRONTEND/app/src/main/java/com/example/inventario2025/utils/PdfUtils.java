package com.example.inventario2025.utils;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.inventario2025.data.local.entities.Elemento;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfUtils {

    public static Bitmap generarCodigoBarras(String codigo) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(codigo, BarcodeFormat.CODE_128, 600, 200);
            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File crearPdfConCodigoDeBarras(Context context, Elemento elemento) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(612, 792, 1).create(); // A4
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(16);
        int y = 50;

        canvas.drawText("Detalle del Elemento", 40, y, paint); y += 40;
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(14);

        canvas.drawText("Código único: " + elemento.getUniCodeElemento(), 40, y, paint); y += 30;
        canvas.drawText("Descripción: " + elemento.getDescripcionElemento(), 40, y, paint); y += 30;
        canvas.drawText("Marca: " + elemento.getMarcaElemento(), 40, y, paint); y += 30;
        canvas.drawText("Modelo: " + elemento.getModeloElemento(), 40, y, paint); y += 30;
        canvas.drawText("Color: " + elemento.getColorElemento(), 40, y, paint); y += 30;
        canvas.drawText("Estado físico: " + elemento.getEstadoElemento(), 40, y, paint); y += 30;
        canvas.drawText("Estado en sistema: " + (elemento.getEstado() == 1 ? "Activo" : "Inactivo"), 40, y, paint); y += 50;

        Bitmap barcode = generarCodigoBarras(elemento.getUniCodeElemento());
        if (barcode != null) {
            canvas.drawBitmap(barcode, 40, y, null);
        }

        document.finishPage(page);

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Elemento_" + elemento.getIdElemento() + ".pdf");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            document.writeTo(fos);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        document.close();
        return file;
    }
}
