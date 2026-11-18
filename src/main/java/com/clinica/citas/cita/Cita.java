package com.clinica.citas.cita;

import com.clinica.citas.common.exception.ConflictException;
import com.clinica.citas.medico.Medico;
import com.clinica.citas.paciente.Paciente;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Cita entre un paciente y un médico.
 * Las transiciones de estado viven aquí para que las reglas no se puedan saltar desde fuera:
 *
 * <pre>
 * PROGRAMADA ─confirmar─▶ CONFIRMADA
 *     │                       │
 *     ├──── cancelar ─────────┼──▶ CANCELADA      (solo antes de que empiece)
 *     ├──── completar ────────┼──▶ COMPLETADA     (solo una vez empezada)
 *     └──── noPresentado ─────┴──▶ NO_PRESENTADO  (solo una vez empezada)
 * </pre>
 */
@Entity
@Table(name = "citas", indexes = {
        @Index(name = "idx_cita_medico_inicio", columnList = "medico_id, inicio"),
        @Index(name = "idx_cita_paciente_inicio", columnList = "paciente_id, inicio")
})
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @Column(length = 500)
    private String motivo;

    protected Cita() {
        // requerido por JPA
    }

    public Cita(Paciente paciente, Medico medico, LocalDateTime inicio, LocalDateTime fin, String motivo) {
        this.paciente = paciente;
        this.medico = medico;
        this.inicio = inicio;
        this.fin = fin;
        this.motivo = motivo;
    }

    public void confirmar() {
        if (estado != EstadoCita.PROGRAMADA) {
            throw new ConflictException("Solo se puede confirmar una cita PROGRAMADA (estado actual: " + estado + ")");
        }
        estado = EstadoCita.CONFIRMADA;
    }

    public void cancelar(LocalDateTime ahora) {
        exigirPendiente("cancelar");
        if (!inicio.isAfter(ahora)) {
            throw new ConflictException("No se puede cancelar una cita que ya ha empezado");
        }
        estado = EstadoCita.CANCELADA;
    }

    public void completar(LocalDateTime ahora) {
        exigirPendiente("completar");
        exigirEmpezada(ahora, "completar");
        estado = EstadoCita.COMPLETADA;
    }

    public void marcarNoPresentado(LocalDateTime ahora) {
        exigirPendiente("marcar como no presentada");
        exigirEmpezada(ahora, "marcar como no presentada");
        estado = EstadoCita.NO_PRESENTADO;
    }

    private void exigirPendiente(String accion) {
        if (estado != EstadoCita.PROGRAMADA && estado != EstadoCita.CONFIRMADA) {
            throw new ConflictException("No se puede " + accion + " una cita en estado " + estado);
        }
    }

    private void exigirEmpezada(LocalDateTime ahora, String accion) {
        if (inicio.isAfter(ahora)) {
            throw new ConflictException("No se puede " + accion + " una cita que aún no ha empezado");
        }
    }

    public Long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public Medico getMedico() { return medico; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFin() { return fin; }
    public EstadoCita getEstado() { return estado; }
    public String getMotivo() { return motivo; }
}
