package com.example.inventario2025.data.remote.models;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters obligatorios para que Retrofit funcione
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
