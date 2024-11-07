package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.wakandaacademy.produdoro.config.security.service.TokenService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.service.TarefaService;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
public class TarefaRestController implements TarefaAPI {
	private final TarefaService tarefaService;
	private final TokenService tokenService;

	public TarefaIdResponse postNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[inicia]  TarefaRestController - postNovaTarefa  ");
		TarefaIdResponse tarefaCriada = tarefaService.criaNovaTarefa(tarefaRequest);
		log.info("[finaliza]  TarefaRestController - postNovaTarefa");
		return tarefaCriada;
	}

	@Override
	public TarefaDetalhadoResponse detalhaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - detalhaTarefa");
		String usuario = getUsuarioByToken(token);
		Tarefa tarefa = tarefaService.detalhaTarefa(usuario,idTarefa);
		log.info("[finaliza] TarefaRestController - detalhaTarefa");
		return new TarefaDetalhadoResponse(tarefa);
	}

	@Override
	public void editaTarefa(String token, TarefaAlteracaoRequest tarefaAlteracaoRequest, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - editaTarefa");
		String email = getUsuarioByToken(token);
		tarefaService.editaTarefa(email, idTarefa, tarefaAlteracaoRequest);
		log.info("[finaliza] TarefaRestController - editaTarefa");
	}

	@Override
	public void deleteTarefas(String token, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - deleteTarefas");
		String usuario = getUsuarioByToken(token);
		tarefaService.deletarTarefas(usuario, idUsuario);
		log.info("[finaliza] TarefaRestController - deleteTarefas");
	}


	@Override
	public void incrementaTarefaProdudoro(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - incrementaTarefaProdudoro");
		String usuarioEmail = getUsuarioByToken(token);
		tarefaService.incrementaTarefaProdudoro(idTarefa, usuarioEmail);
		log.info("[idTarefa] {} ", idTarefa);
		log.info("[finaliza] TarefaRestController - incrementaTarefaProdudoro");
	}

	@Override
	public void deletaTarefasConcluidas(String token, UUID idUsuario) {
		log.info("[inicia]  TarefaRestController - deletaTarefasConcluidas");
		String email = getUsuarioByToken(token);
		tarefaService.deletaTarefasConcluidas(email, idUsuario);
		log.info("[finaliza]  TarefaRestController - deletaTarefasConcluidas");

	}
    @Override
    public void concluiTarefa(String token, UUID idTarefa) {
        log.info("[inicia] TarefaRestController - concluiTarefa");
        String usuario = getUsuarioByToken(token);
        tarefaService.concluiTarefa(usuario, idTarefa);
        log.info("[Finish] TarefaRestController - concluiTarefa");
    }

    @Override
    public void ativaTarefa(String token, UUID idTarefa) {
        log.info("[inicia] TarefaRestController - ativaTarefa");
        String email = getUsuarioByToken(token);
        tarefaService.ativaTarefa(email, idTarefa);
        log.info("[finaliza] TarefaRestController - ativaTarefa");
    }

	@Override
	public List<TarefaListResponse> buscaTarefasPorIdUsuario(String token, UUID idUsuario) {
		log.info("[inicia]  TarefaRestController - buscaTarefasPorIdUsuario ");
		String usuario = getUsuarioByToken(token);
		List<TarefaListResponse> listaTarefas = tarefaService.buscaTarefaPorUsuario(usuario, idUsuario);
		log.info("[finaliza]  TarefaRestController - buscaTarefasPorIdUsuario ");
		return listaTarefas;
	}

    private String getUsuarioByToken(String token) {
        log.debug("[token] {}", token);
        String usuario = tokenService.getUsuarioByBearerToken(token).orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
        log.info("[usuario] {}", usuario);
        return usuario;
    }
}
