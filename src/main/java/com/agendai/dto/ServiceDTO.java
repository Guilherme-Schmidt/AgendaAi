package com.agendai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;

    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Preço deve ter no máximo 8 dígitos inteiros e 2 decimais")
    private BigDecimal price;

    @Min(value = 1, message = "Duração deve ser maior que zero")
    private Integer durationMinutes;

    @NotNull(message = "ID do estabelecimento é obrigatório")
    private Long establishmentId;

    @NotNull(message = "ID da categoria é obrigatório")
    private Long categoryId;

    @Builder.Default
    private Boolean active = true;
}