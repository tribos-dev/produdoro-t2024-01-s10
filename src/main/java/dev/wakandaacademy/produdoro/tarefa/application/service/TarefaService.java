package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaAlteracaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.UUID;
public interface TarefaService {
    TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);
    Tarefa detalhaTarefa(String usuario, UUID idTarefa);
    void editaTarefa(String email, UUID idTarefa, TarefaAlteracaoRequest tarefaAlteracaoRequest);
    void deletarTarefas(String usuario, UUID idUsuario);
    void incrementaTarefaProdudoro(UUID idTarefa, String usuarioEmail);
    void deletaTarefasConcluidas(String email, UUID idUsuario);
	void ativaTarefa(String email, UUID idTarefa);
    List<TarefaListResponse> buscaTarefaPorUsuario(String usuario, UUID idUsuario);
    void concluiTarefa(String usuario, UUID idTarefa);
}