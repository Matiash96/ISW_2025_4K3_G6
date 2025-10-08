package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data.InscripcionDTO;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data.VisitanteDTO;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.*;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.ActividadProgramadaRepository;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.InscripcionRepository;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.VisitanteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class InscripcionServiceTest {
    @InjectMocks
    private InscripcionService inscripcionService;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private VisitanteRepository visitanteRepository;

    @Mock
    private ActividadProgramadaRepository actividadProgramadaRepository;

    @Test
    public void testInscribirseCumpliendoLoNecesario() {

        // Fabricamos la inscripción que va a recibir por parámetro el service
        final Long actividadProgramadaId = 3L;
        VisitanteDTO visitanteDTO1 = new VisitanteDTO("Johnny Lojuno", 44444444, LocalDate.of(2002, 01, 01), 1L); // Asumo que el DTO usa Long para DNI y ID de Talla
        VisitanteDTO visitanteDTO2 = new VisitanteDTO("Johnny Loconozco", 44444443, LocalDate.of(2002, 01, 01), 1L);
        List<VisitanteDTO> participantesDTO = List.of(visitanteDTO1, visitanteDTO2);
        InscripcionDTO inscripcionDTO = new InscripcionDTO(
                LocalDateTime.of(2025, 10, 7, 20, 30, 15),
                actividadProgramadaId,
                true,
                participantesDTO
        );

        // Fabricamos las entidades con las que luego vamos a contrastar si se hizo bien
        Actividad actividadBase = new Actividad(2L, "Tirolesa", true, "Los menores...");
        Talla tallaEntidad = new Talla(1L, "L");
        ActividadProgramada actividadProgramadaEncontrada = new ActividadProgramada(
                actividadProgramadaId, LocalDateTime.of(2025, 10, 8, 10, 30), LocalDateTime.of(2025, 10, 8, 11, 30), 30, actividadBase
        );
        Visitante visitanteGuardado1 = new Visitante("Johnny Lojuno", 44444444, LocalDate.of(2002, 01, 01), tallaEntidad);
        Visitante visitanteGuardado2 = new Visitante("Johnny Loconozco", 44444443, LocalDate.of(2002, 01, 01), tallaEntidad);

        // Esta es la inscripción final que se espera que se fabrique
        final Long inscripcionFinalId = 100L;
        Inscripcion inscripcionGuardada = new Inscripcion(
                inscripcionFinalId,
                inscripcionDTO.getFechaHoraInscripcion(),
                actividadProgramadaEncontrada,
                inscripcionDTO.getAceptanTerminosYCondiciones(),
                List.of(visitanteGuardado1, visitanteGuardado2)
        );

        // 1er Mock: Cuando el InscripcionService vaya a buscar si la actividad existe.
        Mockito.when(actividadProgramadaRepository.findById(actividadProgramadaId))
                .thenReturn(Optional.of(actividadProgramadaEncontrada));

        // 2do Mock: Cuando el InscripcionService vaya a guardar los visitantes.
        // El service llamará a save dos veces: una por cada participante.
        Mockito.when(visitanteRepository.save(Mockito.any(Visitante.class)))
                .thenReturn(visitanteGuardado1) // Primera llamada
                .thenReturn(visitanteGuardado2); // Segunda llamada (si se hace)

        // 3er Mock: Cuando el InscripcionService vaya a guardar la inscripción en la bdd.
        Mockito.when(inscripcionRepository.save(Mockito.any(Inscripcion.class)))
                .thenReturn(inscripcionGuardada);

        // Ahora llamamos al service y le pedimos hacer la inscripción.
        Inscripcion resultado = inscripcionService.inscribirVisitantes(inscripcionDTO);

        // Acá hacemos los asserts para verificar que se cumpla lo que nos está pidiendo la prueba de la US.

        // 1. Verificación básica y de ID
        assertNotNull(resultado, "El Service debe devolver una Entidad de Inscripción.");
        assertEquals(inscripcionFinalId, resultado.getId(), "La Inscripción debe tener el ID asignado por el Repositorio.");

        // 2. Verificación de la Lógica
        assertTrue(resultado.getAceptanTerminosYCondiciones(), "Debe reflejar que los T&C fueron aceptados.");
        assertEquals(actividadProgramadaId, resultado.getActividadSeleccionada().getId(), "La Inscripción debe estar ligada a la Actividad Programada correcta.");

        // 3. Verificación de Participantes
        assertEquals(2, resultado.getParticipantes().size(), "Deben haberse guardado 2 participantes.");
        // Chequear que los visitantes son los que devolvieron los mocks
        assertEquals(visitanteGuardado1.getDni(), resultado.getParticipantes().get(0).getDni());
        assertEquals(visitanteGuardado2.getDni(), resultado.getParticipantes().get(1).getDni());

        // 4. Verificación de Interacción (Asegurar que el Service hizo todas las llamadas necesarias y la cantidad de veces que lo hizo)
        Mockito.verify(actividadProgramadaRepository, Mockito.times(1)).findById(actividadProgramadaId);
        Mockito.verify(visitanteRepository, Mockito.times(2)).save(Mockito.any(Visitante.class));
        Mockito.verify(inscripcionRepository, Mockito.times(1)).save(Mockito.any(Inscripcion.class));
    }


}
