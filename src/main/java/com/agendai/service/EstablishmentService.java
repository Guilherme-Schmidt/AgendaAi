package com.agendai.service;

import com.agendai.dto.EstablishmentDTO;
import com.agendai.model.Establishment;
import com.agendai.model.User;
import com.agendai.model.UserType;
import com.agendai.repository.EstablishmentRepository;
import com.agendai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstablishmentService {

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private UserRepository userRepository;

    public Establishment createEstablishment(EstablishmentDTO dto) {
        // Valida se o proprietário existe e é do tipo PROFESSIONAL
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado"));

        if (!owner.getType().equals(UserType.PROFESSIONAL)) {
            throw new RuntimeException("O proprietário deve ser do tipo PROFESSIONAL");
        }

        Establishment establishment = Establishment.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .openingTime(dto.getOpeningTime())
                .closingTime(dto.getClosingTime())
                .owner(owner)
                .build();

        return establishmentRepository.save(establishment);
    }

    public List<Establishment> getAllEstablishments() {
        return establishmentRepository.findAllOrderByName();
    }

    public List<Establishment> getEstablishmentsByOwner(Long ownerId) {
        return establishmentRepository.findByOwnerIdOrderByName(ownerId);
    }

    public List<Establishment> searchEstablishments(String search) {
        return establishmentRepository.searchEstablishments(search);
    }

    public Optional<Establishment> getEstablishmentById(Long id) {
        return establishmentRepository.findById(id);
    }

    public Establishment updateEstablishment(Long id, EstablishmentDTO dto) {
        Establishment establishment = establishmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado com ID: " + id));

        if (dto.getName() != null) establishment.setName(dto.getName());
        if (dto.getDescription() != null) establishment.setDescription(dto.getDescription());
        if (dto.getAddress() != null) establishment.setAddress(dto.getAddress());
        if (dto.getPhone() != null) establishment.setPhone(dto.getPhone());
        if (dto.getOpeningTime() != null) establishment.setOpeningTime(dto.getOpeningTime());
        if (dto.getClosingTime() != null) establishment.setClosingTime(dto.getClosingTime());

        return establishmentRepository.save(establishment);
    }

    public void deleteEstablishment(Long id) {
        if (!establishmentRepository.existsById(id)) {
            throw new RuntimeException("Estabelecimento não encontrado com ID: " + id);
        }
        establishmentRepository.deleteById(id);
    }
}