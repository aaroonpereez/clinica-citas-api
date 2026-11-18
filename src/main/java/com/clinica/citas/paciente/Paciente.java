package com.clinica.citas.paciente;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 9)
    private String dni;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String telefono;

    private LocalDate fechaNacimiento;

    @Column(nullable = false, updatable = false)
    private Instant creadoEn;

    protected Paciente() {
        // requerido por JPA
    }

    public Paciente(String nombre, String apellidos, String dni, String email,
                    String telefono, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    @PrePersist
    void prePersist() {
        this.creadoEn = Instant.now();
    }

    public void actualizar(String nombre, String apellidos, String email,
                           String telefono, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getDni() { return dni; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public Instant getCreadoEn() { return creadoEn; }
}
