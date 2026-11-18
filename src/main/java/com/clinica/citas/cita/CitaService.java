package com.clinica.citas.cita;

import com.clinica.citas.cita.dto.CitaRequest;
import com.clinica.citas.cita.dto.CitaResponse;
import com.clinica.citas.common.exception.ConflictException;
import com.clinica.citas.common.exception.InvalidRequestException;
import com.clinica.citas.common.exception.ResourceNotFoundException;
import com.clinica.citas.medico.Medico;
import com.clinica.citas.medico.MedicoRepository;
import com.clinica.citas.paciente.Paciente;
import com.clinica.citas.paciente.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

@Service
@Transactional(readOnly = true)
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    /** Se inyecta el reloj para poder fijar "ahora" en los tests. */
    private final Clock clock;

    public CitaService(CitaRepository citaRepository, PacienteRepository pacienteRepository,
                       MedicoRepository medicoRepository, Clock clock) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.clock = clock;
    }

    @Transactional
    public CitaResponse crear(CitaRequest req) {
        if (!req.fin().isAfter(req.inicio())) {
            throw new InvalidRequestException("La hora de fin debe ser posterior a la de inicio");
        }
        if (!req.inicio().isAfter(ahora())) {
            throw new InvalidRequestException("La cita debe ser en una fecha futura");
        }

        Paciente paciente = pacienteRepository.findById(req.pacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", req.pacienteId()));
        // Bloquea la fila del médico: dos reservas simultáneas para él se procesan en serie.
        Medico medico = medicoRepository.findByIdForUpdate(req.medicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico", req.medicoId()));

        if (!medico.isActivo()) {
            throw new ConflictException("El médico está dado de baja y no admite citas");
        }
        if (citaRepository.existeSolapeMedico(req.medicoId(), req.inicio(), req.fin())) {
            throw new ConflictException("El médico ya tiene una cita en ese horario");
        }
        if (citaRepository.existeSolapePaciente(req.pacienteId(), req.inicio(), req.fin())) {
            throw new ConflictException("El paciente ya tiene otra cita en ese horario");
        }

        Cita cita = new Cita(paciente, medico, req.inicio(), req.fin(), req.motivo());
        return CitaResponse.from(citaRepository.save(cita));
    }

    public CitaResponse obtener(Long id) {
        return CitaResponse.from(buscar(id));
    }

    public List<CitaResponse> agendaMedico(Long medicoId, LocalDate fecha) {
        if (!medicoRepository.existsById(medicoId)) {
            throw new ResourceNotFoundException("Médico", medicoId);
        }
        return citaRepository.findAgendaMedico(medicoId, fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay())
                .stream().map(CitaResponse::from).toList();
    }

    public List<CitaResponse> citasPaciente(Long pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente", pacienteId);
        }
        return citaRepository.findByPacienteIdOrderByInicioDesc(pacienteId)
                .stream().map(CitaResponse::from).toList();
    }

    @Transactional
    public CitaResponse confirmar(Long id) {
        Cita cita = buscar(id);
        cita.confirmar();
        return CitaResponse.from(cita);
    }

    @Transactional
    public CitaResponse cancelar(Long id) {
        return cambiarEstado(id, Cita::cancelar);
    }

    @Transactional
    public CitaResponse completar(Long id) {
        return cambiarEstado(id, Cita::completar);
    }

    @Transactional
    public CitaResponse marcarNoPresentado(Long id) {
        return cambiarEstado(id, Cita::marcarNoPresentado);
    }

    private CitaResponse cambiarEstado(Long id, BiConsumer<Cita, LocalDateTime> transicion) {
        Cita cita = buscar(id);
        transicion.accept(cita, ahora());
        return CitaResponse.from(cita);
    }

    private Cita buscar(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", id));
    }

    private LocalDateTime ahora() {
        return LocalDateTime.now(clock);
    }
}
