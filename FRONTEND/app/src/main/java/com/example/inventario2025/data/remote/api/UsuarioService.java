package com.example.inventario2025.data.remote.api;

import com.example.inventario2025.data.remote.models.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioService {

    // ✅ Actualiza los datos de un usuario por su ID
    // Endpoint: PUT http://200.234.238.128/api/usuarios/{id}
    @PUT("api/usuarios/{id}")
    Call<Usuario> actualizarUsuario(
            @Path("id") int id,
            @Body Usuario usuario
    );
}
