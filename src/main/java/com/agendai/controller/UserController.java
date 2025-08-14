package com.agendai.controller;

import com.agendai.dto.UserDTO;
import com.agendai.model.Professional;
import com.agendai.model.User;
import com.agendai.model.UserType;
import com.agendai.repository.ProfessionalRepository;
import com.agendai.repository.UserRepository;
import com.agendai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO dto) {
        try {
            User createdUser = userService.register(dto);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("timestamp", java.time.LocalDateTime.now().toString());

            // Determine specific HTTP status based on error type
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().contains("Email already in use")) {
                status = HttpStatus.CONFLICT;
            } else if (e.getMessage().contains("not found")) {
                status = HttpStatus.NOT_FOUND;
            }

            return new ResponseEntity<>(error, status);
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> listUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // ========== ENDPOINTS ESPECÍFICOS PARA PROFISSIONAIS ==========

    @GetMapping("/professionals")
    public ResponseEntity<List<User>> listProfessionals() {
        List<User> professionals = userRepository.findByTypeAndActiveTrue(UserType.PROFESSIONAL);
        return ResponseEntity.ok(professionals);
    }

    @GetMapping("/professionals/detailed")
    public ResponseEntity<List<Professional>> listProfessionalsDetailed() {
        List<Professional> professionals = professionalRepository.findAllWithActiveUsers();
        return ResponseEntity.ok(professionals);
    }

    @GetMapping("/professionals/establishment/{establishmentId}")
    public ResponseEntity<List<User>> listProfessionalsByEstablishment(@PathVariable Long establishmentId) {
        List<User> professionals = userRepository.findProfessionalsByEstablishment(establishmentId);
        return ResponseEntity.ok(professionals);
    }

    @GetMapping("/professionals/establishment/{establishmentId}/detailed")
    public ResponseEntity<List<Professional>> listProfessionalsByEstablishmentDetailed(@PathVariable Long establishmentId) {
        List<Professional> professionals = professionalRepository.findByEstablishmentIdWithActiveUsers(establishmentId);
        return ResponseEntity.ok(professionals);
    }

    @GetMapping("/professionals/without-establishment")
    public ResponseEntity<List<Professional>> listProfessionalsWithoutEstablishment() {
        List<Professional> professionals = professionalRepository.findByEstablishmentIdIsNull();
        return ResponseEntity.ok(professionals);
    }

    @GetMapping("/clients")
    public ResponseEntity<List<User>> listClients() {
        List<User> clients = userRepository.findByTypeAndActiveTrue(UserType.CLIENT);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<User>> listUsersByType(@PathVariable UserType type) {
        List<User> users = userRepository.findByTypeAndActiveTrue(type);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/professional")
    public ResponseEntity<Professional> getProfessionalByUserId(@PathVariable Long id) {
        Optional<Professional> professional = userService.findProfessionalByUserId(id);
        return professional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        try {
            User updatedUser = userService.updateUser(id, dto);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("timestamp", java.time.LocalDateTime.now().toString());

            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (e.getMessage().contains("não encontrado") || e.getMessage().contains("not found")) {
                status = HttpStatus.NOT_FOUND;
            }

            return new ResponseEntity<>(error, status);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("timestamp", java.time.LocalDateTime.now().toString());

            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para atribuir estabelecimento a um profissional
    @PutMapping("/{userId}/professional/establishment/{establishmentId}")
    public ResponseEntity<?> assignEstablishmentToProfessional(
            @PathVariable Long userId,
            @PathVariable Long establishmentId) {
        try {
            UserDTO dto = new UserDTO();
            dto.setType(UserType.PROFESSIONAL);
            dto.setEstablishmentId(establishmentId);

            User updatedUser = userService.updateUser(userId, dto);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("timestamp", java.time.LocalDateTime.now().toString());

            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}