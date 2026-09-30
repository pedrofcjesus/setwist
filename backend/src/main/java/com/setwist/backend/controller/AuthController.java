package com.setwist.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.setwist.backend.dto.AuthResponseDTO;
import com.setwist.backend.dto.LoginRequestDTO;
import com.setwist.backend.dto.RegisterRequestDTO;
import com.setwist.backend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para registo de novos utilizadores e autenticação com JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registar novo utilizador", description = "Cria uma nova conta de utilizador no sistema e devolve uma mensagem de confirmação.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilizador registado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de registo inválidos ou e-mail já registado")
    })
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar utilizador", description = "Valida as credenciais do utilizador e devolve o token JWT de autenticação.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticação efetuada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas (e-mail ou palavra-passe incorretos)")
    })
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
