package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TarefaRepository {
    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);

    int contagemPosicao(UUID idUsuario);

    List<Tarefa> buscaTodasAsTarefas(UUID idUsuario);

    void defineNovaPosicaoTarefa(Tarefa tarefa, List<Tarefa> todasTarefas, NovaPosicaoRequest novaPosicao);
    List<Tarefa> buscaTarefasConcluidas(UUID idUsuario);
    void deletaVariasTarefas(List<Tarefa> tarefasConcluidas);
    List<Tarefa> buscaTarefasPorUsuario(UUID idUsuario);
    void atualizaPosicaoDaTarefa(List<Tarefa> tarefasDoUsuario);
    int contarTarefas(UUID idUsuario);
	void desativaTarefaAtiva(UUID idUsuario);
}
