package dev.wakandaacademy.produdoro.usuario.application.api;

import javax.validation.Valid;

import dev.wakandaacademy.produdoro.config.security.service.TokenService;
import dev.wakandaacademy.produdoro.handler.APIException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import dev.wakandaacademy.produdoro.usuario.application.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@RestController
@Validated
@Log4j2
@RequiredArgsConstructor
public class UsuarioController implements UsuarioAPI {
	private final UsuarioService usuarioAppplicationService;
	private final TokenService tokenService;

	@Override
	public UsuarioCriadoResponse postNovoUsuario(@Valid UsuarioNovoRequest usuarioNovo) {
		log.info("[inicia] UsuarioController - postNovoUsuario");
		UsuarioCriadoResponse usuarioCriado = usuarioAppplicationService.criaNovoUsuario(usuarioNovo);
		log.info("[finaliza] UsuarioController - postNovoUsuario");
		return usuarioCriado;
	}
	@Override
	public UsuarioCriadoResponse buscaUsuarioPorId(UUID idUsuario) {
		log.info("[inicia] UsuarioController - buscaUsuarioPorId");
		log.info("[idUsuario] {}", idUsuario);
		UsuarioCriadoResponse buscaUsuario = usuarioAppplicationService.buscaUsuarioPorId(idUsuario);
		log.info("[finaliza] UsuarioController - buscaUsuarioPorId");
		return buscaUsuario;
	}

	@Override
	public void mudaStatusParaPausaCurta(String token, UUID idUsuario) {
		log.info("[inicia] UsuarioController - mudaStatusParaPausaCurta");
		String email = getUsuarioByToken(token);
		usuarioAppplicationService.mudaStatusParaPausaCurta(email, idUsuario);
		log.info("[finaliza] UsuarioController - mudaStatusParaPausaCurta");
	}

	private String getUsuarioByToken(String token) {
		log.debug("[token] {}", token);
		String usuario = tokenService.getUsuarioByBearerToken(token)
				.orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
		log.info("[usuario] {}", usuario);
		return usuario;
	}

	@Override
	public void mudaStatusPausaLonga(String token, UUID idUsuario) {
		log.info("[inicia] UsuarioController - mudaStatusPausaLonga");
		String usuarioT = validaTokenUsuario(token);
		usuarioAppplicationService.mudaStatusParaPausaLonga(usuarioT, idUsuario);
		log.info("[finaliza] UsuarioController - mudaStatusPausaLonga");

	}

	@Override
	public void mudaStatusParaFoco(String token, UUID idUsuario) {
		log.info("[inicia] UsuarioController - alteraStatusParaFoco");
		String usuario = validaTokenUsuario(token);
		usuarioAppplicationService.mudaStatusParaFoco(usuario, idUsuario);
		log.info("[finaliza] UsuarioController - alteraStatusParaFoco");
	}

	private String validaTokenUsuario(String token) {
		return tokenService.getUsuarioByBearerToken(token)
				.orElseThrow(()-> APIException.build(HttpStatus.UNAUTHORIZED, "Credencial de autenticação não é válida"));
			}
}
