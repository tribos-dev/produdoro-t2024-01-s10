package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

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
    void setup() {
        MockitoAnnotations.openMocks(this);

        usuarioMock = mock(Usuario.class);
        when(usuarioRepository.buscaUsuarioPorEmail(usuarioEmail)).thenReturn(usuarioMock);
    }

    @Test
    void alteraStatusParaFoco_DeveAlterarStatusParaFoco() {
        //cenario
        doNothing().when(usuarioMock).validaUsuarioPorId(idUsuario);

        //acao
        usuarioApplicationService.mudaStatusParaFoco(usuarioEmail, idUsuario);

        //verificacao
        verify(usuarioRepository).buscaUsuarioPorEmail(usuarioEmail);
        verify(usuarioMock).validaUsuarioPorId(idUsuario);
        verify(usuarioMock).alteraStatusParaFoco(idUsuario);
        verify(usuarioRepository).salva(usuarioMock);
    }

    @Test
    void alteraStatusParaFoco_DeveLancarExcecaoUsuarioNaoEncontrado() {
        //cenario
        UUID idUsuarioNaoEncontrado = UUID.randomUUID();

//        when(usuarioRepository.buscaUsuarioPorEmail("email@email.com")).thenReturn(null);
        doThrow(APIException.build(HttpStatus.BAD_REQUEST, "Usuario não encontrado!"))
                .when(usuarioRepository).buscaUsuarioPorEmail("usuario@email.com");

        //acao
        APIException exception = assertThrows(APIException.class,
                () ->usuarioApplicationService.mudaStatusParaFoco("email@email.com", idUsuarioNaoEncontrado));

        //verificacao
        assertEquals("Usuario não encontrado!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail("email@email.com");
//        UUID idUsuarioInexistente = UUID.randomUUID();
//        doThrow(APIException.build(HttpStatus.BAD_REQUEST, "Usuario não encontrado!"))
//                .when(usuarioRepository).buscaUsuarioPorId(idUsuarioInexistente);
//        // acao
//        APIException exception = assertThrows(APIException.class,
//                () -> usuarioApplicationService.mudaStatusParaFoco("email@email.com", idUsuarioInexistente));
//        // verificacao
//        assertEquals("Usuario não encontrado!", exception.getMessage());
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
//        verify(usuarioRepository, times(1)).buscaUsuarioPorId(idUsuarioInexistente);

//        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(DataHelper.createUsuario());
//
//        assertThrows(APIException.class, () -> usuarioApplicationService.mudaStatusParaFoco(usuarioEmail, UUID.randomUUID()));
//        //cenario
//        UUID idUsuarioNaoEncontrado = UUID.randomUUID();
//
//        when(usuarioRepository.buscaUsuarioPorEmail(usuarioEmail)).thenReturn(usuarioMock);
//        doThrow(APIException.build(HttpStatus.BAD_REQUEST, "Usuario não encontrado!"))
//                .when(usuarioRepository).buscaUsuarioPorId(idUsuarioNaoEncontrado);
//
//        //acao
//        APIException exception = assertThrows(APIException.class,
//                () ->usuarioApplicationService.mudaStatusParaFoco(usuarioEmail, idUsuarioNaoEncontrado));
//
//        //verificacao
//        assertEquals("Usuário não encontrado", exception.getMessage());
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
//        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuarioEmail);
//    }
//        //cenario
//        UUID uuidInvalido = UUID.randomUUID();
//
//        when(usuarioRepository.buscaUsuarioPorEmail(usuarioEmail)).thenReturn(usuarioMock);
//
//        //acao
//        APIException exception = assertThrows(APIException.class,
//                () -> usuarioApplicationService.mudaStatusParaFoco(usuarioEmail, uuidInvalido));
//
//        //verificacao
//        assertEquals("Usuário não encontrado", exception.getMessage());
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
//        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuarioEmail);
    }
}