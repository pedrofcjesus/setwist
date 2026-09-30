package com.setwist.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.setwist.backend.dto.AuthResponseDTO;
import com.setwist.backend.dto.LoginRequestDTO;
import com.setwist.backend.dto.RegisterRequestDTO;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.UserRepository;
import com.setwist.backend.security.CustomUserDetailsService;
import com.setwist.backend.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerDto;
    private LoginRequestDTO loginDto;

    @BeforeEach
    void setUp() {
        registerDto = new RegisterRequestDTO();
        registerDto.setName("Pedro");
        registerDto.setEmail("pedro@example.com");
        registerDto.setPassword("password123");

        loginDto = new LoginRequestDTO();
        loginDto.setEmail("pedro@example.com");
        loginDto.setPassword("password123");
    }

    @Test
    @DisplayName("Deve registar utilizador com sucesso")
    void register_Success() {
        when(userRepository.existsByEmail("pedro@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        String result = authService.register(registerDto);

        assertThat(result).isEqualTo("Utilizador registado com sucesso!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registar e-mail já existente")
    void register_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmail("pedro@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Este email já está registado.");
    }

    @Test
    @DisplayName("Deve autenticar e devolver token JWT no login com sucesso")
    void login_Success() {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("pedro@example.com")
                .password("encodedPassword")
                .authorities("USER")
                .build();

        when(userDetailsService.loadUserByUsername("pedro@example.com")).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(mockUserDetails)).thenReturn("mocked-jwt-token");

        AuthResponseDTO response = authService.login(loginDto);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mocked-jwt-token");
        verify(authenticationManager).authenticate(any());
    }
}
