package com.clinica.citas.cita;

import com.clinica.citas.cita.dto.CitaRequest;
import com.clinica.citas.cita.dto.CitaResponse;
import com.clinica.citas.common.exception.ConflictException;
import com.clinica.citas.common.exception.InvalidRequestException;
import com.clinica.citas.common.exception.ResourceNotFoundException;
import com.clinica.citas.medico.Especialidad;
import com.clinica.citas.medico.Medico;
import com.clinica.citas.medico.MedicoRepository;
import com.clinica.citas.paciente.Paciente;
import com.clinica.citas.paciente.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    /** "Ahora" en los tests: 1 de diciembre de 2026 a las 09:00. */
    static final LocalDateTime AHORA = LocalDateTime.of(2026, 12, 1, 9, 0);

    @Mock CitaRepository citaRepository;
    @Mock PacienteRepository pacienteRepository;
    @Mock MedicoRepository medicoRepository;

    CitaService service;

    final Paciente paciente = new Paciente("Lucía", "García", "12345678Z", "lucia@example.com", null, null);
    final Medico medico = new Medico("Carlos", "Martín", "280912345", Especialidad.CARDIOLOGIA);

    final LocalDateTime inicio = AHORA.plusDays(1);
    final LocalDateTime fin = inicio.plusMinutes(30);

    @BeforeEach
    void setUp() {
        ZoneId zona = ZoneId.of("Europe/Madrid");
        Clock clock = Clock.fixed(AHORA.atZone(zona).toInstant(), zona);
        service = new CitaService(citaRepository, pacienteRepository, medicoRepository, clock);
    }

    private CitaRequest request(LocalDateTime inicio, LocalDateTime fin) {
        return new CitaRequest(1L, 2L, inicio, fin, "Revisión");
    }

    private void pacienteYMedicoExisten() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(medico));
    }

    @Test
    void crear_guardaCitaProgramada() {
        pacienteYMedicoExisten();
        when(citaRepository.existeSolapeMedico(2L, inicio, fin)).thenReturn(false);
        when(citaRepository.existeSolapePaciente(1L, inicio, fin)).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        CitaResponse resultado = service.crear(request(inicio, fin));

        assertThat(resultado.estado()).isEqualTo(EstadoCita.PROGRAMADA);
        assertThat(resultado.medico()).isEqualTo("Carlos Martín");
        assertThat(resultado.inicio()).isEqualTo(inicio);
    }

    @Test
    void crear_rechazaFinAnteriorAlInicio() {
        assertThatThrownBy(() -> service.crear(request(inicio, inicio.minusMinutes(10))))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("fin");
    }

    @Test
    void crear_rechazaFechasPasadas() {
        LocalDateTime ayer = AHORA.minusDays(1);

        assertThatThrownBy(() -> service.crear(request(ayer, ayer.plusMinutes(30))))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("futura");
    }

    @Test
    void crear_lanzaNotFoundSiNoExisteElMedico() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findByIdForUpdate(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.crear(request(inicio, fin)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void crear_rechazaMedicoDadoDeBaja() {
        medico.desactivar();
        pacienteYMedicoExisten();

        assertThatThrownBy(() -> service.crear(request(inicio, fin)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("baja");
        verify(citaRepository, never()).save(any());
    }

    @Test
    void crear_rechazaSolapeConOtraCitaDelMedico() {
        pacienteYMedicoExisten();
        when(citaRepository.existeSolapeMedico(2L, inicio, fin)).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request(inicio, fin)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("médico");
        verify(citaRepository, never()).save(any());
    }

    @Test
    void crear_rechazaSolapeConOtraCitaDelPaciente() {
        pacienteYMedicoExisten();
        when(citaRepository.existeSolapeMedico(2L, inicio, fin)).thenReturn(false);
        when(citaRepository.existeSolapePaciente(1L, inicio, fin)).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request(inicio, fin)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("paciente");
        verify(citaRepository, never()).save(any());
    }

    @Test
    void cancelar_cambiaElEstadoSiLaCitaEsFutura() {
        Cita cita = new Cita(paciente, medico, inicio, fin, null);
        when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));

        CitaResponse resultado = service.cancelar(5L);

        assertThat(resultado.estado()).isEqualTo(EstadoCita.CANCELADA);
    }

    @Test
    void cancelar_rechazaCitasQueYaHanEmpezado() {
        Cita cita = new Cita(paciente, medico, AHORA.minusMinutes(10), AHORA.plusMinutes(20), null);
        when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));

        assertThatThrownBy(() -> service.cancelar(5L)).isInstanceOf(ConflictException.class);
        assertThat(cita.getEstado()).isEqualTo(EstadoCita.PROGRAMADA);
    }

    @Test
    void completar_rechazaCitasQueAunNoHanEmpezado() {
        Cita cita = new Cita(paciente, medico, inicio, fin, null);
        when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));

        assertThatThrownBy(() -> service.completar(5L)).isInstanceOf(ConflictException.class);
    }
}
