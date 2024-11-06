package dev.wakandaacademy.produdoro.tarefa.application.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

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