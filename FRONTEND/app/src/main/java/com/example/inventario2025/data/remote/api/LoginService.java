package com.example.inventario2025.data.remote.api;

import com.example.inventario2025.data.remote.models.Usuario;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginService {
    @POST("api/login")
    @FormUrlEncoded
    Call<Usuario> login(
            @Field("username") String username,
            @Field("password") String password
    );
}
