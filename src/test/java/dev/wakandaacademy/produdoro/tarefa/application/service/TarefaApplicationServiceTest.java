package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @Test
    void deveRetornarIdTarefaNovaCriada() {
        TarefaRequest request = getTarefaRequest();
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }

    @Test
    void deveListarTarefasPorUsuario() {
    	Usuario usuarioEncontrado = DataHelper.createUsuario();
    	List<Tarefa> listaTarefas = DataHelper.createListTarefa();
    	when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuarioEncontrado);
    	when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuarioEncontrado);
    	when(tarefaRepository.buscaTarefaPorUsuario(any())).thenReturn(listaTarefas);
    	String usuario = "email@email.com";
    	UUID idUsuario = UUID.fromString("a713162f-20a9-4db9-a85b-90cd51ab18f4");
    	List<TarefaListResponse> response = tarefaApplicationService.buscaTarefaPorUsuario(usuario, idUsuario);
    	assertNotNull(response);
    	assertEquals(ArrayList.class, response.getClass());
    	assertEquals(8, response.size());
    }

    @Test
    void deveListarTarefasPorUsuarioFalha() {
    	Usuario usuarioEncontrado = DataHelper.createUsuario();
    	when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuarioEncontrado);
    	when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuarioEncontrado);
    	String usuario = "email@email.com";
    	UUID idUsuario = UUID.randomUUID();
    	APIException ex = assertThrows(APIException.class, ()->{tarefaApplicationService.buscaTarefaPorUsuario(usuario, idUsuario);});
    	assertEquals("A Credencial de autenticação não é válida", ex.getMessage());
    	assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());

    void deveRetornarTarefaConcluida() {
        Usuario usuario = DataHelper.createUsuario();
        UUID idTarefa = UUID.randomUUID();
        Tarefa tarefa = Tarefa.builder()
                .idTarefa(UUID.randomUUID())
                .status(StatusTarefa.A_FAZER)
                .idUsuario(usuario.getIdUsuario())
                .build();
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(tarefa.getIdTarefa())).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.salva(tarefa)).thenReturn(tarefa);
        tarefaApplicationService.concluiTarefa(usuario.getEmail(), tarefa.getIdTarefa());
        assertEquals(StatusTarefa.CONCLUIDA, tarefa.getStatus());
    }

    @Test
    void deveDeletarTarefasConcluidas(){
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> tarefasConcluidas = DataHelper.creatTarefasConcluidas();
        List<Tarefa> tarefas = DataHelper.createListTarefa();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefasConcluidas(any())).thenReturn(tarefasConcluidas);
        when(tarefaRepository.buscaTarefasPorUsuario(any())).thenReturn(tarefas);
        tarefaApplicationService.deletaTarefasConcluidas(usuario.getEmail(), usuario.getIdUsuario());
        verify(tarefaRepository, times(1)).deletaVariasTarefas(tarefasConcluidas);
        verify(tarefaRepository, times(1)).atualizaPosicaoDaTarefa(tarefas);
    }

    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }

    @Test
    @DisplayName("Ativa tarefa - deve ativar tarefa")
    void ativaTarefaDeveAtivarTarefa() {
        UUID idTarefa = DataHelper.createTarefa().getIdTarefa();
        UUID idUsuario = DataHelper.createUsuario().getIdUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        Usuario usuario = DataHelper.createUsuario();
        String email = "email@gmail.com";
        when(usuarioRepository.buscaUsuarioPorEmail(email)).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.ativaTarefa(email, idTarefa);
        verify(tarefaRepository, times(1)).buscaTarefaPorId(idTarefa);
        verify(tarefaRepository, times(1)).desativaTarefaAtiva(idUsuario);
        assertEquals(StatusAtivacaoTarefa.ATIVA, tarefa.getStatusAtivacao());
    }

    @Test
    @DisplayName("Ativa tarefa com id invalido - deve retornar exception")
    void ativaTarefaDeveRetornarErro() {
        UUID idTarefaInvalido = UUID.randomUUID();
        String email = "email@gmail.com";
        when(tarefaRepository.buscaTarefaPorId(idTarefaInvalido)).thenReturn(Optional.empty());
        APIException ex = assertThrows(APIException.class, () -> {
            tarefaApplicationService.ativaTarefa(email, idTarefaInvalido);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
    }
}