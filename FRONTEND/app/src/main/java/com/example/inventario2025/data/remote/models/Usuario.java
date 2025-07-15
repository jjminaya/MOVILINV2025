package com.example.inventario2025.data.remote.models;

public class Usuario {
        private int idUsuario;
        private String username;
        private String password;
        private String tipoUsuario;
        private int idPersona;
        private int estado;

        // Getters y setters
        public int getIdUsuario() {
                return idUsuario;
        }

        public void setIdUsuario(int idUsuario) {
                this.idUsuario = idUsuario;
        }

        public String getUsername() {
                return username;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public String getTipoUsuario() {
                return tipoUsuario;
        }

        public void setTipoUsuario(String tipoUsuario) {
                this.tipoUsuario = tipoUsuario;
        }

        public int getIdPersona() {
                return idPersona;
        }

        public void setIdPersona(int idPersona) {
                this.idPersona = idPersona;
        }

        public int getEstado() {
                return estado;
        }

        public void setEstado(int estado) {
                this.estado = estado;
        }
}
