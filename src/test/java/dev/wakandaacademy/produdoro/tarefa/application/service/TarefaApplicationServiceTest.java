package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

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
    	APIException ex = assertThrows(APIException.class,() -> {
    		tarefaApplicationService.ativaTarefa(email, idTarefaInvalido);
    		});
    	assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());   	
    }

}
