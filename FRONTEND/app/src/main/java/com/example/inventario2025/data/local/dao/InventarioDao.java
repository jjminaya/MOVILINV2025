package com.example.inventario2025.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.inventario2025.data.local.entities.Inventario;

import java.util.List;

@Dao
public interface InventarioDao {

    @Query("SELECT * FROM inventario ORDER BY descripcionInventario ASC")
    LiveData<List<Inventario>> getAllInventories();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Inventario inventario);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Inventario> inventarios);

    @Update
    void update(Inventario inventario);

    @Delete
    void delete(Inventario inventario);

    @Query("SELECT * FROM inventario WHERE idInventario = :inventarioId")
    LiveData<Inventario> getInventoryById(int inventarioId);

    @Query("DELETE FROM inventario WHERE idInventario = :inventarioId")
    void deleteById(int inventarioId);

    @Query("SELECT * FROM inventario WHERE ownerUserId = :userId ORDER BY idInventario DESC")
    LiveData<List<Inventario>> getInventariosByUserId(int userId);

    @Query("DELETE FROM inventario")
    void deleteAll();

    @Query("SELECT * FROM inventario WHERE idInventario = :id")
    Inventario getInventarioByIdSync(int id);
}