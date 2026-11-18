package com.clinica.citas.cita.dto;

import com.clinica.citas.cita.Cita;
import com.clinica.citas.cita.EstadoCita;
import com.clinica.citas.medico.Especialidad;
import com.clinica.citas.medico.Medico;
import com.clinica.citas.paciente.Paciente;

import java.time.LocalDateTime;

public record CitaResponse(
        Long id,
        Long pacienteId,
        String paciente,
        Long medicoId,
        String medico,
        Especialidad especialidad,
        LocalDateTime inicio,
        LocalDateTime fin,
        EstadoCita estado,
        String motivo
) {
    public static CitaResponse from(Cita c) {
        Paciente p = c.getPaciente();
        Medico m = c.getMedico();
        return new CitaResponse(c.getId(),
                p.getId(), p.getNombre() + " " + p.getApellidos(),
                m.getId(), m.getNombreCompleto(), m.getEspecialidad(),
                c.getInicio(), c.getFin(), c.getEstado(), c.getMotivo());
    }
}
