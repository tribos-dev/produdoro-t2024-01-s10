//package dev.wakandaacademy.produdoro.tarefa.application.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//import java.util.*;
//
//import dev.wakandaacademy.produdoro.DataHelper;
//import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
//import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
//import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
//import org.junit.jupiter.api.BeforeEach;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import java.util.Optional;
//import java.util.UUID;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import org.springframework.http.HttpStatus;
//import dev.wakandaacademy.produdoro.DataHelper;
//import dev.wakandaacademy.produdoro.handler.APIException;
//import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
//import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
//import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
//import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
//import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
//import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
//import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
//import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
//
//@ExtendWith(MockitoExtension.class)
//class TarefaApplicationServiceTest {
//
//    // @Autowired
//    @InjectMocks
//    TarefaApplicationService tarefaApplicationService;
//
//
//    //	@MockBean
//    // @MockBean
//    @Mock
//    TarefaRepository tarefaRepository;
//    @Mock
//    UsuarioRepository usuarioRepository;
//
//    @Mock
//    UsuarioRepository usuarioRepository;
//
//    private UUID idTarefa;
//    private UUID idUsuario;
//    private String usuario;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        idTarefa = UUID.randomUUID();
//        idUsuario = UUID.randomUUID();
//        usuario = "usuario@teste.com";
//    }
//
//    @Test
//    void deveRetornarIdTarefaNovaCriada() {
//        TarefaRequest request = getTarefaRequest();
//        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request, 1));
//        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request,0));
//
//        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);
//
//        assertNotNull(response);
//        assertEquals(TarefaIdResponse.class, response.getClass());
//        assertEquals(UUID.class, response.getIdTarefa().getClass());
//    }
//
//    @Test
//    void deveRetornarTarefaConcluida() {
//        Usuario usuario = DataHelper.createUsuario();
//        UUID idTarefa = UUID.randomUUID();
//        Tarefa tarefa = Tarefa.builder()
//                .idTarefa(UUID.randomUUID())
//                .status(StatusTarefa.A_FAZER)
//                .idUsuario(usuario.getIdUsuario())
//                .build();
//        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
//        when(tarefaRepository.buscaTarefaPorId(tarefa.getIdTarefa())).thenReturn(Optional.of(tarefa));
//        when(tarefaRepository.salva(tarefa)).thenReturn(tarefa);
//        tarefaApplicationService.concluiTarefa(usuario.getEmail(), tarefa.getIdTarefa());
//        assertEquals(StatusTarefa.CONCLUIDA, tarefa.getStatus());
//    }
//
//    @Test
//    void deveDeletarTarefasConcluidas(){
//        Usuario usuario = DataHelper.createUsuario();
//        List<Tarefa> tarefasConcluidas = DataHelper.creatTarefasConcluidas();
//        List<Tarefa> tarefas = DataHelper.createListTarefa();
//        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
//        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
//        when(tarefaRepository.buscaTarefasConcluidas(any())).thenReturn(tarefasConcluidas);
//        when(tarefaRepository.buscaTarefasPorUsuario(any())).thenReturn(tarefas);
//        tarefaApplicationService.deletaTarefasConcluidas(usuario.getEmail(), usuario.getIdUsuario());
//        verify(tarefaRepository, times(1)).deletaVariasTarefas(tarefasConcluidas);
//        verify(tarefaRepository, times(1)).atualizaPosicaoDaTarefa(tarefas);
//    }
//
//    public TarefaRequest getTarefaRequest() {
//        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0, 1);
//        return request;
//    }
//
//    @Test
//    void testAlteraPosicaoTarefaComSucesso() {
//        NovaPosicaoRequest novaPosicaoRequest = new NovaPosicaoRequest(2);
//        Tarefa tarefa = new Tarefa(idTarefa, 1); // Correção: usando apenas os argumentos idTarefa e posicaoTarefa
//
//        List<Tarefa> todasTarefas = Arrays.asList(
//                new Tarefa(UUID.randomUUID(), 0), // Correção para criação da tarefa
//                tarefa,
//                new Tarefa(UUID.randomUUID(), 2)  // Correção para criação da tarefa
//        );
//
//        when(usuarioRepository.buscaUsuarioPorEmail(usuario)).thenReturn(new Usuario(idUsuario, usuario));
//        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));
//        when(tarefaRepository.buscaTodasAsTarefas(idUsuario)).thenReturn(todasTarefas);
//
//        tarefaApplicationService.alteraPosicaoTarefa(usuario, idTarefa, novaPosicaoRequest);
//
//        verify(tarefaRepository).defineNovaPosicaoTarefa(tarefa, todasTarefas, novaPosicaoRequest);
//    }
//
//    @Test
//    @DisplayName("Ativa tarefa - deve ativar tarefa")
//    void ativaTarefaDeveAtivarTarefa() {
//        UUID idTarefa = DataHelper.createTarefa().getIdTarefa();
//        UUID idUsuario = DataHelper.createUsuario().getIdUsuario();
//        Tarefa tarefa = DataHelper.createTarefa();
//        Usuario usuario = DataHelper.createUsuario();
//        String email = "email@gmail.com";
//        when(usuarioRepository.buscaUsuarioPorEmail(email)).thenReturn(usuario);
//        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));
//        tarefaApplicationService.ativaTarefa(email, idTarefa);
//        verify(tarefaRepository, times(1)).buscaTarefaPorId(idTarefa);
//        verify(tarefaRepository, times(1)).desativaTarefaAtiva(idUsuario);
//        assertEquals(StatusAtivacaoTarefa.ATIVA, tarefa.getStatusAtivacao());
//    }
//
//    // Os demais testes seguirão o mesmo padrão de ajuste no construtor da tarefa
//}
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
//
//
//    @Test
//    @DisplayName("Ativa tarefa com id invalido - deve retornar exception")
//    void ativaTarefaDeveRetornarErro() {
//        UUID idTarefaInvalido = UUID.randomUUID();
//        String email = "email@gmail.com";
//        when(tarefaRepository.buscaTarefaPorId(idTarefaInvalido)).thenReturn(Optional.empty());
//        APIException ex = assertThrows(APIException.class, () -> {
//            tarefaApplicationService.ativaTarefa(email, idTarefaInvalido);
//        });
//        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
//    }
//}