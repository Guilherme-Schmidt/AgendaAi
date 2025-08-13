package com.agendai.service;

import com.agendai.dto.AuthRequest;
import com.agendai.dto.AuthResponse;
import com.agendai.model.User;
import com.agendai.repository.UserRepository;
import com.agendai.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Senha incorreta");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);

    }

}
