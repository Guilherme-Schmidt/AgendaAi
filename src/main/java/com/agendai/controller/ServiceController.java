package com.agendai.controller;

import com.agendai.dto.ServiceDTO;
import com.agendai.model.Service;
import com.agendai.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    public ResponseEntity<Service> createService(@Valid @RequestBody ServiceDTO dto) {
        try {
            Service createdService = serviceService.createService(dto);
            return new ResponseEntity<>(createdService, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Service>> getAllServices(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        List<Service> services = activeOnly ?
                serviceService.getAllActiveServices() :
                serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Service>> getAllServicesPaginated(Pageable pageable) {
        Page<Service> services = serviceService.getAllServices(pageable);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        return serviceService.getServiceById(id)
                .map(service -> ResponseEntity.ok(service))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/establishment/{establishmentId}")
    public ResponseEntity<List<Service>> getServicesByEstablishment(@PathVariable Long establishmentId) {
        List<Service> services = serviceService.getServicesByEstablishment(establishmentId);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Service>> getServicesByCategory(@PathVariable Long categoryId) {
        List<Service> services = serviceService.getServicesByCategory(categoryId);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/establishment/{establishmentId}/category/{categoryId}")
    public ResponseEntity<List<Service>> getServicesByEstablishmentAndCategory(
            @PathVariable Long establishmentId,
            @PathVariable Long categoryId) {
        List<Service> services = serviceService.getServicesByEstablishmentAndCategory(establishmentId, categoryId);
        return ResponseEntity.ok(services);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @Valid @RequestBody ServiceDTO dto) {
        try {
            Service updatedService = serviceService.updateService(id, dto);
            return ResponseEntity.ok(updatedService);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Service> toggleServiceStatus(@PathVariable Long id) {
        try {
            Service updatedService = serviceService.toggleServiceStatus(id);
            return ResponseEntity.ok(updatedService);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        try {
            serviceService.deleteService(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<Void> softDeleteService(@PathVariable Long id) {
        try {
            serviceService.softDeleteService(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}