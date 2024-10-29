package dev.wakandaacademy.produdoro.tarefa.application.api;

import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import lombok.Value;

import java.util.UUID;

@Value
public class TarefaListResponse {
    private UUID idTarefa;
    private String descricao;
    private StatusTarefa status;
    private StatusAtivacaoTarefa statusAtivacao;
    private int contagemPomodoro;

}
