package com.agendai.service;

import com.agendai.dto.LoginRequest;
import com.agendai.dto.RegisterRequest;
import com.agendai.dto.AuthResponse;
import com.agendai.exception.BusinessException;
import com.agendai.mapper.UserMapper;
import com.agendai.model.Professional;
import com.agendai.model.User;
import com.agendai.model.UserType;
import com.agendai.repository.ProfessionalRepository;
import com.agendai.repository.UserRepository;
import com.agendai.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Validar se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já está em uso");
        }

        // Criar usuário
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // Se for profissional, criar registro adicional
        if (request.getType() == UserType.PROFESSIONAL) {
            createProfessionalRecord(savedUser, request);
        }

        // Gerar tokens
        String token = jwtUtil.generateTokenFromEmail(savedUser.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail());

        log.info("User registered successfully with ID: {}", savedUser.getId());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .userType(savedUser.getType())
                .expiresAt(LocalDateTime.now().plusSeconds(86400)) // 24h
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

            if (!user.getActive()) {
                throw new BusinessException("Conta desativada");
            }

            String token = jwtUtil.generateToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            log.info("User logged in successfully: {}", request.getEmail());

            return AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .type("Bearer")
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .userType(user.getType())
                    .expiresAt(LocalDateTime.now().plusSeconds(86400))
                    .build();

        } catch (BadCredentialsException e) {
            log.error("Bad credentials for user: {}", request.getEmail());
            throw new BusinessException("Email ou senha inválidos");
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateJwtToken(refreshToken)) {
            throw new BusinessException("Refresh token inválido");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        String newToken = jwtUtil.generateTokenFromEmail(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .type("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .userType(user.getType())
                .expiresAt(LocalDateTime.now().plusSeconds(86400))
                .build();
    }

    private void createProfessionalRecord(User user, RegisterRequest request) {
        Professional professional = Professional.builder()
                .user(user)
                .bio(request.getProfessionalData() != null ?
                        request.getProfessionalData().getBio() : null)
                .available(true)
                .build();

        // TODO: Validar establishment se fornecido
        if (request.getProfessionalData() != null &&
                request.getProfessionalData().getEstablishmentId() != null) {
            // Implementar validação do establishment
        }

        professionalRepository.save(professional);
    }
}
