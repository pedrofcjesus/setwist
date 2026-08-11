package com.setwist.backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.setwist.backend.dto.AuthResponseDTO;
import com.setwist.backend.dto.LoginRequestDTO;
import com.setwist.backend.dto.RegisterRequestDTO;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.UserRepository;
import com.setwist.backend.security.CustomUserDetailsService;
import com.setwist.backend.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    public String register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Este email já está registado.");
        }

        User user = new User(dto.getName(), dto.getEmail(), passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);

        return "Utilizador registado com sucesso!";
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());

        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponseDTO(token);
    }
}
