package dev.wakandaacademy.produdoro.tarefa.infra;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.mongodb.core.query.Update;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TarefaInfraRepository implements TarefaRepository {
    private final TarefaSpringMongoDBRepository tarefaSpringMongoDBRepository;
	private final MongoTemplate mongoTemplate;

    @Override
    public Tarefa salva(Tarefa tarefa) {
        log.info("[inicia] TarefaInfraRepository - salva");
        try {
            tarefaSpringMongoDBRepository.save(tarefa);
        } catch (DataIntegrityViolationException e) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Tarefa já cadastrada", e);
        }
        log.info("[finaliza] TarefaInfraRepository - salva");
        return tarefa;
    }
    @Override
    public Optional<Tarefa> buscaTarefaPorId(UUID idTarefa) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefaPorId");
        Optional<Tarefa> tarefaPorId = tarefaSpringMongoDBRepository.findByIdTarefa(idTarefa);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefaPorId");
        return tarefaPorId;
    }


    @Override
    public List<Tarefa> buscaTodasAsTarefas(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscaTodasAsTarefas");
        List<Tarefa> todasTarefas = tarefaSpringMongoDBRepository.findAllByIdUsuarioOrderByPosicaoTarefaAsc(idUsuario);
        log.info("[finaliza] TarefaInfraRepository - buscaTodasAsTarefas");
        return todasTarefas;
    }

    @Override
    public void deletaTarefas(List<Tarefa> tarefas) {
        log.info("[inicia] TarefaInfraRepository - deletaTarefas");
        Optional.of(tarefas)
                .filter(t -> !t.isEmpty())
                .orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "Usuário não possui tarefa(as) cadastrada(as)"));
        tarefaSpringMongoDBRepository.deleteAll(tarefas);
        log.info("[finaliza] TarefaInfraRepository - deletaTarefas");
    }


    @Override
    public void defineNovaPosicaoTarefa(Tarefa tarefa, List<Tarefa> todasTarefas, NovaPosicaoRequest novaPosicaoRequest) {
        log.info("[inicia] TarefaInfraRepository - defineNovaPosicaoTarefa");
        validaNovaPosicao(tarefa, todasTarefas, novaPosicaoRequest);
        int posicaoAtualTarefa = tarefa.getPosicao();
        int novaPosicaoTarefa = novaPosicaoRequest.getNovaPosicao();
        if (novaPosicaoTarefa < posicaoAtualTarefa) {
            IntStream.range(novaPosicaoTarefa, posicaoAtualTarefa)
                    .forEach(i -> atualizaPosicaoTarefa(todasTarefas.get(i), i++));
        } else if (novaPosicaoTarefa > posicaoAtualTarefa) {
            IntStream.range(posicaoAtualTarefa + 1, novaPosicaoTarefa + 1)
                    .forEach(i -> atualizaPosicaoTarefa(todasTarefas.get(i), i--));
        }
        tarefa.alteraPosicaTarefa(novaPosicaoTarefa);
        atualizaPosicaoTarefa(tarefa, novaPosicaoTarefa);
        log.info("[finaliza] TarefaInfraRepository - defineNovaPosicaoTarefa");
    }

    @Override
    public List<Tarefa> buscaTarefasConcluidas(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefasConcluidas");
        Query query = new Query();
        query.addCriteria(Criteria.where("idUsuario").is(idUsuario).and("status").is(StatusTarefa.CONCLUIDA));
        List<Tarefa> tarefasConcluidas = mongoTemplate.find(query, Tarefa.class);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefasConcluidas");
        return tarefasConcluidas;
    }

    @Override
    public void deletaVariasTarefas(List<Tarefa> tarefasConcluidas) {
        log.info("[inicia] TarefaInfraRepository - deletaVariasTarefas");
        tarefaSpringMongoDBRepository.deleteAll(tarefasConcluidas);
        log.info("[finaliza] TarefaInfraRepository - deletaVariasTarefas");
    }

    @Override
    public List<Tarefa> buscaTarefasPorUsuario(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefaPorUsuario");
        List<Tarefa> listaTarefas = tarefaSpringMongoDBRepository.findAllByIdUsuario(idUsuario);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefaPorUsuario");
        return listaTarefas;
    }

    @Override
    public void atualizaPosicaoDaTarefa(List<Tarefa> tarefasDoUsuario) {
        log.info("[inicia] TarefaInfraRepository - atualizaPosicaoDaTarefa");
        int tamanhoDaLista = tarefasDoUsuario.size();
        List<Tarefa> tarefasAtualizadas = IntStream.range(0, tamanhoDaLista)
                .mapToObj(i -> atualizaTarefaComNovaPosicao(tarefasDoUsuario.get(i), i))
                        .collect(Collectors.toList());
        salvaVariasTarefas(tarefasAtualizadas);
        log.info("[finaliza] TarefaInfraRepository - atualizaPosicaoDaTarefa");

    }

    private void salvaVariasTarefas(List<Tarefa> tarefasDoUsuario) {
        tarefaSpringMongoDBRepository.saveAll(tarefasDoUsuario);
    }

    @Override
    public int contarTarefas(UUID idUsuario) {
        List<Tarefa> tarefas = buscaTarefasPorUsuario(idUsuario);
        int novaPosicao = tarefas.size();
        return novaPosicao;
    }

    private Tarefa atualizaTarefaComNovaPosicao(Tarefa tarefa, int novaPosicao) {
        tarefa.atualizaPosicao(novaPosicao);
        return tarefa;
    }

    @Override
    public void desativaTarefaAtiva(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - desativaTarefaAtiva");
        Query query = new Query(Criteria.where("statusAtivacao").is("ATIVA").and("idUsuario").is(idUsuario));
        Update update = new Update().set("statusAtivacao", "INATIVA");
        mongoTemplate.updateMulti(query, update, Tarefa.class);
        log.info("[finaliza] TarefaInfraRepository - desativaTarefaAtiva");
    }


    private void atualizaPosicaoTarefa(Tarefa tarefa, int novaPosicao) {
        Query query = new Query(Criteria.where("idTarefa").is(tarefa.getIdTarefa()));
        Update update = new Update().set("posicao", novaPosicao);
        mongoTemplate.updateFirst(query, update, Tarefa.class);
    }

    private void validaNovaPosicao(Tarefa tarefa, List<Tarefa> todasTarefas, NovaPosicaoRequest novaPosicao) {
        int posicaoAntiga = tarefa.getPosicao();
        int tamanhoListaTarefa = todasTarefas.size();
        if (novaPosicao.getNovaPosicao() >= tamanhoListaTarefa || novaPosicao.getNovaPosicao() <= posicaoAntiga){
            String mensagem = novaPosicao.getNovaPosicao() >= tamanhoListaTarefa
                    ? "Posição da tarefa não pode ser maior, nem igual a quantidade de tarefas do usuariio"
                    : "A posição enviada é igual a posição atual da tarefa, insira nova posição";
            throw APIException.build(HttpStatus.NOT_FOUND, mensagem);

        }
    }
}
