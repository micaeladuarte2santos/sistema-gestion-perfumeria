package com.perfumeria.services;

import com.perfumeria.models.Usuario;

public interface IUsuarioService {
    Usuario crearUsuario(Usuario usuario);
    void eliminarUsuario(String username);
    void verificarUsuario(String username, String codigo);
    void reenviarCodigoVerificacion(String username);
    boolean verificarCredenciales(String username, String password);
    boolean existeUsuario(String username);
    void actualizarPassword(String username, String nuevoPassword);
    void solicitarCodigoRecuperacion(String username);
    void actualizarPasswordConCodigo(String username, String codigo, String nuevoPassword);
}
