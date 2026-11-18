package com.clinica.citas.cita;

import com.clinica.citas.medico.Medico;
import com.clinica.citas.paciente.Paciente;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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

    public Long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public Medico getMedico() { return medico; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFin() { return fin; }
    public EstadoCita getEstado() { return estado; }
    public String getMotivo() { return motivo; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }
}
