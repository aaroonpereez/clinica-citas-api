package com.clinica.citas.cita;

import com.clinica.citas.common.exception.ConflictException;
import com.clinica.citas.medico.Especialidad;
import com.clinica.citas.medico.Medico;
import com.clinica.citas.paciente.Paciente;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Tests de las transiciones de estado de la entidad, sin Spring ni mocks. */
class CitaTest {

    static final LocalDateTime AHORA = LocalDateTime.of(2026, 12, 1, 9, 0);

    private Cita citaQueEmpieza(LocalDateTime inicio) {
        Paciente paciente = new Paciente("Lucía", "García", "12345678Z", "lucia@example.com", null, null);
        Medico medico = new Medico("Carlos", "Martín", "280912345", Especialidad.CARDIOLOGIA);
        return new Cita(paciente, medico, inicio, inicio.plusMinutes(30), null);
    }

    @Test
    void confirmar_pasaDeProgramadaAConfirmada() {
        Cita cita = citaQueEmpieza(AHORA.plusDays(1));

        cita.confirmar();

        assertThat(cita.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
    }

    @Test
    void confirmar_dosVecesFalla() {
        Cita cita = citaQueEmpieza(AHORA.plusDays(1));
        cita.confirmar();

        assertThatThrownBy(cita::confirmar).isInstanceOf(ConflictException.class);
    }

    @Test
    void unaCitaConfirmadaSePuedeCancelar() {
        Cita cita = citaQueEmpieza(AHORA.plusDays(1));
        cita.confirmar();

        cita.cancelar(AHORA);

        assertThat(cita.getEstado()).isEqualTo(EstadoCita.CANCELADA);
    }

    @Test
    void unaCitaCanceladaNoSePuedeCompletar() {
        Cita cita = citaQueEmpieza(AHORA.plusHours(1));
        cita.cancelar(AHORA);

        assertThatThrownBy(() -> cita.completar(AHORA.plusHours(2))).isInstanceOf(ConflictException.class);
    }

    @Test
    void completar_funcionaUnaVezEmpezada() {
        Cita cita = citaQueEmpieza(AHORA.minusMinutes(5));

        cita.completar(AHORA);

        assertThat(cita.getEstado()).isEqualTo(EstadoCita.COMPLETADA);
    }

    @Test
    void marcarNoPresentado_rechazaCitasFuturas() {
        Cita cita = citaQueEmpieza(AHORA.plusDays(1));

        assertThatThrownBy(() -> cita.marcarNoPresentado(AHORA)).isInstanceOf(ConflictException.class);
    }
}
