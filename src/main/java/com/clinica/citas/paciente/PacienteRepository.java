package com.clinica.citas.paciente;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
