package com.example.inventario2025.data.remote;

import com.example.inventario2025.data.remote.api.InventorioApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://200.234.238.128/api/";
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static InventorioApiService getInventoryApiService() {
        return getClient().create(InventorioApiService.class);
    }
}