package com.agendai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "client_id")
    private Long clientId;
    @Column(name = "professional_id")
    private Long professionalId;
    @Column(name = "service_id")
    private Long serviceId;
    @Column(name = "appointment_date")
    @Temporal(TemporalType.DATE) // Use DATE se você só precisa da data
    private Date appointmentDate;
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP) // Use TIMESTAMP se você precisa da data e hora
    private Date startTime;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String notes;
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
}
