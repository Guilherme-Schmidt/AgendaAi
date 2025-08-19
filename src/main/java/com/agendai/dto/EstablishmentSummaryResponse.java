package com.agendai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstablishmentSummaryResponse {
    private Long id;
    private String name;
    private String address;
    private String phone;
}