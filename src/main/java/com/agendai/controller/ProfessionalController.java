package com.agendai.controller;

import com.agendai.model.Professional;
import com.agendai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/professionals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfessionalController {

    private final UserService userService;

    /**
     * Atribui um estabelecimento a um profissional
     */
    @PutMapping("/{userId}/establishment/{establishmentId}")
    public ResponseEntity<?> assignEstablishment(
            @PathVariable Long userId,
            @PathVariable Long establishmentId) {
        try {
            Professional updatedProfessional = userService.assignEstablishmentToProfessional(userId, establishmentId);
            return ResponseEntity.ok(updatedProfessional);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Remove a atribuição de estabelecimento de um profissional
     */
    @DeleteMapping("/{userId}/establishment")
    public ResponseEntity<?> removeEstablishment(@PathVariable Long userId) {
        try {
            Professional updatedProfessional = userService.removeEstablishmentFromProfessional(userId);
            return ResponseEntity.ok(updatedProfessional);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Atualiza apenas a bio do profissional
     */
    @PutMapping("/{userId}/bio")
    public ResponseEntity<?> updateBio(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        try {
            String bio = request.get("bio");

            // Usar o método específico para atualizar bio
            Professional updatedProfessional = userService.updateProfessionalBio(userId, bio);

            return ResponseEntity.ok(updatedProfessional);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Busca dados completos do profissional
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfessionalData(@PathVariable Long userId) {
        try {
            Professional professional = userService.findProfessionalByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

            return ResponseEntity.ok(professional);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Verifica se um profissional tem estabelecimento atribuído
     */
    @GetMapping("/{userId}/has-establishment")
    public ResponseEntity<?> hasEstablishment(@PathVariable Long userId) {
        try {
            Professional professional = userService.findProfessionalByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

            Map<String, Object> response = new HashMap<>();
            response.put("hasEstablishment", professional.hasEstablishment());
            response.put("establishmentId", professional.getEstablishmentId());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Método auxiliar para criar respostas de erro consistentes
    private ResponseEntity<?> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        return new ResponseEntity<>(error, status);
    }
}