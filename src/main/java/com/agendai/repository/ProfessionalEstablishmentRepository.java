package com.agendai.repository;

import com.agendai.model.ProfessionalEstablishment;
import com.agendai.model.User;
import com.agendai.model.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfessionalEstablishmentRepository extends JpaRepository<ProfessionalEstablishment, Long> {

    // Buscar profissionais de um estabelecimento
    @Query("SELECT pe FROM ProfessionalEstablishment pe WHERE pe.establishment.id = :establishmentId AND pe.isActive = true")
    List<ProfessionalEstablishment> findByEstablishmentIdAndActive(@Param("establishmentId") Long establishmentId);

    // Buscar estabelecimentos de um profissional
    @Query("SELECT pe FROM ProfessionalEstablishment pe WHERE pe.professional.id = :professionalId AND pe.isActive = true")
    List<ProfessionalEstablishment> findByProfessionalIdAndActive(@Param("professionalId") Long professionalId);

    // Verificar se já existe vínculo ativo
    @Query("SELECT pe FROM ProfessionalEstablishment pe WHERE pe.professional.id = :professionalId AND pe.establishment.id = :establishmentId AND pe.isActive = true")
    Optional<ProfessionalEstablishment> findActiveRelation(@Param("professionalId") Long professionalId, @Param("establishmentId") Long establishmentId);

    // Verificar se existe vínculo (ativo ou inativo)
    boolean existsByProfessionalIdAndEstablishmentId(Long professionalId, Long establishmentId);
}
