package com.example.inventario2025.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.inventario2025.data.local.dao.ElementoDao;
import com.example.inventario2025.data.local.dao.InventarioDao;
import com.example.inventario2025.data.local.entities.Elemento;
import com.example.inventario2025.data.local.entities.Inventario;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Inventario.class, Elemento.class}, version = 6, exportSchema = false)
public abstract class InventarioBaseDatos extends RoomDatabase {

    public abstract InventarioDao inventoryDao();
    public abstract ElementoDao elementoDao();

    private static volatile InventarioBaseDatos INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static InventarioBaseDatos getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (InventarioBaseDatos.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    InventarioBaseDatos.class, "inventory_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}