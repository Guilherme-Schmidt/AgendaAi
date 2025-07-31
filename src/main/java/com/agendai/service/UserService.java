package com.agendai.service;

import com.agendai.dto.UserDTO;
import com.agendai.model.User;
import com.agendai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User register(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .type(dto.getType())
                .phone(dto.getPhone())
                .cpf(dto.getDocumentId())
                .birthDate(dto.getBirthDate())
                .active(true)
                .build();

        return userRepository.save(user);
    }

    public User updateUser(Long id, UserDTO dto){
        User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("Usuário não encontrado com o ID: " + id));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(encoder.encode(dto.getPassword()));
        }
        if (dto.getType() != null) user.setType(dto.getType());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getDocumentId() != null) user.setCpf(dto.getDocumentId());
        if (dto.getBirthDate() != null) user.setBirthDate(dto.getBirthDate());

        return userRepository.save(user);
    }

    public void deleteUser(Long id){
        if(!userRepository.existsById(id)){
             throw new RuntimeException("Usuário não encontrado com ID: "+ id);
        }
        userRepository.deleteById(id);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

}
