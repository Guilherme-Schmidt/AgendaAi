package com.agendai.controller;

import com.agendai.dto.ProfessionalEstablishmentDTO;
import com.agendai.model.ProfessionalEstablishment;
import com.agendai.model.User;
import com.agendai.model.Establishment;
import com.agendai.service.ProfessionalEstablishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professional-establishments")
@CrossOrigin(origins = "*")
public class ProfessionalEstablishmentController {

    @Autowired
    private ProfessionalEstablishmentService professionalEstablishmentService;

    @PostMapping("/link")
    public ResponseEntity<ProfessionalEstablishment> linkProfessionalToEstablishment(
            @RequestBody ProfessionalEstablishmentDTO dto) {
        try {
            ProfessionalEstablishment relation = professionalEstablishmentService.linkProfessionalToEstablishment(dto);
            return ResponseEntity.ok(relation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/establishment/{establishmentId}/professionals")
    public ResponseEntity<List<User>> getProfessionalsByEstablishment(@PathVariable Long establishmentId) {
        List<User> professionals = professionalEstablishmentService.getProfessionalsByEstablishment(establishmentId);
        return ResponseEntity.ok(professionals);
    }

    @GetMapping("/professional/{professionalId}/establishments")
    public ResponseEntity<List<Establishment>> getEstablishmentsByProfessional(@PathVariable Long professionalId) {
        List<Establishment> establishments = professionalEstablishmentService.getEstablishmentsByProfessional(professionalId);
        return ResponseEntity.ok(establishments);
    }

    @DeleteMapping("/unlink")
    public ResponseEntity<Void> unlinkProfessionalFromEstablishment(
            @RequestParam Long professionalId,
            @RequestParam Long establishmentId) {
        try {
            professionalEstablishmentService.unlinkProfessionalFromEstablishment(professionalId, establishmentId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/relations")
    public ResponseEntity<List<ProfessionalEstablishment>> getAllActiveRelations() {
        List<ProfessionalEstablishment> relations = professionalEstablishmentService.getAllActiveRelations();
        return ResponseEntity.ok(relations);
    }
}