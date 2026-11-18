package com.clinica.citas.medico;

import com.clinica.citas.medico.dto.MedicoRequest;
import com.clinica.citas.medico.dto.MedicoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/medicos")
@Tag(name = "Médicos")
public class MedicoController {

    private final MedicoService service;

    public MedicoController(MedicoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar médicos (paginado), opcionalmente por especialidad")
    public Page<MedicoResponse> listar(
            @RequestParam(required = false) Especialidad especialidad,
            @PageableDefault(size = 20, sort = "apellidos", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.listar(especialidad, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un médico")
    public MedicoResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Dar de alta un médico")
    public ResponseEntity<MedicoResponse> crear(@Valid @RequestBody MedicoRequest request) {
        MedicoResponse creado = service.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un médico (el número de colegiado no cambia)")
    public MedicoResponse actualizar(@PathVariable Long id, @Valid @RequestBody MedicoRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Dar de baja un médico (lo desactiva, no lo borra)")
    public void desactivar(@PathVariable Long id) {
        service.desactivar(id);
    }

    @PatchMapping("/{id}/activar")
    @Operation(summary = "Reactivar un médico dado de baja")
    public MedicoResponse activar(@PathVariable Long id) {
        return service.activar(id);
    }
}
