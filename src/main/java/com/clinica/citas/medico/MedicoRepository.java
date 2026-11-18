package com.clinica.citas.medico;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    boolean existsByNumeroColegiado(String numeroColegiado);

    List<Medico> findByEspecialidadAndActivoTrue(Especialidad especialidad);
}
