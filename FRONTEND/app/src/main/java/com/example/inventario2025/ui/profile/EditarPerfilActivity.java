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
import com.example.inventario2025.data.local.entities.Usuario;
import com.example.inventario2025.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnGuardar;

    private SharedPrefManager sharedPrefManager;
    private Usuario usuarioActualLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        etUsername = findViewById(R.id.etUsernameEditar);
        etPassword = findViewById(R.id.etPasswordEditar);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);

        sharedPrefManager = new SharedPrefManager(this);
        usuarioActualLocal = sharedPrefManager.obtenerUsuario();

        if (usuarioActualLocal == null || usuarioActualLocal.getIdUsuario() == -1) { // Comprobar con -1
            Toast.makeText(this, "Error: Usuario no válido", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        etUsername.setText(usuarioActualLocal.getUsername());

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

            // Convertimos el objeto local a un objeto remoto para enviarlo a la API
            com.example.inventario2025.data.remote.models.Usuario usuarioParaEnviar = new com.example.inventario2025.data.remote.models.Usuario();
            usuarioParaEnviar.setIdUsuario(usuarioActualLocal.getIdUsuario());
            usuarioParaEnviar.setUsername(nuevoUsername);
            usuarioParaEnviar.setPassword(nuevaPassword);
            // Copia otros campos si la API los necesita para actualizar
            usuarioParaEnviar.setTipoUsuario(usuarioActualLocal.getTipoUsuario());
            usuarioParaEnviar.setIdPersona(usuarioActualLocal.getIdPersona());
            usuarioParaEnviar.setEstado(usuarioActualLocal.getEstado());

            UsuarioService service = ApiClient.getUsuarioService();
            Call<com.example.inventario2025.data.remote.models.Usuario> call = service.actualizarUsuario(usuarioActualLocal.getIdUsuario(), usuarioParaEnviar);

            call.enqueue(new Callback<com.example.inventario2025.data.remote.models.Usuario>() {
                @Override
                public void onResponse(Call<com.example.inventario2025.data.remote.models.Usuario> call, Response<com.example.inventario2025.data.remote.models.Usuario> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        com.example.inventario2025.data.remote.models.Usuario usuarioRemotoActualizado = response.body();

                        // Convertimos la respuesta remota a un objeto local antes de guardar
                        Usuario usuarioLocalParaGuardar = new Usuario();
                        usuarioLocalParaGuardar.setIdUsuario(usuarioRemotoActualizado.getIdUsuario());
                        usuarioLocalParaGuardar.setUsername(usuarioRemotoActualizado.getUsername());
                        usuarioLocalParaGuardar.setPassword(usuarioRemotoActualizado.getPassword());
                        usuarioLocalParaGuardar.setTipoUsuario(usuarioRemotoActualizado.getTipoUsuario());
                        usuarioLocalParaGuardar.setIdPersona(usuarioRemotoActualizado.getIdPersona());
                        usuarioLocalParaGuardar.setEstado(usuarioRemotoActualizado.getEstado());

                        // Guardamos el objeto del tipo correcto (local)
                        sharedPrefManager.guardarUsuario(usuarioLocalParaGuardar);

                        Toast.makeText(EditarPerfilActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();

                        setResult(RESULT_OK, new Intent());
                        finish();
                    } else if (response.code() == 409) {
                        Toast.makeText(EditarPerfilActivity.this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditarPerfilActivity.this, "Error al actualizar: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<com.example.inventario2025.data.remote.models.Usuario> call, Throwable t) {
                    Toast.makeText(EditarPerfilActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
