package com.clinica.citas.paciente;

import com.clinica.citas.common.exception.ConflictException;
import com.clinica.citas.common.exception.ResourceNotFoundException;
import com.clinica.citas.paciente.dto.PacienteRequest;
import com.clinica.citas.paciente.dto.PacienteResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    PacienteRepository repository;

    @InjectMocks
    PacienteService service;

    private final PacienteRequest request = new PacienteRequest(
            "Lucía", "García López", "12345678z", "lucia@example.com", "600123123", LocalDate.of(1990, 5, 12));

    @Test
    void crear_guardaPacienteConDniEnMayusculas() {
        when(repository.existsByDni("12345678Z")).thenReturn(false);
        when(repository.existsByEmail("lucia@example.com")).thenReturn(false);
        when(repository.save(any(Paciente.class))).thenAnswer(inv -> inv.getArgument(0));

        PacienteResponse resultado = service.crear(request);

        assertThat(resultado.dni()).isEqualTo("12345678Z");
        assertThat(resultado.nombre()).isEqualTo("Lucía");
    }

    @Test
    void crear_lanzaConflictSiElDniYaExiste() {
        when(repository.existsByDni("12345678Z")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("DNI");
        verify(repository, never()).save(any());
    }

    @Test
    void obtener_lanzaNotFoundSiNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
