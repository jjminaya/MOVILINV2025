package com.example.inventario2025.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.inventario2025.data.local.entities.Elemento;

import java.util.List;

@Dao
public interface ElementoDao {

    @Query("SELECT * FROM elemento WHERE idInventario = :inventarioId ORDER BY descripcionElemento ASC")
    LiveData<List<Elemento>> getElementosByInventarioId(int inventarioId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Elemento elemento);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Elemento> elementos);

    @Update
    void update(Elemento elemento);

    @Delete
    void delete(Elemento elemento);

    @Query("SELECT * FROM elemento WHERE idElemento = :elementoId")
    LiveData<Elemento> getElementoById(int elementoId);

    @Query("DELETE FROM elemento WHERE idElemento = :elementoId")
    void deleteById(int elementoId);

    @Query("DELETE FROM elemento WHERE idInventario = :inventarioId")
    void deleteElementsByInventarioId(int inventarioId);
}