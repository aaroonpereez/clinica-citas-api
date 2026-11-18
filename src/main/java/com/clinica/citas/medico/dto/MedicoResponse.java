package com.clinica.citas.medico.dto;

import com.clinica.citas.medico.Especialidad;
import com.clinica.citas.medico.Medico;

public record MedicoResponse(
        Long id,
        String nombre,
        String apellidos,
        String numeroColegiado,
        Especialidad especialidad,
        boolean activo
) {
    public static MedicoResponse from(Medico m) {
        return new MedicoResponse(m.getId(), m.getNombre(), m.getApellidos(),
                m.getNumeroColegiado(), m.getEspecialidad(), m.isActivo());
    }
}
