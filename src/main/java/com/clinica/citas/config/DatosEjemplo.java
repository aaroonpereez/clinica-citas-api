package com.clinica.citas.config;

import com.clinica.citas.cita.Cita;
import com.clinica.citas.cita.CitaRepository;
import com.clinica.citas.medico.Especialidad;
import com.clinica.citas.medico.Medico;
import com.clinica.citas.medico.MedicoRepository;
import com.clinica.citas.paciente.Paciente;
import com.clinica.citas.paciente.PacienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Carga médicos, pacientes y citas de ejemplo al arrancar, solo si la base de datos está vacía.
 * Se desactiva con {@code app.datos-ejemplo=false}. Todos los datos son ficticios.
 */
@Component
@ConditionalOnProperty(name = "app.datos-ejemplo", havingValue = "true")
public class DatosEjemplo implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosEjemplo.class);

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository;
    private final Clock clock;

    public DatosEjemplo(PacienteRepository pacienteRepository, MedicoRepository medicoRepository,
                        CitaRepository citaRepository, Clock clock) {
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (pacienteRepository.count() > 0 || medicoRepository.count() > 0) {
            log.info("La base de datos ya tiene datos: no se cargan los de ejemplo");
            return;
        }

        List<Medico> medicos = medicoRepository.saveAll(List.of(
                new Medico("Carmen", "Ortega Ruiz", "280911111", Especialidad.MEDICINA_GENERAL),
                new Medico("Javier", "Molina Pérez", "280922222", Especialidad.CARDIOLOGIA),
                new Medico("Elena", "Navarro Gil", "280933333", Especialidad.PEDIATRIA),
                new Medico("Sergio", "Romero Díaz", "280944444", Especialidad.TRAUMATOLOGIA),
                new Medico("Laura", "Castillo Vega", "280955555", Especialidad.DERMATOLOGIA)));

        List<Paciente> pacientes = pacienteRepository.saveAll(List.of(
                new Paciente("Lucía", "García López", "12345678Z", "lucia.garcia@example.com", "600111222", LocalDate.of(1990, 5, 12)),
                new Paciente("Pablo", "Sánchez Mora", "11111111H", "pablo.sanchez@example.com", "600333444", LocalDate.of(1985, 11, 3)),
                new Paciente("Marta", "Jiménez Rey", "22222222J", "marta.jimenez@example.com", null, LocalDate.of(2018, 2, 20)),
                new Paciente("Andrés", "Herrera Soto", "33333333P", "andres.herrera@example.com", "600555666", LocalDate.of(1972, 8, 30)),
                new Paciente("Sofía", "Domínguez Cano", "44444444A", "sofia.dominguez@example.com", "600777888", LocalDate.of(1999, 1, 15)),
                new Paciente("Diego", "Vázquez León", "55555555K", "diego.vazquez@example.com", null, LocalDate.of(1964, 6, 7))));

        LocalDateTime ahora = LocalDateTime.now(clock);
        LocalDate manana = ahora.toLocalDate().plusDays(1);
        LocalDate pasadoManana = ahora.toLocalDate().plusDays(2);
        LocalDate haceUnaSemana = ahora.toLocalDate().minusDays(7);

        Medico general = medicos.get(0);
        Medico cardio = medicos.get(1);
        Medico pediatra = medicos.get(2);

        // Agenda de mañana de la médica de cabecera: tres citas seguidas.
        Cita c1 = cita(pacientes.get(0), general, manana, "09:00", "Revisión anual");
        Cita c2 = cita(pacientes.get(1), general, manana, "09:30", "Dolor de garganta");
        Cita c3 = cita(pacientes.get(3), general, manana, "10:00", "Resultados de analítica");
        c1.confirmar();

        Cita c4 = cita(pacientes.get(3), cardio, pasadoManana, "11:00", "Control de tensión");
        Cita c5 = cita(pacientes.get(2), pediatra, pasadoManana, "17:00", "Vacunación");

        // Una cita cancelada y dos ya pasadas, para que se vean todos los estados.
        Cita c6 = cita(pacientes.get(4), cardio, pasadoManana, "12:00", "Palpitaciones");
        c6.cancelar(ahora);
        Cita c7 = cita(pacientes.get(0), cardio, haceUnaSemana, "10:00", "Electrocardiograma");
        c7.completar(ahora);
        Cita c8 = cita(pacientes.get(5), general, haceUnaSemana, "12:30", "Renovar receta");
        c8.marcarNoPresentado(ahora);

        citaRepository.saveAll(List.of(c1, c2, c3, c4, c5, c6, c7, c8));
        log.info("Datos de ejemplo cargados: {} médicos, {} pacientes y 8 citas", medicos.size(), pacientes.size());
    }

    private static Cita cita(Paciente paciente, Medico medico, LocalDate dia, String hora, String motivo) {
        LocalDateTime inicio = dia.atTime(LocalTime.parse(hora));
        return new Cita(paciente, medico, inicio, inicio.plusMinutes(30), motivo);
    }
}
