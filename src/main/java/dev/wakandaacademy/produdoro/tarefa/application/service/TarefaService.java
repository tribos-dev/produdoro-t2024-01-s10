package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.UUID;
public interface TarefaService {
    TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);
    Tarefa detalhaTarefa(String usuario, UUID idTarefa);

    void alteraPosicaoTarefa(String usuario, UUID idTarefa, NovaPosicaoRequest novaPosicao);
    void deletaTarefasConcluidas(String email, UUID idUsuario);
	void ativaTarefa(String email, UUID idTarefa);
    void concluiTarefa(String usuario, UUID idTarefa);
}
