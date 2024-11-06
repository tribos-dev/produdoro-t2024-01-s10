package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
    List<Tarefa> buscaTodasTarefasId(UUID idUsuario);
    void deletaTarefas(List<Tarefa> tarefas);
    List<Tarefa> buscaTarefasConcluidas(UUID idUsuario);
    void deletaVariasTarefas(List<Tarefa> tarefasConcluidas);
    List<Tarefa> buscaTarefasPorUsuario(UUID idUsuario);
    void atualizaPosicaoDaTarefa(List<Tarefa> tarefasDoUsuario);
    int contarTarefas(UUID idUsuario);
	void desativaTarefaAtiva(UUID idUsuario);
}
