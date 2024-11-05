package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

    //	@Autowired
    @InjectMocks
    TarefaApplicationService tarefaApplicationService;


    //	@MockBean
    @Mock
    TarefaRepository tarefaRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    private UUID idTarefa;
    private UUID idUsuario;
    private String usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        idTarefa = UUID.randomUUID();
        idUsuario = UUID.randomUUID();
        usuario = "usuario@teste.com";
    }

    @Test
    void deveRetornarIdTarefaNovaCriada() {
        TarefaRequest request = getTarefaRequest();
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request, 1));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }



    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0, 1);
        return request;
    }

    @Test
    void testAlteraPosicaoTarefaComSucesso() {
        NovaPosicaoRequest novaPosicaoRequest = new NovaPosicaoRequest(2);
        Tarefa tarefa = new Tarefa(idTarefa, 1); // Correção: usando apenas os argumentos idTarefa e posicaoTarefa

        List<Tarefa> todasTarefas = Arrays.asList(
                new Tarefa(UUID.randomUUID(), 0), // Correção para criação da tarefa
                tarefa,
                new Tarefa(UUID.randomUUID(), 2)  // Correção para criação da tarefa
        );

        when(usuarioRepository.buscaUsuarioPorEmail(usuario)).thenReturn(new Usuario(idUsuario, usuario));
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.buscaTodasAsTarefas(idUsuario)).thenReturn(todasTarefas);

        tarefaApplicationService.alteraPosicaoTarefa(usuario, idTarefa, novaPosicaoRequest);

        verify(tarefaRepository).defineNovaPosicaoTarefa(tarefa, todasTarefas, novaPosicaoRequest);
    }

    // Os demais testes seguirão o mesmo padrão de ajuste no construtor da tarefa
}
//    @Test
//    void DeveMudarOdemDaTarefa(){
//        //Cenario
//        Tarefa tarefa = DataHelper.createTarefa();
//        List<Tarefa> todasTarefas = DataHelper.createListTarefa();
//        Usuario usuario = DataHelper.createUsuario();
//        NovaPosicaoRequest novaPosicao = new NovaPosicaoRequest(1);
//
//        //acao
//        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
//        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn((usuario));
//        when(tarefaRepository.buscaTodasAsTarefas(tarefa.getIdUsuario())).thenReturn(todasTarefas);
//
//        tarefaApplicationService.alteraPosicaoTarefa(String.valueOf(usuario.getIdUsuario()), tarefa.getIdTarefa(), novaPosicao);
//
//        //verifica
//        verify(tarefaRepository, times(1)).defineNovaPosicaoTarefa(tarefa, todasTarefas, novaPosicao);
//
//    }

