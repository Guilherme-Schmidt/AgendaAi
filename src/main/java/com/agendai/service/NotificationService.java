package com.agendai.service;

import com.agendai.model.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendBookingConfirmation(Booking booking) {
        try {
            String subject = "Agendamento Criado - " + booking.getEstablishment().getName();
            String text = buildBookingConfirmationMessage(booking);

            sendEmail(booking.getCustomer().getEmail(), subject, text);

            log.info("Booking confirmation sent to: {}", booking.getCustomer().getEmail());
        } catch (Exception e) {
            log.error("Error sending booking confirmation", e);
        }
    }

    @Async
    public void sendBookingStatusUpdate(Booking booking) {
        try {
            String subject = "Agendamento " + booking.getStatus().name() + " - " + booking.getEstablishment().getName();
            String text = buildStatusUpdateMessage(booking);

            sendEmail(booking.getCustomer().getEmail(), subject, text);

            log.info("Booking status update sent to: {}", booking.getCustomer().getEmail());
        } catch (Exception e) {
            log.error("Error sending booking status update", e);
        }
    }

    @Async
    public void sendBookingCancellation(Booking booking) {
        try {
            String subject = "Agendamento Cancelado - " + booking.getEstablishment().getName();
            String text = buildCancellationMessage(booking);

            sendEmail(booking.getCustomer().getEmail(), subject, text);

            log.info("Booking cancellation sent to: {}", booking.getCustomer().getEmail());
        } catch (Exception e) {
            log.error("Error sending booking cancellation", e);
        }
    }

    @Async
    public void sendBookingReminder(Booking booking) {
        try {
            String subject = "Lembrete: Agendamento Amanhã - " + booking.getEstablishment().getName();
            String text = buildReminderMessage(booking);

            sendEmail(booking.getCustomer().getEmail(), subject, text);

            log.info("Booking reminder sent to: {}", booking.getCustomer().getEmail());
        } catch (Exception e) {
            log.error("Error sending booking reminder", e);
        }
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    private String buildBookingConfirmationMessage(Booking booking) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return String.format("""
                Olá %s,
                
                Seu agendamento foi criado com sucesso!
                
                Detalhes do Agendamento:
                - Estabelecimento: %s
                - Profissional: %s
                - Serviço: %s
                - Data/Hora: %s
                - Valor: R$ %.2f
                
                Status: %s
                
                Em caso de dúvidas, entre em contato.
                
                Agendai - Sistema de Agendamentos
                """,
                booking.getCustomer().getName(),
                booking.getEstablishment().getName(),
                booking.getProfessional().getUser().getName(),
                booking.getService().getName(),
                booking.getBookingDateTime().format(formatter),
                booking.getTotalAmount(),
                booking.getStatus());
    }

    private String buildStatusUpdateMessage(Booking booking) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return String.format("""
                Olá %s,
                
                O status do seu agendamento foi atualizado.
                
                Novo Status: %s
                Data/Hora: %s
                Estabelecimento: %s
                
                Agendai - Sistema de Agendamentos
                """,
                booking.getCustomer().getName(),
                booking.getStatus(),
                booking.getBookingDateTime().format(formatter),
                booking.getEstablishment().getName());
    }

    private String buildCancellationMessage(Booking booking) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return String.format("""
                Olá %s,
                
                Seu agendamento foi cancelado.
                
                Detalhes:
                - Data/Hora: %s
                - Estabelecimento: %s
                - Motivo: %s
                
                Se você não solicitou este cancelamento, entre em contato.
                
                Agendai - Sistema de Agendamentos
                """,
                booking.getCustomer().getName(),
                booking.getBookingDateTime().format(formatter),
                booking.getEstablishment().getName(),
                booking.getCancellationReason() != null ? booking.getCancellationReason() : "Não informado");
    }

    private String buildReminderMessage(Booking booking) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return String.format("""
                Olá %s,
                
                Este é um lembrete do seu agendamento para amanhã.
                
                Detalhes:
                - Estabelecimento: %s
                - Profissional: %s
                - Serviço: %s
                - Data/Hora: %s
                - Endereço: %s
                
                Nos vemos lá!
                
                Agendai - Sistema de Agendamentos
                """,
                booking.getCustomer().getName(),
                booking.getEstablishment().getName(),
                booking.getProfessional().getUser().getName(),
                booking.getService().getName(),
                booking.getBookingDateTime().format(formatter),
                booking.getEstablishment().getAddress());
    }
}