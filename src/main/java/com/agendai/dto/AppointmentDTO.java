package com.agendai.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
public class AppointmentDTO {
    private Long id;
    private Long clientId;
    private Long professionalId;
    private Long serviceId;
    private Date appointmentDate;
    private Time startTime;
    private String status; // Pode ser um Enum também
    private String notes;
}
