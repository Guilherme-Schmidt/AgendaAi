package com.agendai.service;

import com.agendai.dto.AppointmentDTO;
import com.agendai.model.*;
import com.agendai.repository.AppointmentRepository;
import com.agendai.repository.ProfessionalRepository;
import com.agendai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    public Appointment createAppointment(AppointmentDTO dto) {
        // 1️⃣ Busca o usuário e valida tipo
        User professionalUser = userRepository.findById(dto.getProfessionalId())
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        if (!professionalUser.getType().equals(UserType.PROFESSIONAL)) {
            throw new RuntimeException("O profissional deve ser do tipo PROFESSIONAL");
        }

        // 2️⃣ Busca o registro de Professional vinculado a esse usuário
        Professional professional = professionalRepository.findByUserId(professionalUser.getId())
                .orElseThrow(() -> new RuntimeException("Registro de profissional não encontrado"));

        // 3️⃣ Verifica conflito de horário
        boolean exists = appointmentRepository.existsByProfessionalIdAndAppointmentDateAndStartTime(
                professional.getId(),
                dto.getAppointmentDate(), // java.sql.Date
                dto.getStartTime()        // java.sql.Time
        );
        if (exists) {
            throw new RuntimeException("Já existe um agendamento para esse profissional nesse horário.");
        }

        // 4️⃣ Criação do agendamento
        Appointment appointment = Appointment.builder()
                .clientId(dto.getClientId())
                .professionalId(professional.getId())
                .serviceId(dto.getServiceId())
                .appointmentDate(dto.getAppointmentDate())
                .startTime(dto.getStartTime())
                .status(Status.valueOf(dto.getStatus()))
                .notes(dto.getNotes())
                .build();

        return appointmentRepository.save(appointment);
    }



    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment updateAppointment(Long id, AppointmentDTO dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + id));

        if (dto.getClientId() != null) appointment.setClientId(dto.getClientId());
        if (dto.getProfessionalId() != null) appointment.setProfessionalId(dto.getProfessionalId());
        if (dto.getServiceId() != null) appointment.setServiceId(dto.getServiceId());
        if (dto.getAppointmentDate() != null) appointment.setAppointmentDate(dto.getAppointmentDate());
        if (dto.getStartTime() != null) appointment.setStartTime(dto.getStartTime());
        if (dto.getStatus() != null) appointment.setStatus(Status.valueOf(dto.getStatus()));
        if (dto.getNotes() != null) appointment.setNotes(dto.getNotes());

        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Agendamento não encontrado com ID: " + id);
        }
        appointmentRepository.deleteById(id);
    }
}
