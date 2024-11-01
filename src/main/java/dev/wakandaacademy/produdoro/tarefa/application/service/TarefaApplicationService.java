package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;


    @Override
    public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
        log.info("[inicia] TarefaApplicationService - criaNovaTarefa");
        Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest));
        log.info("[finaliza] TarefaApplicationService - criaNovaTarefa");
        return TarefaIdResponse.builder().idTarefa(tarefaCriada.getIdTarefa()).build();
    }
    @Override
    public Tarefa detalhaTarefa(String usuario, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - detalhaTarefa");
        Usuario usuarioPorEmail = buscaUsuario(usuario);
        log.info("[usuarioPorEmail] {}", usuarioPorEmail);
        Tarefa tarefa =
                tarefaRepository.buscaTarefaPorId(idTarefa).orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
        tarefa.pertenceAoUsuario(usuarioPorEmail);
        log.info("[finaliza] TarefaApplicationService - detalhaTarefa");
        return tarefa;
    }

    private Usuario buscaUsuario(String usuario) {
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
        return usuarioPorEmail;
    }

    @Override
    public void incrementaTarefaProdudoro(UUID idTarefa, String usuarioEmail) {
        log.info("[inicia] TarefaApplicationService - incrementaTarefaProdudoro");

        Optional<Usuario> usuarioOpt = Optional.ofNullable(buscaUsuario(usuarioEmail));
        Optional<Tarefa> tarefaOpt = Optional.ofNullable(detalhaTarefa(usuarioEmail, idTarefa));

        usuarioOpt.ifPresentOrElse(
                usuario -> processaIncrementoPomodoro(tarefaOpt, usuario),
                () -> log.warn("Usuário com email {} não encontrado", usuarioEmail)
        );

        log.info("[finaliza] TarefaApplicationService - incrementaTarefaProdudoro");
    }

    private void processaIncrementoPomodoro(Optional<Tarefa> tarefaOpt, Usuario usuario) {
        if (usuario.getStatus() == StatusUsuario.FOCO) {
            incrementarPomodoro(tarefaOpt);
        } else {
            gerenciarCiclosDePomodoro(usuario);
        }
        atualizaStatusParaFocoSeNecessario(usuario);
    }

    private void incrementarPomodoro(Optional<Tarefa> tarefaOpt) {
        tarefaOpt.ifPresent(tarefa -> {
            tarefa.incrementaPomodoro();
            tarefaRepository.salva(tarefa);
            log.info("Pomodoro incrementado para a tarefa com id: {}", tarefa.getIdTarefa());
        });
    }

    private void gerenciarCiclosDePomodoro(Usuario usuario) {
        Integer ciclos = 1;  // Este valor poderia vir de um banco de dados ou de um contexto externo
        if (ciclos < 4) {
            usuario.mudaStatusParaPausaCurta();
            ciclos++;
            log.info("Status alterado para Pausa Curta. Ciclo incrementado para {}", ciclos);
        } else {
            usuario.mudaStatusParaPausaLonga();
            ciclos = 1;
            log.info("Status alterado para Pausa Longa. Ciclo resetado.");
        }
        usuarioRepository.salva(usuario);
    }

    private void atualizaStatusParaFocoSeNecessario(Usuario usuario) {
        if (usuario.getStatus() != StatusUsuario.FOCO) {
            usuario.mudaStatusParaFoco();
            usuarioRepository.salva(usuario);
            log.info("Status do usuário atualizado para FOCO.");
        }
    }
}
