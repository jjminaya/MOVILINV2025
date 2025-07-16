package com.example.inventario2025.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inventario2025.R;
import com.example.inventario2025.data.remote.api.ApiClient;
import com.example.inventario2025.data.remote.api.LoginService;
import com.example.inventario2025.data.remote.models.LoginRequest;
import com.example.inventario2025.data.remote.models.Usuario;
import com.example.inventario2025.ui.MainActivity;
import com.example.inventario2025.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPrefManager prefManager = new SharedPrefManager(this);
        if (prefManager.estaLogueado()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> loginConBackend());
    }

    private void loginConBackend() {
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginService service = ApiClient.getLoginService();
        LoginRequest request = new LoginRequest(user, pass);  // ✅ Usamos JSON como cuerpo

        Call<Usuario> call = service.login(request);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<com.example.inventario2025.data.remote.models.Usuario> call, Response<com.example.inventario2025.data.remote.models.Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Recibimos el usuario de la API (tipo remote.models.Usuario)
                    com.example.inventario2025.data.remote.models.Usuario usuarioRemoto = response.body();

                    Log.d("LOGIN_DEBUG", "ID: " + usuarioRemoto.getIdUsuario());
                    Log.d("LOGIN_DEBUG", "Username: " + usuarioRemoto.getUsername());
                    Log.d("LOGIN_DEBUG", "TipoUsuario: " + usuarioRemoto.getTipoUsuario());

                    // Creamos un objeto del tipo que espera SharedPrefManager (local.entities.Usuario)
                    com.example.inventario2025.data.local.entities.Usuario usuarioLocal = new com.example.inventario2025.data.local.entities.Usuario();

                    // Copiamos los datos del objeto remoto al objeto local
                    usuarioLocal.setIdUsuario(usuarioRemoto.getIdUsuario());
                    usuarioLocal.setUsername(usuarioRemoto.getUsername());
                    usuarioLocal.setTipoUsuario(usuarioRemoto.getTipoUsuario());
                    usuarioLocal.setIdPersona(usuarioRemoto.getIdPersona());
                    usuarioLocal.setEstado(usuarioRemoto.getEstado());

                    // Guardamos el objeto local, que es el tipo correcto
                    SharedPrefManager prefManager = new SharedPrefManager(LoginActivity.this);
                    prefManager.guardarUsuario(usuarioLocal);

                    Toast.makeText(LoginActivity.this, "Bienvenido " + usuarioRemoto.getUsername(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    Log.d("LOGIN_DEBUG", "Código de error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LOGIN_DEBUG", "Error en red o servidor", t);
            }
        });
    }
}
