package com.clinica.citas.medico;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    boolean existsByNumeroColegiado(String numeroColegiado);

    Page<Medico> findByEspecialidad(Especialidad especialidad, Pageable pageable);

    /**
     * Carga el médico bloqueando su fila (SELECT ... FOR UPDATE) hasta que acabe la transacción.
     * Así dos reservas simultáneas para el mismo médico se procesan una detrás de otra
     * y no pueden colarse dos citas en el mismo hueco.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Medico m where m.id = :id")
    Optional<Medico> findByIdForUpdate(@Param("id") Long id);
}
