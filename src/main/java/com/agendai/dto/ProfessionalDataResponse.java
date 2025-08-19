package com.agendai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalDataResponse {
    private Long professionalId;
    private String bio;
    private Boolean available;
    private EstablishmentSummaryResponse establishment;
}