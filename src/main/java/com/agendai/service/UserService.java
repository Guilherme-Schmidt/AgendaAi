package com.agendai.service;

import com.agendai.dto.UserDTO;
import com.agendai.model.Professional;
import com.agendai.model.User;
import com.agendai.model.UserType;
import com.agendai.repository.ProfessionalRepository;
import com.agendai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public User register(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Criar o usuário
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

        User savedUser = userRepository.save(user);

        // Se for PROFESSIONAL, criar registro básico na tabela professionals
        if (dto.getType() == UserType.PROFESSIONAL) {
            Professional professional = Professional.builder()
                    .userId(savedUser.getId())
                    .user(savedUser)
                    .establishmentId(dto.getEstablishmentId()) // Pode ser null
                    .bio(dto.getBio()) // Pode ser null
                    .build();

            professionalRepository.save(professional);
        }

        return savedUser;
    }

    @Transactional
    public User updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));

        // Atualizar dados básicos do usuário
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(encoder.encode(dto.getPassword()));
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getDocumentId() != null) {
            user.setCpf(dto.getDocumentId());
        }
        if (dto.getBirthDate() != null) {
            user.setBirthDate(dto.getBirthDate());
        }

        // Gerenciar mudança de tipo de usuário
        if (dto.getType() != null && dto.getType() != user.getType()) {
            UserType oldType = user.getType();
            user.setType(dto.getType());

            // Se mudou PARA professional, criar registro
            if (dto.getType() == UserType.PROFESSIONAL && oldType != UserType.PROFESSIONAL) {
                if (!professionalRepository.existsByUserId(id)) {
                    Professional professional = Professional.builder()
                            .userId(id)
                            .user(user)
                            .establishmentId(dto.getEstablishmentId()) // Pode ser null
                            .bio(dto.getBio()) // Pode ser null
                            .build();
                    professionalRepository.save(professional);
                }
            }
            // Se mudou DE professional para outro tipo, remover registro
            else if (oldType == UserType.PROFESSIONAL && dto.getType() != UserType.PROFESSIONAL) {
                professionalRepository.deleteByUserId(id);
            }
        }

        // Se é professional, atualizar dados específicos
        if (user.getType() == UserType.PROFESSIONAL) {
            Optional<Professional> professionalOpt = professionalRepository.findByUserId(id);
            if (professionalOpt.isPresent()) {
                Professional professional = professionalOpt.get();
                boolean needsUpdate = false;

                if (dto.getEstablishmentId() != null) {
                    professional.setEstablishmentId(dto.getEstablishmentId());
                    needsUpdate = true;
                }
                if (dto.getBio() != null) {
                    professional.setBio(dto.getBio());
                    needsUpdate = true;
                }

                if (needsUpdate) {
                    professionalRepository.save(professional);
                }
            }
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        // Se for professional, deletar primeiro o registro da tabela professionals
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent() && userOpt.get().getType() == UserType.PROFESSIONAL) {
            professionalRepository.deleteByUserId(id);
        }

        userRepository.deleteById(id);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<Professional> findProfessionalByUserId(Long userId) {
        return professionalRepository.findByUserId(userId);
    }

    // ========== MÉTODOS ESPECÍFICOS PARA PROFISSIONAIS ==========

    /**
     * Atribui um estabelecimento a um profissional
     */
    @Transactional
    public Professional assignEstablishmentToProfessional(Long userId, Long establishmentId) {
        // Verificar se o usuário existe e é profissional
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (user.getType() != UserType.PROFESSIONAL) {
            throw new RuntimeException("Usuário não é um profissional");
        }

        // Buscar o registro do profissional
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Registro de profissional não encontrado"));

        // Atribuir o estabelecimento (sem validar se existe por enquanto)
        professional.setEstablishmentId(establishmentId);
        return professionalRepository.save(professional);
    }

    /**
     * Remove a atribuição de estabelecimento de um profissional
     */
    @Transactional
    public Professional removeEstablishmentFromProfessional(Long userId) {
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        professional.setEstablishmentId(null);
        return professionalRepository.save(professional);
    }

    /**
     * Atualiza apenas a bio do profissional
     */
    @Transactional
    public Professional updateProfessionalBio(Long userId, String bio) {
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        professional.setBio(bio);
        return professionalRepository.save(professional);
    }
}