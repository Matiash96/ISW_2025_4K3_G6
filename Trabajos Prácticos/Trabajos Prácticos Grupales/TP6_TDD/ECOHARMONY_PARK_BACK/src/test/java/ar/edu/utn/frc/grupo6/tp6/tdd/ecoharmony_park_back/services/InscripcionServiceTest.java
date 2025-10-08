package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data.InscripcionDTO;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data.VisitanteDTO;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.CupoInsuficienteException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.DatosIncompletosException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.HorarioNoDisponibleException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.TerminosNoAceptadosException;
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
import org.springframework.boot.test.context.SpringBootTest; // por el momento no lo vamos a usar hasta diseñar la BDD

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

    @Test
    public void testInscribirseActividadSinCupo() {
        final Long actividadProgramadaId = 3L;

        VisitanteDTO visitanteDTO1 = new VisitanteDTO("Esteban Quito", 11223344, LocalDate.of(2000, 1, 1), 1L);
        List<VisitanteDTO> participantesDTO = List.of(visitanteDTO1);

        InscripcionDTO inscripcionDTO = new InscripcionDTO(
                LocalDateTime.of(2025, 10, 7, 10, 30),
                actividadProgramadaId,
                true,
                participantesDTO
        );

        Actividad actividadBase = new Actividad(4L, "Palestra", true, "Solo para mayores de 18 años");
        ActividadProgramada actividadProgramadaSinCupo = new ActividadProgramada(
                actividadProgramadaId,
                LocalDateTime.of(2025, 10, 8, 14, 0),
                LocalDateTime.of(2025, 10, 8, 15, 0),
                0,
                actividadBase
        );

        // 1er Mock: Simular que la búsqueda encuentra la actividad sin cupo
        Mockito.when(actividadProgramadaRepository.findById(actividadProgramadaId))
                .thenReturn(Optional.of(actividadProgramadaSinCupo));

        // 2do Mock: El Service DEBE lanzar CupoInsuficienteException
        assertThrows(CupoInsuficienteException.class, () -> {
            inscripcionService.inscribirVisitantes(inscripcionDTO);
        }, "Debe fallar porque el cupo es cero.");

        // 3er Mock: Confirmamos que la validación ocurrió ANTES de guardar.
        // El Service NO DEBIÓ intentar guardar ni visitantes ni la inscripción final.
        Mockito.verify(visitanteRepository, Mockito.never()).save(Mockito.any(Visitante.class));
        Mockito.verify(inscripcionRepository, Mockito.never()).save(Mockito.any(Inscripcion.class));
    }

    @Test
    public void testInscribirseActividadNoRequiereTalle() {

        final Long actividadProgramadaId = 3L;

        // 1. Actividad base que no requiere vestimenta
        Actividad actividadBase = new Actividad(5L, "Safari", false, "Recorrido a pie.");

        // 2. Visitante DTO
        VisitanteDTO visitanteDTO = new VisitanteDTO("Juan Perez", 55555555, LocalDate.of(1995, 5, 5), null);
        List<VisitanteDTO> participantesDTO = List.of(visitanteDTO);

        // 3. Inscripcion DTO
        InscripcionDTO inscripcionDTO = new InscripcionDTO(
                LocalDateTime.now(),
                actividadProgramadaId,
                true,
                participantesDTO
        );


        // 4. Actividad Programada que se busca (debe existir)
        ActividadProgramada actividadProgramadaEncontrada = new ActividadProgramada(
                actividadProgramadaId,
                LocalDateTime.of(2025, 11, 1, 9, 0),
                LocalDateTime.of(2025, 11, 1, 10, 30),
                50,
                actividadBase
        );

        // 5. Visitante guardado
        Visitante visitanteGuardado = new Visitante("Juan Perez", 55555555, LocalDate.of(1995, 5, 5), null); // Talla queda en null

        // 6. Inscripción final guardada
        final Long inscripcionFinalId = 200L;
        Inscripcion inscripcionGuardada = new Inscripcion(
                inscripcionFinalId,
                inscripcionDTO.getFechaHoraInscripcion(),
                actividadProgramadaEncontrada,
                inscripcionDTO.getAceptanTerminosYCondiciones(),
                List.of(visitanteGuardado)
        );

        // Mock: Simular la búsqueda de la actividad
        Mockito.when(actividadProgramadaRepository.findById(actividadProgramadaId))
                .thenReturn(Optional.of(actividadProgramadaEncontrada));

        // Mock: Simular el guardado del Visitante (ÉXITO)
        // El Service no debe fallar al guardar un Visitante con talla null si la actividad no la requiere.
        Mockito.when(visitanteRepository.save(Mockito.any(Visitante.class)))
                .thenReturn(visitanteGuardado);

        // Mock: Simular el guardado final de la Inscripción (ÉXITO)
        Mockito.when(inscripcionRepository.save(Mockito.any(Inscripcion.class)))
                .thenReturn(inscripcionGuardada);


        // 7. La llamada al Service DEBE ser exitosa
        Inscripcion resultado = inscripcionService.inscribirVisitantes(inscripcionDTO);

        // Verificaciones
        assertNotNull(resultado, "El Service debe devolver una Entidad de Inscripción.");
        assertEquals(inscripcionFinalId, resultado.getId());
        assertEquals(actividadProgramadaId, resultado.getActividadSeleccionada().getId());

        // Verificar que el Service sí guardó al visitante, incluso sin talla.
        Mockito.verify(visitanteRepository, Mockito.times(1)).save(Mockito.any(Visitante.class));
        Mockito.verify(inscripcionRepository, Mockito.times(1)).save(Mockito.any(Inscripcion.class));
    }

    @Test
    public void testInscribirseActividadHorarioNoDisponible() {
        final Long actividadProgramadaId = 33L;

        // 1. Horario de Inscripción FUERA DE RANGO: 9:00 AM (la actividad es de 10:30 a 11:30)
        final LocalDateTime horaInscripcionTemprana = LocalDateTime.of(2025, 10, 8, 9, 0, 0);

        // 2. Visitante DTO
        VisitanteDTO visitanteDTO = new VisitanteDTO("Ana Temporal", 66666666, LocalDate.of(1990, 1, 1), 1L);
        List<VisitanteDTO> participantesDTO = List.of(visitanteDTO);

        // 3. Inscripcion DTO: Hora que debe fallar
        InscripcionDTO inscripcionDTO = new InscripcionDTO(
                horaInscripcionTemprana, // Hora que falla
                actividadProgramadaId,
                true,
                participantesDTO
        );

        // 4. Actividad Programada que se busca (con un horario definido)
        Actividad actividadBase = new Actividad(6L, "Palestra", true, "Solo para mayores de 18 años");
        ActividadProgramada actividadProgramadaEncontrada = new ActividadProgramada(
                actividadProgramadaId,
                // Horario VÁLIDO: Inicia 10:30 AM
                LocalDateTime.of(2025, 10, 8, 10, 30),
                // Termina 11:30 AM
                LocalDateTime.of(2025, 10, 8, 11, 30),
                10,
                actividadBase
        );

        // --- ARRANGE: CONFIGURACIÓN DE MOCKS ---

        // 5. Mock: Simular que la búsqueda encuentra la actividad
        Mockito.when(actividadProgramadaRepository.findById(actividadProgramadaId))
                .thenReturn(Optional.of(actividadProgramadaEncontrada));

        // 6. El Service DEBE lanzar HorarioNoDisponibleException
        assertThrows(HorarioNoDisponibleException.class, () -> {
            inscripcionService.inscribirVisitantes(inscripcionDTO);
        }, "Debe fallar porque la hora de inscripción está fuera del horario programado de la actividad.");


        // 7. Confirmamos que la validación ocurrió ANTES de guardar.
        Mockito.verify(visitanteRepository, Mockito.never()).save(Mockito.any(Visitante.class));
        Mockito.verify(inscripcionRepository, Mockito.never()).save(Mockito.any(Inscripcion.class));
    }

    @Test
    public void testInscribirseSinAceptarTYC() {
        final Long actividadProgramadaId = 44L;

        // 1. Visitante DTO
        VisitanteDTO visitanteDTO = new VisitanteDTO("Juana Pereza", 77777777, LocalDate.of(1985, 1, 1), 1L);
        List<VisitanteDTO> participantesDTO = List.of(visitanteDTO);

        // 2. Inscripcion DTO
        InscripcionDTO inscripcionDTO = new InscripcionDTO(
                LocalDateTime.now(),
                actividadProgramadaId,
                false, // Los TYC van a estar negados
                participantesDTO
        );


        // 3. Mockear la actividad
        Actividad actividadBase = new Actividad(7L, "Palestra", true, "Solo para mayores de 18 años");
        ActividadProgramada actividadProgramadaEncontrada = new ActividadProgramada(
                actividadProgramadaId,
                LocalDateTime.of(2025, 10, 8, 12, 0),
                LocalDateTime.of(2025, 10, 8, 13, 0),
                5,
                actividadBase
        );

        // Configurar el mock de búsqueda de actividad para que NO falle por sí mismo
        Mockito.when(actividadProgramadaRepository.findById(actividadProgramadaId))
                .thenReturn(Optional.of(actividadProgramadaEncontrada));

        // 4. El Service DEBE lanzar TerminosNoAceptadosException
        assertThrows(TerminosNoAceptadosException.class, () -> {
            inscripcionService.inscribirVisitantes(inscripcionDTO);
        }, "Debe fallar porque los Términos y Condiciones no fueron aceptados.");

        // 5. Confirmamos que NO se llamó a ningún método de guardado
        Mockito.verify(visitanteRepository, Mockito.never()).save(Mockito.any(Visitante.class));
        Mockito.verify(inscripcionRepository, Mockito.never()).save(Mockito.any(Inscripcion.class));
    }

    @Test
    public void testInscribirseActividadTalleRequeridoNoIngresado() {final Long actividadProgramadaId = 55L; // Nuevo ID

        // 1. Visitante DTO: ID de Talla es NULL (Simula la falta del dato)
        VisitanteDTO visitanteDTO = new VisitanteDTO("Dani Vestimenta", 88888888, LocalDate.of(1998, 1, 1), null); // La talla es null
        List<VisitanteDTO> participantesDTO = List.of(visitanteDTO);

        // 2. Inscripcion DTO
        InscripcionDTO inscripcionDTO = new InscripcionDTO(
                LocalDateTime.now(),
                actividadProgramadaId,
                true,
                participantesDTO
        );

        // 3. Actividad Programada que se busca
        Actividad actividadBase = new Actividad(8L, "Tirolesa", true, "Requiere equipo de seguridad."); // requiereVestimenta = true
        ActividadProgramada actividadProgramadaRequerida = new ActividadProgramada(
                actividadProgramadaId,
                LocalDateTime.of(2025, 10, 8, 14, 0),
                LocalDateTime.of(2025, 10, 8, 15, 0),
                5,
                actividadBase // Requiere Vestimenta
        );

        // 4. 1er Mock: Simular que la búsqueda encuentra la actividad
        Mockito.when(actividadProgramadaRepository.findById(actividadProgramadaId))
                .thenReturn(Optional.of(actividadProgramadaRequerida));

        // 5. El Service DEBE lanzar DatosIncompletosException
        assertThrows(DatosIncompletosException.class, () -> {
            inscripcionService.inscribirVisitantes(inscripcionDTO);
        }, "Debe fallar porque la actividad requiere talla de vestimenta, pero el visitante no la proporcionó.");

        // 6. Confirmamos que NO se llamó a ningún método de guardado
        Mockito.verify(visitanteRepository, Mockito.never()).save(Mockito.any(Visitante.class));
        Mockito.verify(inscripcionRepository, Mockito.never()).save(Mockito.any(Inscripcion.class));}
}
