package com.agendai.service;

import com.agendai.dto.request.booking.CreateBookingRequest;
import com.agendai.dto.response.booking.BookingResponse;
import com.agendai.dto.response.PagedResponse;
import com.agendai.exception.BusinessException;
import com.agendai.exception.ResourceNotFoundException;
import com.agendai.model.*;
import com.agendai.model.enums.BookingStatus;
import com.agendai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceRepository serviceRepository;
    private final EstablishmentRepository establishmentRepository;
    private final NotificationService notificationService;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        log.info("Creating booking for customer: {}, professional: {}",
                request.getCustomerId(), request.getProfessionalId());

        // Validações
        User customer = validateCustomer(request.getCustomerId());
        Professional professional = validateProfessional(request.getProfessionalId());
        Service service = validateService(request.getServiceId());

        LocalDateTime bookingDateTime = request.getBookingDate().atTime(request.getBookingTime());
        LocalDateTime endDateTime = bookingDateTime.plusMinutes(service.getDurationMinutes());

        // Validar disponibilidade
        validateAvailability(professional.getId(), bookingDateTime, endDateTime);

        // Validar horário de funcionamento
        validateBusinessHours(professional.getEstablishment(), bookingDateTime);

        // Criar agendamento
        Booking booking = Booking.builder()
                .customer(customer)
                .professional(professional)
                .service(service)
                .establishment(professional.getEstablishment())
                .bookingDateTime(bookingDateTime)
                .endDateTime(endDateTime)
                .status(BookingStatus.PENDING)
                .totalAmount(service.getPrice())
                .notes(request.getNotes())
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // Enviar notificações
        notificationService.sendBookingConfirmation(savedBooking);

        log.info("Booking created successfully with ID: {}", savedBooking.getId());
        return mapToResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableSlots(Long professionalId, LocalDate date, Integer serviceDurationMinutes) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));

        if (professional.getEstablishment() == null) {
            throw new BusinessException("Profissional não possui estabelecimento vinculado");
        }

        Establishment establishment = professional.getEstablishment();
        LocalTime openingTime = establishment.getOpeningTime();
        LocalTime closingTime = establishment.getClosingTime();

        if (openingTime == null || closingTime == null) {
            throw new BusinessException("Horário de funcionamento não configurado");
        }

        // Buscar agendamentos existentes
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Booking> existingBookings = bookingRepository.findConflictingBookings(
                professionalId, startOfDay, endOfDay);

        // Gerar slots disponíveis
        List<LocalTime> availableSlots = generateTimeSlots(
                openingTime, closingTime, serviceDurationMinutes, existingBookings);

        return availableSlots;
    }

    @Transactional(readOnly = true)
    public PagedResponse<BookingResponse> getCustomerBookings(Long customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingDateTime").descending());
        Page<Booking> bookingPage = bookingRepository.findByCustomerId(customerId, pageable);

        List<BookingResponse> content = bookingPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return createPagedResponse(bookingPage, content);
    }

    @Transactional(readOnly = true)
    public PagedResponse<BookingResponse> getProfessionalBookings(Long professionalId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingDateTime").descending());
        Page<Booking> bookingPage = bookingRepository.findByProfessionalId(professionalId, pageable);

        List<BookingResponse> content = bookingPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return createPagedResponse(bookingPage, content);
    }

    @Transactional
    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Apenas agendamentos pendentes podem ser confirmados");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);

        notificationService.sendBookingStatusUpdate(savedBooking);

        return mapToResponse(savedBooking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, String reason, String cancelledBy) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        if (!booking.canBeCancelled()) {
            throw new BusinessException("Este agendamento não pode ser cancelado");
        }

        booking.cancel(reason, cancelledBy);
        Booking savedBooking = bookingRepository.save(booking);

        notificationService.sendBookingCancellation(savedBooking);

        return mapToResponse(savedBooking);
    }

    // Métodos auxiliares
    private User validateCustomer(Long customerId) {
        return userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    private Professional validateProfessional(Long professionalId) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));

        if (!professional.getAvailable()) {
            throw new BusinessException("Profissional não está disponível");
        }

        if (professional.getEstablishment() == null) {
            throw new BusinessException("Profissional não possui estabelecimento");
        }

        return professional;
    }

    private Service validateService(Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        if (!service.getActive()) {
            throw new BusinessException("Serviço não está ativo");
        }

        return service;
    }

    private void validateAvailability(Long professionalId, LocalDateTime start, LocalDateTime end) {
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                professionalId, start, end);

        if (!conflictingBookings.isEmpty()) {
            throw new BusinessException("Horário não disponível");
        }
    }

    private void validateBusinessHours(Establishment establishment, LocalDateTime bookingDateTime) {
        LocalTime bookingTime = bookingDateTime.toLocalTime();

        if (establishment.getOpeningTime() != null &&
                establishment.getClosingTime() != null) {

            if (bookingTime.isBefore(establishment.getOpeningTime()) ||
                    bookingTime.isAfter(establishment.getClosingTime())) {
                throw new BusinessException("Horário fora do funcionamento do estabelecimento");
            }
        }
    }

    private List<LocalTime> generateTimeSlots(LocalTime openingTime, LocalTime closingTime,
                                              Integer serviceDuration, List<Booking> existingBookings) {
        // Implementação simplificada - gerar slots de 30 em 30 minutos
        List<LocalTime> slots = new ArrayList<>();
        LocalTime currentTime = openingTime;

        while (currentTime.plusMinutes(serviceDuration).isBefore(closingTime) ||
                currentTime.plusMinutes(serviceDuration).equals(closingTime)) {

            LocalTime finalCurrentTime = currentTime;
            boolean isAvailable = existingBookings.stream()
                    .noneMatch(booking -> isTimeConflict(
                            finalCurrentTime,
                            finalCurrentTime.plusMinutes(serviceDuration),
                            booking.getBookingDateTime().toLocalTime(),
                            booking.getEndDateTime().toLocalTime()));

            if (isAvailable) {
                slots.add(currentTime);
            }

            currentTime = currentTime.plusMinutes(30); // Slots de 30 minutos
        }

        return slots;
    }

    private boolean isTimeConflict(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .customerName(booking.getCustomer().getName())
                .professionalName(booking.getProfessional().getUser().getName())
                .serviceName(booking.getService().getName())
                .establishmentName(booking.getEstablishment().getName())
                .bookingDateTime(booking.getBookingDateTime())
                .endDateTime(booking.getEndDateTime())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .paymentStatus(booking.getPaymentStatus())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    private <T> PagedResponse<T> createPagedResponse(Page<?> page, List<T> content) {
        return PagedResponse.<T>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}