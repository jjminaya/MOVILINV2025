package com.example.inventario2025.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileShareUtils {

    public static void shareImage(Context context, Bitmap bitmap, String fileName) {
        try {
            // 1. Guardar el bitmap en el directorio de caché
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream stream = new FileOutputStream(cachePath + "/" + fileName + ".png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            // 2. Obtener la URI del archivo usando FileProvider
            File imagePath = new File(context.getCacheDir(), "images");
            File newFile = new File(imagePath, fileName + ".png");
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", newFile);

            if (contentUri != null) {
                // 3. Crear el Intent para compartir
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                context.startActivity(Intent.createChooser(shareIntent, "Compartir etiqueta vía..."));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}