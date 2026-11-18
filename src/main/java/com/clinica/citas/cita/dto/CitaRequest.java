package com.clinica.citas.cita.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CitaRequest(
        @NotNull Long pacienteId,
        @NotNull Long medicoId,
        @NotNull @Schema(example = "2026-12-15T10:00:00") LocalDateTime inicio,
        @NotNull @Schema(example = "2026-12-15T10:30:00") LocalDateTime fin,
        @Size(max = 500) String motivo
) {
}
