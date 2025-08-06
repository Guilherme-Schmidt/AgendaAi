package com.agendai.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class EstablishmentDTO {
    private String name;
    private String description;
    private String address;
    private String phone;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Long ownerId;
}