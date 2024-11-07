package dev.wakandaacademy.produdoro.usuario.domain;

import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.Email;

import dev.wakandaacademy.produdoro.handler.APIException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import dev.wakandaacademy.produdoro.pomodoro.domain.ConfiguracaoPadrao;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioNovoRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@Log4j2
@Document(collection = "Usuario")
public class Usuario {
	@Id
	private UUID idUsuario;
	@Email
	@Indexed(unique = true)
	private String email;
	private ConfiguracaoUsuario configuracao;
	@Builder.Default
	private StatusUsuario status = StatusUsuario.FOCO;
	@Builder.Default
	private Integer quantidadePomodorosPausaCurta = 0;
	
	public Usuario(UsuarioNovoRequest usuarioNovo, ConfiguracaoPadrao configuracaoPadrao) {
		this.idUsuario = UUID.randomUUID();
		this.email = usuarioNovo.getEmail();
		this.status = StatusUsuario.FOCO;
		this.configuracao = new ConfiguracaoUsuario(configuracaoPadrao);
	}

	public void pertenceAoUsuario(Usuario usuarioPorEmail) {
		if(!this.idUsuario.equals(usuarioPorEmail.getIdUsuario())){
			throw APIException.build(HttpStatus.UNAUTHORIZED, "Usuário(a) não autorizado(a) para a requisição solicitada!");
		}
	}


	public void mudaStatusParaPausaCurta() {
		this.status = StatusUsuario.PAUSA_CURTA;
	}

	public void mudaStatusParaFoco() {
		this.status = StatusUsuario.FOCO;
	}
    public void validaUsuario(UUID idUsuario) {
        Optional.of(this.idUsuario)
                .filter(id -> id.equals(idUsuario))
                .orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, "Usuário(a) não autorizado(a) para a requisição solicitada"));

    }
	public void mudaStatusParaPausaLonga() {
		validaSeEstaEmPausaLonga();
		this.status = StatusUsuario.PAUSA_LONGA;
	}
	private void validaSeEstaEmPausaLonga() {
		if (this.status.equals(StatusUsuario.PAUSA_LONGA)) {
			throw APIException.build(HttpStatus.BAD_REQUEST, "Usuario ja esta em pausa longa");
		}
	}



	public void alteraStatusParaFoco(UUID idUsuario) {
		validaUsuario(idUsuario);
		verificaStatusFoco();
	}

	public void verificaStatusFoco() {
		if (this.status.equals(StatusUsuario.FOCO)) {
			throw APIException.build(HttpStatus.BAD_REQUEST, "Usuário já está em foco!");
		}
		mudaStatusParaFoco();
	}

    public void mudaStatusParaPausaCurta(UUID idUsuario) {
		pertenceAoUsuario(idUsuario);
		verificaSeJaEstaEmPausaCurta();
		mudaStatusPausaCurta();
    }

	private void mudaStatusPausaCurta() {
		this.status = StatusUsuario.PAUSA_CURTA;
	}

	private void verificaSeJaEstaEmPausaCurta() {
		if (this.status.equals(StatusUsuario.PAUSA_CURTA)) {
			throw APIException.build(HttpStatus.BAD_REQUEST, "Usuário já está em Pausa Curta.");
		}
	}

	private void pertenceAoUsuario(UUID idUsuario) {
		if (!this.idUsuario.equals(idUsuario)) {
			throw APIException.build(HttpStatus.UNAUTHORIZED, "Credencial de autenticação não é válida.");
		}
	}
}