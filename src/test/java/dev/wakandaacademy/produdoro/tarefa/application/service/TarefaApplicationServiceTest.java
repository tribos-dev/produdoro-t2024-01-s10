package dev.wakandaacademy.produdoro.tarefa.application.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import org.springframework.http.HttpStatus;


import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

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

    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }
}
