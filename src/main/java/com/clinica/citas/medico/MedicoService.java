package com.clinica.citas.medico;

import com.clinica.citas.common.exception.ConflictException;
import com.clinica.citas.common.exception.ResourceNotFoundException;
import com.clinica.citas.medico.dto.MedicoRequest;
import com.clinica.citas.medico.dto.MedicoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MedicoService {

    private final MedicoRepository repository;

    public MedicoService(MedicoRepository repository) {
        this.repository = repository;
    }

    public Page<MedicoResponse> listar(Especialidad especialidad, Pageable pageable) {
        Page<Medico> medicos = especialidad == null
                ? repository.findAll(pageable)
                : repository.findByEspecialidad(especialidad, pageable);
        return medicos.map(MedicoResponse::from);
    }

    public MedicoResponse obtener(Long id) {
        return MedicoResponse.from(buscar(id));
    }

    @Transactional
    public MedicoResponse crear(MedicoRequest req) {
        if (repository.existsByNumeroColegiado(req.numeroColegiado())) {
            throw new ConflictException("Ya existe un médico con número de colegiado " + req.numeroColegiado());
        }
        Medico medico = new Medico(req.nombre(), req.apellidos(), req.numeroColegiado(), req.especialidad());
        return MedicoResponse.from(repository.save(medico));
    }

    @Transactional
    public MedicoResponse actualizar(Long id, MedicoRequest req) {
        Medico medico = buscar(id);
        // El número de colegiado identifica al médico y no se modifica.
        medico.actualizar(req.nombre(), req.apellidos(), req.especialidad());
        return MedicoResponse.from(medico);
    }

    /** Baja lógica: el médico tiene citas asociadas, así que no se borra de la base de datos. */
    @Transactional
    public void desactivar(Long id) {
        buscar(id).desactivar();
    }

    @Transactional
    public MedicoResponse activar(Long id) {
        Medico medico = buscar(id);
        medico.activar();
        return MedicoResponse.from(medico);
    }

    private Medico buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico", id));
    }
}
