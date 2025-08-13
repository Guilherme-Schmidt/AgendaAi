package com.agendai.service;

import com.agendai.dto.ProfessionalEstablishmentDTO;
import com.agendai.model.ProfessionalEstablishment;
import com.agendai.model.User;
import com.agendai.model.Establishment;
import com.agendai.model.UserType;
import com.agendai.repository.ProfessionalEstablishmentRepository;
import com.agendai.repository.UserRepository;
import com.agendai.repository.EstablishmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfessionalEstablishmentService {

    @Autowired
    private ProfessionalEstablishmentRepository professionalEstablishmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    public ProfessionalEstablishment linkProfessionalToEstablishment(ProfessionalEstablishmentDTO dto) {
        // Valida se o profissional existe e é do tipo PROFESSIONAL
        User professional = userRepository.findById(dto.getProfessionalId())
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        if (!professional.getType().equals(UserType.PROFESSIONAL)) {
            throw new RuntimeException("O usuário deve ser do tipo PROFESSIONAL");
        }

        // Valida se o estabelecimento existe
        Establishment establishment = establishmentRepository.findById(dto.getEstablishmentId())
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        // Verifica se já existe vínculo ativo
        Optional<ProfessionalEstablishment> existingRelation =
                professionalEstablishmentRepository.findActiveRelation(dto.getProfessionalId(), dto.getEstablishmentId());

        if (existingRelation.isPresent()) {
            throw new RuntimeException("Profissional já está vinculado a este estabelecimento");
        }

        ProfessionalEstablishment relation = ProfessionalEstablishment.builder()
                .professional(professional)
                .establishment(establishment)
                .isActive(true)
                .build();

        return professionalEstablishmentRepository.save(relation);
    }

    public List<User> getProfessionalsByEstablishment(Long establishmentId) {
        List<ProfessionalEstablishment> relations =
                professionalEstablishmentRepository.findByEstablishmentIdAndActive(establishmentId);

        return relations.stream()
                .map(ProfessionalEstablishment::getProfessional)
                .collect(Collectors.toList());
    }

    public List<Establishment> getEstablishmentsByProfessional(Long professionalId) {
        List<ProfessionalEstablishment> relations =
                professionalEstablishmentRepository.findByProfessionalIdAndActive(professionalId);

        return relations.stream()
                .map(ProfessionalEstablishment::getEstablishment)
                .collect(Collectors.toList());
    }

    public void unlinkProfessionalFromEstablishment(Long professionalId, Long establishmentId) {
        Optional<ProfessionalEstablishment> relation =
                professionalEstablishmentRepository.findActiveRelation(professionalId, establishmentId);

        if (relation.isPresent()) {
            ProfessionalEstablishment pe = relation.get();
            pe.setIsActive(false);
            professionalEstablishmentRepository.save(pe);
        } else {
            throw new RuntimeException("Vínculo não encontrado");
        }
    }

    public List<ProfessionalEstablishment> getAllActiveRelations() {
        return professionalEstablishmentRepository.findAll().stream()
                .filter(ProfessionalEstablishment::getIsActive)
                .collect(Collectors.toList());
    }
}
