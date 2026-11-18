package com.clinica.citas.medico.dto;

import com.clinica.citas.medico.Especialidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MedicoRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 150) String apellidos,
        @NotBlank @Pattern(regexp = "\\d{6,9}", message = "debe tener entre 6 y 9 dígitos") String numeroColegiado,
        @NotNull Especialidad especialidad
) {
}
