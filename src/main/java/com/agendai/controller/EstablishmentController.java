package com.agendai.controller;

import com.agendai.dto.EstablishmentDTO;
import com.agendai.model.Establishment;
import com.agendai.service.EstablishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/establishments")
@CrossOrigin(origins = "*")
public class EstablishmentController {

    @Autowired
    private EstablishmentService establishmentService;

    @PostMapping
    public ResponseEntity<Establishment> createEstablishment(@RequestBody EstablishmentDTO dto) {
        try {
            Establishment establishment = establishmentService.createEstablishment(dto);
            return ResponseEntity.ok(establishment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Establishment>> getAllEstablishments() {
        List<Establishment> establishments = establishmentService.getAllEstablishments();
        return ResponseEntity.ok(establishments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Establishment> getEstablishmentById(@PathVariable Long id) {
        return establishmentService.getEstablishmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Establishment>> getEstablishmentsByOwner(@PathVariable Long ownerId) {
        List<Establishment> establishments = establishmentService.getEstablishmentsByOwner(ownerId);
        return ResponseEntity.ok(establishments);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Establishment>> searchEstablishments(@RequestParam String q) {
        List<Establishment> establishments = establishmentService.searchEstablishments(q);
        return ResponseEntity.ok(establishments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Establishment> updateEstablishment(@PathVariable Long id, @RequestBody EstablishmentDTO dto) {
        try {
            Establishment establishment = establishmentService.updateEstablishment(id, dto);
            return ResponseEntity.ok(establishment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstablishment(@PathVariable Long id) {
        try {
            establishmentService.deleteEstablishment(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}