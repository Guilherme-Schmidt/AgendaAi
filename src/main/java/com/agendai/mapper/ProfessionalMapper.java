package com.agendai.mapper;

import com.agendai.dto.ProfessionalDataResponse;
import com.agendai.model.Professional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EstablishmentMapper.class})
public interface ProfessionalMapper {

    @Mapping(target = "professionalId", source = "id")
    ProfessionalDataResponse toResponse(Professional professional);
}