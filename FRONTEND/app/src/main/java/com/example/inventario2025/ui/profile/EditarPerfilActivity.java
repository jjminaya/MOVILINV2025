package com.example.inventario2025.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventario2025.R;
import com.example.inventario2025.data.local.entities.Usuario;
import com.example.inventario2025.data.remote.api.ApiClient;
import com.example.inventario2025.utils.SharedPrefManager;
import com.example.inventario2025.ui.login.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Spinner spinnerTipoUsuario;
    private CheckBox checkActivo, checkInactivo;
    private Button btnGuardar, btnCancelar;

    private SharedPrefManager sharedPrefManager;
    private Usuario usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        etUsername = findViewById(R.id.etUsernameEditar);
        etPassword = findViewById(R.id.etPasswordEditar);
        spinnerTipoUsuario = findViewById(R.id.spinnerTipoUsuario);
        checkActivo = findViewById(R.id.checkActivo);
        checkInactivo = findViewById(R.id.checkInactivo);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);
        btnCancelar = findViewById(R.id.btnCancelar); // ⬅️ Asegúrate que esté este

        sharedPrefManager = new SharedPrefManager(this);
        usuarioActual = sharedPrefManager.obtenerUsuario();

        if (usuarioActual == null || usuarioActual.getIdUsuario() == -1) {
            Toast.makeText(this, "Error: Usuario no válido", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        etUsername.setText(usuarioActual.getUsername());

        if (usuarioActual.getEstado() == 1) {
            checkActivo.setChecked(true);
        } else {
            checkInactivo.setChecked(true);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_usuario, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoUsuario.setAdapter(adapter);

        if (usuarioActual.getTipoUsuario() != null) {
            int position = adapter.getPosition(usuarioActual.getTipoUsuario());
            spinnerTipoUsuario.setSelection(position);
        }

        checkActivo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) checkInactivo.setChecked(false);
        });

        checkInactivo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) checkActivo.setChecked(false);
        });

        btnGuardar.setOnClickListener(v -> guardarCambios());

        // ⬅️ Este botón cierra la actividad sin guardar
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void guardarCambios() {
        String nuevoUsername = etUsername.getText().toString().trim();
        String nuevaPassword = etPassword.getText().toString().trim();
        String tipoSeleccionado = spinnerTipoUsuario.getSelectedItem().toString();
        boolean activo = checkActivo.isChecked();
        boolean inactivo = checkInactivo.isChecked();

        if (nuevoUsername.isEmpty()) {
            Toast.makeText(this, "El nombre de usuario es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!activo && !inactivo) {
            Toast.makeText(this, "Selecciona el estado del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si no se cambia la contraseña, se conserva la actual
        String passwordFinal = nuevaPassword.isEmpty() ? usuarioActual.getPassword() : nuevaPassword;

        usuarioActual.setUsername(nuevoUsername);
        usuarioActual.setPassword(passwordFinal);
        usuarioActual.setTipoUsuario(tipoSeleccionado);
        usuarioActual.setEstado(activo ? 1 : 0);

        com.example.inventario2025.data.remote.models.Usuario usuarioRemoto =
                new com.example.inventario2025.data.remote.models.Usuario();

        usuarioRemoto.setIdUsuario(usuarioActual.getIdUsuario());
        usuarioRemoto.setUsername(usuarioActual.getUsername());
        usuarioRemoto.setPassword(usuarioActual.getPassword());
        usuarioRemoto.setTipoUsuario(usuarioActual.getTipoUsuario());
        usuarioRemoto.setIdPersona(usuarioActual.getIdPersona());
        usuarioRemoto.setEstado(usuarioActual.getEstado());

        Call<com.example.inventario2025.data.remote.models.Usuario> call =
                ApiClient.getUsuarioService().actualizarUsuario(
                        usuarioRemoto.getIdUsuario(), usuarioRemoto);

        call.enqueue(new Callback<com.example.inventario2025.data.remote.models.Usuario>() {
            @Override
            public void onResponse(Call<com.example.inventario2025.data.remote.models.Usuario> call,
                                   Response<com.example.inventario2025.data.remote.models.Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario nuevoUsuarioLocal = new Usuario();
                    nuevoUsuarioLocal.setIdUsuario(response.body().getIdUsuario());
                    nuevoUsuarioLocal.setUsername(response.body().getUsername());
                    nuevoUsuarioLocal.setPassword(response.body().getPassword());
                    nuevoUsuarioLocal.setTipoUsuario(response.body().getTipoUsuario());
                    nuevoUsuarioLocal.setIdPersona(response.body().getIdPersona());
                    nuevoUsuarioLocal.setEstado(response.body().getEstado());

                    sharedPrefManager.guardarUsuario(nuevoUsuarioLocal);

                    Toast.makeText(EditarPerfilActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditarPerfilActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(EditarPerfilActivity.this, "Error al actualizar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.inventario2025.data.remote.models.Usuario> call, Throwable t) {
                Toast.makeText(EditarPerfilActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
