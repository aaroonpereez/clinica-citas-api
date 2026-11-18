package com.clinica.citas.paciente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PacienteRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 150) String apellidos,
        @NotBlank @Pattern(regexp = "\\d{8}[A-Za-z]", message = "debe tener 8 números y una letra") String dni,
        @NotBlank @Email String email,
        @Pattern(regexp = "\\+?\\d{9,15}", message = "teléfono no válido") String telefono,
        @Past LocalDate fechaNacimiento
) {
}
