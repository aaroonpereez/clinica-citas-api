package com.clinica.citas.medico;

import com.clinica.citas.common.exception.ConflictException;
import com.clinica.citas.common.exception.ResourceNotFoundException;
import com.clinica.citas.medico.dto.MedicoRequest;
import com.clinica.citas.medico.dto.MedicoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicoServiceTest {

    @Mock
    MedicoRepository repository;

    @InjectMocks
    MedicoService service;

    private final MedicoRequest request =
            new MedicoRequest("Carlos", "Martín Sanz", "280912345", Especialidad.CARDIOLOGIA);

    @Test
    void crear_guardaMedicoActivo() {
        when(repository.existsByNumeroColegiado("280912345")).thenReturn(false);
        when(repository.save(any(Medico.class))).thenAnswer(inv -> inv.getArgument(0));

        MedicoResponse resultado = service.crear(request);

        assertThat(resultado.especialidad()).isEqualTo(Especialidad.CARDIOLOGIA);
        assertThat(resultado.activo()).isTrue();
    }

    @Test
    void crear_lanzaConflictSiElColegiadoYaExiste() {
        when(repository.existsByNumeroColegiado("280912345")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request)).isInstanceOf(ConflictException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void desactivar_marcaElMedicoComoInactivo() {
        Medico medico = new Medico("Carlos", "Martín Sanz", "280912345", Especialidad.CARDIOLOGIA);
        when(repository.findById(1L)).thenReturn(Optional.of(medico));

        service.desactivar(1L);

        assertThat(medico.isActivo()).isFalse();
    }

    @Test
    void actualizar_noCambiaElNumeroDeColegiado() {
        Medico medico = new Medico("Carlos", "Martín Sanz", "280912345", Especialidad.CARDIOLOGIA);
        when(repository.findById(1L)).thenReturn(Optional.of(medico));

        MedicoResponse resultado = service.actualizar(1L,
                new MedicoRequest("Carlos", "Martín Sanz", "999999999", Especialidad.PEDIATRIA));

        assertThat(resultado.numeroColegiado()).isEqualTo("280912345");
        assertThat(resultado.especialidad()).isEqualTo(Especialidad.PEDIATRIA);
    }

    @Test
    void obtener_lanzaNotFoundSiNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(99L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
