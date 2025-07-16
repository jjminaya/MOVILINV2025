package com.example.inventario2025.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Usuario;
import com.example.inventario2025.data.remote.api.ApiClient;
import com.example.inventario2025.data.remote.api.LoginService;
import com.example.inventario2025.data.remote.models.LoginRequest;
import com.example.inventario2025.utils.SharedPrefManager;
import com.example.inventario2025.ui.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefManager = new SharedPrefManager(this);
        if (sharedPrefManager.estaLogueado()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

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
        LoginRequest request = new LoginRequest(user, pass);

        Call<com.example.inventario2025.data.remote.models.Usuario> call = service.login(request);

        call.enqueue(new Callback<com.example.inventario2025.data.remote.models.Usuario>() {
            @Override
            public void onResponse(Call<com.example.inventario2025.data.remote.models.Usuario> call, Response<com.example.inventario2025.data.remote.models.Usuario> response) {

                if (response.isSuccessful() && response.body() != null) {
                    com.example.inventario2025.data.remote.models.Usuario usuarioRemoto = response.body();

                    // Validar estado del usuario
                    if (usuarioRemoto.getEstado() == 0) {
                        Toast.makeText(LoginActivity.this, "Usuario desactivado temporalmente", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Usuario usuarioLocal = new Usuario();
                    usuarioLocal.setIdUsuario(usuarioRemoto.getIdUsuario());
                    usuarioLocal.setUsername(usuarioRemoto.getUsername());
                    usuarioLocal.setPassword(usuarioRemoto.getPassword());
                    usuarioLocal.setTipoUsuario(usuarioRemoto.getTipoUsuario());
                    usuarioLocal.setIdPersona(usuarioRemoto.getIdPersona());
                    usuarioLocal.setEstado(usuarioRemoto.getEstado());

                    sharedPrefManager.guardarUsuario(usuarioLocal);

                    Toast.makeText(LoginActivity.this, "Bienvenido " + usuarioLocal.getUsername(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    // ✅ Detectar errores específicos
                    if (response.code() == 403) {
                        Toast.makeText(LoginActivity.this, "Usuario desactivado temporalmente", Toast.LENGTH_LONG).show();
                    } else if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error inesperado: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<com.example.inventario2025.data.remote.models.Usuario> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
