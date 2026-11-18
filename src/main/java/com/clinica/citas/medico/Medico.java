package com.clinica.citas.medico;

import jakarta.persistence.*;

@Entity
@Table(name = "medicos")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 20)
    private String numeroColegiado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Especialidad especialidad;

    @Column(nullable = false)
    private boolean activo = true;

    protected Medico() {
        // requerido por JPA
    }

    public Medico(String nombre, String apellidos, String numeroColegiado, Especialidad especialidad) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.numeroColegiado = numeroColegiado;
        this.especialidad = especialidad;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getNumeroColegiado() { return numeroColegiado; }
    public Especialidad getEspecialidad() { return especialidad; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
