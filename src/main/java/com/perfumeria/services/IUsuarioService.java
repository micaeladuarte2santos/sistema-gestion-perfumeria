package com.perfumeria.services;

import com.perfumeria.models.Usuario;

public interface IUsuarioService {
    Usuario crearUsuario(Usuario usuario);
    void eliminarUsuario(String username);
    void verificarUsuario(String username, String codigo);
    void reenviarCodigoVerificacion(String username);
}
