package com.clinica.citas.cita;

import org.springframework.data.jpa.repository.EntityGraph;
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

    /** Igual que {@link #existeSolapeMedico}, pero para el paciente. */
    @Query("""
            select count(c) > 0 from Cita c
            where c.paciente.id = :pacienteId
              and c.estado <> com.clinica.citas.cita.EstadoCita.CANCELADA
              and c.inicio < :fin
              and c.fin > :inicio
            """)
    boolean existeSolapePaciente(@Param("pacienteId") Long pacienteId,
                                 @Param("inicio") LocalDateTime inicio,
                                 @Param("fin") LocalDateTime fin);

    /** Citas de un médico que empiezan en [desde, hasta). Trae paciente y médico en la misma consulta. */
    @EntityGraph(attributePaths = {"paciente", "medico"})
    @Query("""
            select c from Cita c
            where c.medico.id = :medicoId
              and c.inicio >= :desde
              and c.inicio < :hasta
            order by c.inicio
            """)
    List<Cita> findAgendaMedico(@Param("medicoId") Long medicoId,
                                @Param("desde") LocalDateTime desde,
                                @Param("hasta") LocalDateTime hasta);

    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<Cita> findByPacienteIdOrderByInicioDesc(Long pacienteId);

    boolean existsByPacienteId(Long pacienteId);
}
