package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioApplicationServiceTest {

    @InjectMocks
    private UsuarioApplicationService usuarioApplicationService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioMock;
    private final String usuarioEmail = "usuario@teste.com";
    private final UUID idUsuario = UUID.randomUUID();

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        usuarioMock = mock(Usuario.class);
        when(usuarioRepository.buscaUsuarioPorEmail(usuarioEmail)).thenReturn(usuarioMock);
    }

    @Test
    void alteraStatusParaFoco_DeveAlterarStatusParaFoco(){
        doNothing().when(usuarioMock).validaUsuarioPorId(idUsuario);

        usuarioApplicationService.alteraStatusParaFoco(usuarioEmail, idUsuario);

        verify(usuarioRepository).buscaUsuarioPorEmail(usuarioEmail);
        verify(usuarioMock).validaUsuarioPorId(idUsuario);
        verify(usuarioMock).alteraStatusParaFoco();
        verify(usuarioRepository).salva(usuarioMock);
    }

//    @Test
//    void alteraStatusParaFoco_DeveLancarExcecaoUsuarioNaoEncontrado(){
//        when(usuarioRepository.buscaUsuarioPorEmail(usuarioEmail)).thenReturn(null);
//        assertThrows(APIException.class, () -> usuarioApplicationService.alteraStatusParaFoco(usuarioEmail, idUsuario));
//        verify(usuarioRepository, never()).salva(any());
//    }

}