package com.clinica.citas.cita;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Indica si el médico tiene alguna cita no cancelada que se solape con [inicio, fin).
     * Dos intervalos se solapan si cada uno empieza antes de que termine el otro.
     */
    @Query("""
            select count(c) > 0 from Cita c
            where c.medico.id = :medicoId
              and c.estado <> com.clinica.citas.cita.EstadoCita.CANCELADA
              and c.inicio < :fin
              and c.fin > :inicio
            """)
    boolean existeSolapeMedico(@Param("medicoId") Long medicoId,
                               @Param("inicio") LocalDateTime inicio,
                               @Param("fin") LocalDateTime fin);

    List<Cita> findByMedicoIdAndInicioBetweenOrderByInicio(Long medicoId, LocalDateTime desde, LocalDateTime hasta);

    List<Cita> findByPacienteIdOrderByInicioDesc(Long pacienteId);
}
