package com.clinica.citas.paciente;

import com.clinica.citas.common.exception.ConflictException;
import com.clinica.citas.common.exception.ResourceNotFoundException;
import com.clinica.citas.paciente.dto.PacienteRequest;
import com.clinica.citas.paciente.dto.PacienteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    public Page<PacienteResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(PacienteResponse::from);
    }

    public PacienteResponse obtener(Long id) {
        return PacienteResponse.from(buscar(id));
    }

    @Transactional
    public PacienteResponse crear(PacienteRequest req) {
        String dni = req.dni().toUpperCase();
        if (repository.existsByDni(dni)) {
            throw new ConflictException("Ya existe un paciente con DNI " + dni);
        }
        if (repository.existsByEmail(req.email())) {
            throw new ConflictException("Ya existe un paciente con email " + req.email());
        }
        Paciente paciente = new Paciente(req.nombre(), req.apellidos(), dni, req.email(),
                req.telefono(), req.fechaNacimiento());
        return PacienteResponse.from(repository.save(paciente));
    }

    @Transactional
    public PacienteResponse actualizar(Long id, PacienteRequest req) {
        Paciente paciente = buscar(id);
        if (repository.existsByEmailAndIdNot(req.email(), id)) {
            throw new ConflictException("Ya existe un paciente con email " + req.email());
        }
        // El DNI no se modifica: identifica al paciente.
        paciente.actualizar(req.nombre(), req.apellidos(), req.email(), req.telefono(), req.fechaNacimiento());
        return PacienteResponse.from(paciente);
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(buscar(id));
    }

    private Paciente buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
    }
}
