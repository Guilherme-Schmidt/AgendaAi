package com.agendai.mapper;

import com.agendai.dto.EstablishmentSummaryResponse;
import com.agendai.model.Establishment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EstablishmentMapper {

    EstablishmentSummaryResponse toSummaryResponse(Establishment establishment);
}