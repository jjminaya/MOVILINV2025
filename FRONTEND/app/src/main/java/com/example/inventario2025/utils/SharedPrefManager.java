package com.example.inventario2025.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.inventario2025.data.remote.models.Usuario;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "user_session";
    private static final String KEY_ID = "idUsuario";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TIPO = "tipoUsuario";
    private static final String KEY_ID_PERSONA = "idPersona";
    private static final String KEY_ESTADO = "estado";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // ✅ Guarda todos los datos del usuario
    public void guardarUsuario(Usuario usuario) {
        editor.putInt(KEY_ID, usuario.getIdUsuario());
        editor.putString(KEY_USERNAME, usuario.getUsername());
        editor.putString(KEY_PASSWORD, usuario.getPassword());
        editor.putString(KEY_TIPO, usuario.getTipoUsuario());
        editor.putInt(KEY_ID_PERSONA, usuario.getIdPersona());
        editor.putInt(KEY_ESTADO, usuario.getEstado());
        editor.apply();
    }

    // ✅ Devuelve el usuario completo almacenado
    public Usuario obtenerUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(sharedPreferences.getInt(KEY_ID, -1));
        usuario.setUsername(sharedPreferences.getString(KEY_USERNAME, null));
        usuario.setPassword(sharedPreferences.getString(KEY_PASSWORD, null));
        usuario.setTipoUsuario(sharedPreferences.getString(KEY_TIPO, null));
        usuario.setIdPersona(sharedPreferences.getInt(KEY_ID_PERSONA, 0));
        usuario.setEstado(sharedPreferences.getInt(KEY_ESTADO, 1));
        return usuario;
    }

    // ✅ Limpia todos los datos (cerrar sesión)
    public void cerrarSesion() {
        editor.clear();
        editor.apply();
    }

    // ✅ Verifica si hay un usuario guardado
    public boolean estaLogueado() {
        return sharedPreferences.contains(KEY_ID)
                && sharedPreferences.getInt(KEY_ID, -1) != -1
                && sharedPreferences.getString(KEY_USERNAME, null) != null;
    }
}
