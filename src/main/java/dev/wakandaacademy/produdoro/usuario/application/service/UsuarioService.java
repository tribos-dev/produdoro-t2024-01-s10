package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioCriadoResponse;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioNovoRequest;

import java.util.UUID;

public interface UsuarioService {
	UsuarioCriadoResponse criaNovoUsuario(UsuarioNovoRequest usuarioNovo);
    UsuarioCriadoResponse buscaUsuarioPorId(UUID idUsuario);
    void mudaStatusParaPausaCurta(String email, UUID idUsuario);
    void mudaStatusParaPausaLonga(String usuario, UUID idUsuario);
    void mudaStatusParaFoco(String usuario, UUID idUsuario);
}
