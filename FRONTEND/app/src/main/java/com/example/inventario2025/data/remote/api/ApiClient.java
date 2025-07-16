package com.example.inventario2025.data.remote.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // ✅ Producción (servidor web)
    private static final String BASE_URL = "http://200.234.238.128/api/";
    // Local solo si pruebas localmente


    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static LoginService getLoginService() {
        return getClient().create(LoginService.class);
    }

    public static UsuarioService getUsuarioService() {
        return getClient().create(UsuarioService.class);
    }

    // Puedes seguir agregando más servicios aquí
}
