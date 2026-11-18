package com.clinica.citas.cita;

import com.clinica.citas.cita.dto.CitaRequest;
import com.clinica.citas.cita.dto.CitaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Citas")
public class CitaController {

    private final CitaService service;

    public CitaController(CitaService service) {
        this.service = service;
    }

    @PostMapping("/citas")
    @Operation(summary = "Reservar una cita",
            description = "Falla con 409 si el médico o el paciente ya tienen una cita que se solape")
    public ResponseEntity<CitaResponse> crear(@Valid @RequestBody CitaRequest request) {
        CitaResponse creada = service.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @GetMapping("/citas/{id}")
    @Operation(summary = "Obtener una cita")
    public CitaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PatchMapping("/citas/{id}/confirmar")
    @Operation(summary = "Confirmar una cita programada")
    public CitaResponse confirmar(@PathVariable Long id) {
        return service.confirmar(id);
    }

    @PatchMapping("/citas/{id}/cancelar")
    @Operation(summary = "Cancelar una cita (solo antes de que empiece)")
    public CitaResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @PatchMapping("/citas/{id}/completar")
    @Operation(summary = "Marcar una cita como completada (solo una vez empezada)")
    public CitaResponse completar(@PathVariable Long id) {
        return service.completar(id);
    }

    @PatchMapping("/citas/{id}/no-presentado")
    @Operation(summary = "Marcar que el paciente no se presentó (solo una vez empezada)")
    public CitaResponse marcarNoPresentado(@PathVariable Long id) {
        return service.marcarNoPresentado(id);
    }

    @GetMapping("/medicos/{medicoId}/agenda")
    @Operation(summary = "Agenda de un médico para un día")
    public List<CitaResponse> agenda(@PathVariable Long medicoId,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.agendaMedico(medicoId, fecha);
    }

    @GetMapping("/pacientes/{pacienteId}/citas")
    @Operation(summary = "Historial de citas de un paciente (más recientes primero)")
    public List<CitaResponse> citasPaciente(@PathVariable Long pacienteId) {
        return service.citasPaciente(pacienteId);
    }
}
