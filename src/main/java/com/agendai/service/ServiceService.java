package com.agendai.service;

import com.agendai.dto.ServiceDTO;
import com.agendai.model.Service;
import com.agendai.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public Service createService(ServiceDTO dto) {
        // Verificar se já existe um serviço com o mesmo nome no estabelecimento
        if (serviceRepository.existsByNameAndEstablishmentId(dto.getName(), dto.getEstablishmentId())) {
            throw new RuntimeException("Já existe um serviço com esse nome neste estabelecimento");
        }

        return serviceRepository.save(Service.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .durationMinutes(dto.getDurationMinutes())
                .establishmentId(dto.getEstablishmentId())
                .categoryId(dto.getCategoryId())
                .active(dto.getActive())
                .build());
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public List<Service> getAllActiveServices() {
        return serviceRepository.findByActiveTrue();
    }

    public Page<Service> getAllServices(Pageable pageable) {
        return serviceRepository.findAll(pageable);
    }

    public Optional<Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    public Service getServiceByIdOrThrow(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + id));
    }

    public List<Service> getServicesByEstablishment(Long establishmentId) {
        return serviceRepository.findByEstablishmentIdAndActiveTrue(establishmentId);
    }

    public List<Service> getServicesByCategory(Long categoryId) {
        return serviceRepository.findByCategoryIdAndActiveTrue(categoryId);
    }

    public List<Service> getServicesByEstablishmentAndCategory(Long establishmentId, Long categoryId) {
        return serviceRepository.findByEstablishmentAndCategory(establishmentId, categoryId);
    }

    public Service updateService(Long id, ServiceDTO dto) {
        Service service = getServiceByIdOrThrow(id);

        // Verificar se o novo nome já existe (apenas se for diferente do atual)
        if (!service.getName().equals(dto.getName()) &&
                serviceRepository.existsByNameAndEstablishmentId(dto.getName(), dto.getEstablishmentId())) {
            throw new RuntimeException("Já existe um serviço com esse nome neste estabelecimento");
        }

        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setPrice(dto.getPrice());
        service.setDurationMinutes(dto.getDurationMinutes());
        service.setEstablishmentId(dto.getEstablishmentId());
        service.setCategoryId(dto.getCategoryId());
        service.setActive(dto.getActive());

        return serviceRepository.save(service);
    }

    public Service toggleServiceStatus(Long id) {
        Service service = getServiceByIdOrThrow(id);
        service.setActive(!service.getActive());
        return serviceRepository.save(service);
    }

    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new RuntimeException("Serviço não encontrado com ID: " + id);
        }
        serviceRepository.deleteById(id);
    }

    public void softDeleteService(Long id) {
        Service service = getServiceByIdOrThrow(id);
        service.setActive(false);
        serviceRepository.save(service);
    }
}
