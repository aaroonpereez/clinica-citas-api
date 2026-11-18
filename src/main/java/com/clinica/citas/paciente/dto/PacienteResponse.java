package com.clinica.citas.paciente.dto;

import com.clinica.citas.paciente.Paciente;

import java.time.LocalDate;

public record PacienteResponse(
        Long id,
        String nombre,
        String apellidos,
        String dni,
        String email,
        String telefono,
        LocalDate fechaNacimiento
) {
    public static PacienteResponse from(Paciente p) {
        return new PacienteResponse(p.getId(), p.getNombre(), p.getApellidos(), p.getDni(),
                p.getEmail(), p.getTelefono(), p.getFechaNacimiento());
    }
}
