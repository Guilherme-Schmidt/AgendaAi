package com.agendai.repository;

import com.agendai.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.sql.Time;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByProfessionalIdAndAppointmentDateAndStartTime(Long professionalId, Date appointmentDate, Time startTime);

}
