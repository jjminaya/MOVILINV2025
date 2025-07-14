package com.example.inventario2025.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventario2025.R;
import com.example.inventario2025.data.remote.api.ApiClient;
import com.example.inventario2025.data.remote.api.UsuarioService;
import com.example.inventario2025.data.remote.models.Usuario;
import com.example.inventario2025.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnGuardar;

    private SharedPrefManager sharedPrefManager;
    private Usuario usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        etUsername = findViewById(R.id.etUsernameEditar);
        etPassword = findViewById(R.id.etPasswordEditar);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);

        sharedPrefManager = new SharedPrefManager(this);
        usuarioActual = sharedPrefManager.obtenerUsuario();

        if (usuarioActual == null || usuarioActual.getIdUsuario() == 0) {
            Toast.makeText(this, "Error: Usuario no válido", Toast.LENGTH_LONG).show();
            finish(); // Cierra la actividad
            return;
        }

        etUsername.setText(usuarioActual.getUsername());

        btnGuardar.setOnClickListener(v -> {
            String nuevoUsername = etUsername.getText().toString().trim();
            String nuevaPassword = etPassword.getText().toString().trim();

            if (nuevoUsername.isEmpty()) {
                etUsername.setError("Campo obligatorio");
                return;
            }

            if (nuevaPassword.isEmpty()) {
                etPassword.setError("Campo obligatorio");
                return;
            }

            usuarioActual.setUsername(nuevoUsername);
            usuarioActual.setPassword(nuevaPassword);

            UsuarioService service = ApiClient.getUsuarioService();
            Call<Usuario> call = service.actualizarUsuario(usuarioActual.getIdUsuario(), usuarioActual);

            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.isSuccessful()) {
                        sharedPrefManager.guardarUsuario(response.body());

                        Toast.makeText(EditarPerfilActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();

                        // Devuelve resultado al fragmento anterior
                        setResult(RESULT_OK, new Intent());
                        finish();
                    } else if (response.code() == 409) {
                        Toast.makeText(EditarPerfilActivity.this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditarPerfilActivity.this, "Error al actualizar: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    Toast.makeText(EditarPerfilActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
