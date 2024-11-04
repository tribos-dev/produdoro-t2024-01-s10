package dev.wakandaacademy.produdoro.tarefa.application.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Value
public class NovaPosicaoRequest {
    @NotNull
    @PositiveOrZero
    private int posicaoTarefa;

}
