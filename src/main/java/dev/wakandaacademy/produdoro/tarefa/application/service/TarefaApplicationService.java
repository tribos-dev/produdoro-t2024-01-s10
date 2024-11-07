package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaAlteracaoRequest;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {



    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;
    private Integer ciclos = 1;


    @Override
    public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
        log.info("[inicia] TarefaApplicationService - criaNovaTarefa");
        int novaPosicao = tarefaRepository.contarTarefas(tarefaRequest.getIdUsuario());
        Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest, novaPosicao));
        log.info("[finaliza] TarefaApplicationService - criaNovaTarefa");
        return TarefaIdResponse.builder().idTarefa(tarefaCriada.getIdTarefa()).build();
    }
    @Override
    public Tarefa detalhaTarefa(String usuario, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - detalhaTarefa");
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
        log.info("[usuarioPorEmail] {}", usuarioPorEmail);
        Tarefa tarefa =
                tarefaRepository.buscaTarefaPorId(idTarefa).orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
        tarefa.pertenceAoUsuario(usuarioPorEmail);
        log.info("[finaliza] TarefaApplicationService - detalhaTarefa");
        return tarefa;
    }
    @Override
    public void deletarTarefas(String usuario, UUID idUsuario) {
        log.info("[inicia] TarefaApplicationService - deletarTarefas");
        validaUsuario(usuario, idUsuario);
        List<Tarefa> tarefas = tarefaRepository.buscaTodasTarefasId(idUsuario);
        tarefaRepository.deletaTarefas(tarefas);
        log.info("[finaliza] TarefaApplicationService - deletarTarefas");
    }
    @Override
    public void alteraPosicaoTarefa(String usuario, UUID idTarefa, NovaPosicaoRequest novaPosicao) {
        log.info("[inicia] TarefaApplicationService - alteraPosicaoTarefa");
        Tarefa tarefa = detalhaTarefa(usuario, idTarefa);
        List<Tarefa> todasTarefas = tarefaRepository.buscaTodasAsTarefas(tarefa.getIdUsuario());
        tarefaRepository.defineNovaPosicaoTarefa(tarefa, todasTarefas, novaPosicao);
        log.info("[finaliza] TarefaApplicationService - alteraPosicaoTarefa");
    }

    @Override
    public void deletaTarefasConcluidas(String email, UUID idUsuario) {
        log.info("[inicia] TarefaApplicationService - deletaTarefasConcluidas");
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(email);
        Usuario usuario = usuarioRepository.buscaUsuarioPorId(idUsuario);
        usuario.validaUsuario(usuarioPorEmail.getIdUsuario());
        List<Tarefa> tarefasConcluidas = tarefaRepository.buscaTarefasConcluidas(usuario.getIdUsuario());
        if(tarefasConcluidas.isEmpty()){
            throw APIException.build(HttpStatus.NOT_FOUND, "usuário não possui nenhuma tarefa concluida");
        }
        tarefaRepository.deletaVariasTarefas(tarefasConcluidas);
        List<Tarefa> tarefasDoUsuario = tarefaRepository.buscaTarefasPorUsuario(usuario.getIdUsuario());
        tarefaRepository.atualizaPosicaoDaTarefa(tarefasDoUsuario);
        log.info("[finaliza] TarefaApplicationService - deletaTarefasConcluidas");
    }
    @Override
    public void ativaTarefa(String email, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - ativaTarefa");
        Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Id da tarefa invalido!"));
        Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(email);
        tarefa.pertenceAoUsuario(usuario);
        tarefa.verificaSeJaEstaAtiva();
        tarefaRepository.desativaTarefaAtiva(usuario.getIdUsuario());
        tarefa.ativaTarefa();
        tarefaRepository.salva(tarefa);
        log.info("[finaliza] TarefaApplicationService - ativaTarefa");
    }

    @Override
    public void concluiTarefa(String usuario, UUID idTarefa) {
        log.info("[inicia] TarefaRestController - concluiTarefa");
        Tarefa tarefa = detalhaTarefa(usuario, idTarefa);
        tarefa.concluiTarefa();
        tarefaRepository.salva(tarefa);
        log.info("[Finish] TarefaRestController - concluiTarefa");
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
            incrementarPomodoro(tarefaOpt, usuario);
        } else {
            atualizaStatusParaFocoSeNecessario(usuario);
        }
    }

    private void incrementarPomodoro(Optional<Tarefa> tarefaOpt, Usuario usuario) {
        tarefaOpt.ifPresent(tarefa -> {
            tarefa.incrementaPomodoro();
            gerenciarCiclosDePomodoro(usuario);
            tarefaRepository.salva(tarefa);
            log.info("Pomodoro incrementado para a tarefa com id: {}", tarefa.getIdTarefa());
        });
    }

    private void gerenciarCiclosDePomodoro(Usuario usuario) {

        if (this.ciclos < 4) {
            usuario.mudaStatusParaPausaCurta();
            this.ciclos++;
            log.info("Status alterado para Pausa Curta. Ciclo incrementado para {}", ciclos);
        } else {
            usuario.mudaStatusParaPausaLonga();
            this.ciclos = 1;
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

    @Override
    public void editaTarefa(String email, UUID idTarefa, TarefaAlteracaoRequest tarefaAlteracaoRequest) {
        log.info("[inicia] TarefaApplicationService - editaTarefa");
        Tarefa tarefa = detalhaTarefa(email, idTarefa);
        tarefa.editaTarefa(tarefaAlteracaoRequest);
        tarefaRepository.salva(tarefa);
        log.info("[finaliza] TarefaApplicationService - editaTarefa");
    }

    private void validaUsuario(String email, UUID idUsuario) {
        Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(email);
        usuarioRepository.buscaUsuarioPorId(idUsuario);
        usuario.validaUsuario(idUsuario);
    }

}
