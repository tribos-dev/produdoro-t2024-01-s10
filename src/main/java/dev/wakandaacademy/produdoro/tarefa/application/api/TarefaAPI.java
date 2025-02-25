package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v1/tarefa")
public interface TarefaAPI {
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    TarefaIdResponse postNovaTarefa(@RequestBody @Valid TarefaRequest tarefaRequest);

    @GetMapping("/{idTarefa}")
    @ResponseStatus(code = HttpStatus.OK)
    TarefaDetalhadoResponse detalhaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
                                          @PathVariable UUID idTarefa);

    @GetMapping("tarefasUsuario/{idUsuario}")
    @ResponseStatus(code = HttpStatus.OK)
    List<TarefaListResponse> buscaTarefasPorIdUsuario(@RequestHeader(name = "Authorization",required = true) String token,
                                                      @PathVariable UUID idUsuario);

    @PatchMapping("/edita-tarefa")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void editaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
                     @RequestBody @Valid TarefaAlteracaoRequest tarefaAlteracaoRequest,
                    @RequestParam(name = "id") UUID idTarefa);


    @DeleteMapping("/limpar-tarefas/{idUsuario}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void deleteTarefas(@RequestHeader(name = "Authorization",required = true) String token,
                                          @PathVariable UUID idUsuario);

    @PostMapping("/incrementa-tarefa/{idTarefa}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void incrementaTarefaProdudoro(@RequestHeader(name = "Authorization",required = true) String token,
                                          @PathVariable UUID idTarefa);

    @PatchMapping(value = "/{idTarefa}/{novaPosicao}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void alteraPosicaoTarefa(@RequestHeader(name = "Authorization", required = true) String token,
                             @PathVariable UUID idTarefa, @Valid @RequestBody NovaPosicaoRequest novaPosicao);


    @PatchMapping("ativa-tarefa/{idTarefa}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void ativaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
            @PathVariable UUID idTarefa);

    @DeleteMapping("/{idUsuario}/deleta-tarefas-concluidas")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void deletaTarefasConcluidas(@RequestHeader(name = "Authorization", required = true) String token,
                                 @PathVariable UUID idUsuario);


    @PatchMapping("/conclui-tarefa")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void concluiTarefa(@RequestHeader(name = "Authorization", required = true) String token,
                       @RequestParam(name = "id") UUID idTarefa);
}