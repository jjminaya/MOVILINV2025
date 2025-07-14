package com.example.inventario2025.data.remote.api;

import com.example.inventario2025.data.remote.models.LoginRequest;
import com.example.inventario2025.data.remote.models.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {

    @POST("api/login")
    Call<Usuario> login(@Body LoginRequest request);  // Enviamos JSON
}
