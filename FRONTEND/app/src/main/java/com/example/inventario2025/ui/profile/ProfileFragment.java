package com.example.inventario2025.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.inventario2025.R;
import com.example.inventario2025.ui.login.LoginActivity;
import com.example.inventario2025.utils.SharedPrefManager;

public class ProfileFragment extends Fragment {

    SharedPrefManager sharedPrefManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPrefManager = new SharedPrefManager(requireContext());

        TextView tvUsername = root.findViewById(R.id.tvUsername);
        TextView tvTipoUsuario = root.findViewById(R.id.tvTipoUsuario);
        Button btnEditarPerfil = root.findViewById(R.id.btnEditarPerfil);
        Button btnCerrarSesion = root.findViewById(R.id.btnCerrarSesion);

        // Mostrar datos
        tvUsername.setText("Usuario: " + sharedPrefManager.obtenerUsuario().getUsername());
        tvTipoUsuario.setText("Tipo: " + sharedPrefManager.obtenerUsuario().getTipoUsuario());

        // Editar perfil
        btnEditarPerfil.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditarPerfilActivity.class));
        });

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            sharedPrefManager.cerrarSesion();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });

        return root;
    }
}


