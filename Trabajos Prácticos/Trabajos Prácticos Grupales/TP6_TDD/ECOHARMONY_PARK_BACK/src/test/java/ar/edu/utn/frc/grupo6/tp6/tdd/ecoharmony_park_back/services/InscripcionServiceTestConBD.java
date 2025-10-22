package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data.InscripcionDTO;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data.VisitanteDTO;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.*;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Inscripcion;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Visitante;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.InscripcionRepository;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.VisitanteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application.properties")
public class InscripcionServiceTestConBD {

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private VisitanteRepository visitanteRepository;

    @Test
    public void testInscribirseConCupoHorarioDatosVisitanteTyCValidos() {
        // Arrange: Datos de la inscripción. La actividad con ID 3 y talla con ID 1 existen en data.sql
        final Long actividadProgramadaId = 3L;
        VisitanteDTO visitanteDTO1 = new VisitanteDTO("Johnny Lojuno", 44444444, 21, 1L);
        VisitanteDTO visitanteDTO2 = new VisitanteDTO("Johnny Loconozco", 44444443, 22, 1L);
        List<VisitanteDTO> participantesDTO = List.of(visitanteDTO1, visitanteDTO2);
        InscripcionDTO inscripcionDTO = new InscripcionDTO(
                LocalDateTime.of(2025, 10, 8, 10, 0, 0), // Inscripción durante el horario de la actividad (10:30-11:30)
                actividadProgramadaId,
                true,
                participantesDTO
        );

        // Act: Llamar al servicio para inscribir
        Inscripcion resultado = inscripcionService.inscribirVisitantes(inscripcionDTO);

        // Assert: Verificar que la inscripción se guardó correctamente en la BD
        assertNotNull(resultado, "El Service debe devolver una Entidad de Inscripción.");
        assertNotNull(resultado.getId(), "La Inscripción debe tener un ID asignado por la BD.");

        // Buscar la inscripción en la BD para verificar
        Optional<Inscripcion> inscripcionGuardadaOpt = inscripcionRepository.findById(resultado.getId());
        assertTrue(inscripcionGuardadaOpt.isPresent(), "La inscripción debe existir en la base de datos.");
        Inscripcion inscripcionGuardada = inscripcionGuardadaOpt.get();

        assertTrue(inscripcionGuardada.getAceptanTerminosYCondiciones(), "Debe reflejar que los T&C fueron aceptados.");
        assertEquals(actividadProgramadaId, inscripcionGuardada.getActividadSeleccionada().getId(), "La Inscripción debe estar ligada a la Actividad Programada correcta.");
        assertEquals(2, inscripcionGuardada.getParticipantes().size(), "Deben haberse guardado 2 participantes.");

        // Verificar que los visitantes se guardaron
        Optional<Visitante> visitante1 = visitanteRepository.findByDni(44444444);
        Optional<Visitante> visitante2 = visitanteRepository.findByDni(44444443);
        assertTrue(visitante1.isPresent(), "El primer visitante debe estar en la BD.");
        assertTrue(visitante2.isPresent(), "El segundo visitante debe estar en la BD.");
        assertEquals("Johnny Lojuno", visitante1.get().getNombre());
        assertEquals("Johnny Loconozco", visitante2.get().getNombre());
    }

    @Test
    public void testInscribirseActividadSinCupo() {
        // Arrange: La actividad con ID 4 en data.sql tiene cupo 0
        final Long actividadProgramadaId = 4L;
        VisitanteDTO visitanteDTO1 = new VisitanteDTO("Esteban Quito", 11223344, 21, 1L);
        InscripcionDTO inscripcionDTO = new InscripcionDTO(
                LocalDateTime.of(2025, 10, 7, 10, 30),
                actividadProgramadaId,
                true,
                List.of(visitanteDTO1)
        );

        long countInscripcionesAntes = inscripcionRepository.count();
        long countVisitantesAntes = visitanteRepository.count();

        // Act & Assert: Esperar la excepción y verificar que no se guardó nada
        assertThrows(CupoInsuficienteException.class, () -> inscripcionService.inscribirVisitantes(inscripcionDTO), "Debe fallar porque el cupo es cero.");

        assertEquals(countInscripcionesAntes, inscripcionRepository.count(), "No debe guardarse ninguna inscripción.");
        assertEquals(countVisitantesAntes, visitanteRepository.count(), "No debe guardarse ningún visitante.");
    }

    @Test
    public void testInscribirseActividadNoRequiereTalle() {
        // Arrange: La actividad con ID 6 en data.sql no requiere vestimenta
        final Long actividadProgramadaId = 6L;
        VisitanteDTO visitanteDTO = new VisitanteDTO("Juan Perez", 55555555, 21, null);
        InscripcionDTO inscripcionDTO = new InscripcionDTO(
                LocalDateTime.of(2025, 10, 30, 9, 30, 0), // Inscripción durante el horario de la actividad (09:00-10:30)
                actividadProgramadaId,
                true,
                List.of(visitanteDTO)
        );

        // Act: La inscripción debe ser exitosa
        Inscripcion resultado = inscripcionService.inscribirVisitantes(inscripcionDTO);

        // Assert: Verificar que se guardó correctamente
        assertNotNull(resultado);
        assertNotNull(resultado.getId());

        Optional<Visitante> visitanteGuardado = visitanteRepository.findByDni(55555555);
        assertTrue(visitanteGuardado.isPresent());
        assertNull(visitanteGuardado.get().getTallaVestimenta(), "La talla del visitante debe ser null.");
    }

    @Test
    public void testInscribirseActividadHorarioNoDisponible() {
        // Arrange: La actividad con ID 3 es de 10:30 a 11:30. La inscripción es a las 9:00.
        final Long actividadProgramadaId = 3L;
        final LocalDateTime horaInscripcionTemprana = LocalDateTime.of(2025, 10, 8, 15, 0, 0);
        VisitanteDTO visitanteDTO = new VisitanteDTO("Ana Temporal", 66666666, 21, 1L);
        InscripcionDTO inscripcionDTO = new InscripcionDTO(
                horaInscripcionTemprana,
                actividadProgramadaId,
                true,
                List.of(visitanteDTO)
        );

        long countInscripcionesAntes = inscripcionRepository.count();

        // Act & Assert
        assertThrows(HorarioNoDisponibleException.class, () -> inscripcionService.inscribirVisitantes(inscripcionDTO), "Debe fallar porque la hora de inscripción está fuera del horario.");
        assertEquals(countInscripcionesAntes, inscripcionRepository.count(), "No debe guardarse ninguna inscripción.");
    }

    @Test
    public void testInscribirseSinAceptarTerminosYCondiciones() {
        // Arrange: La inscripción tiene aceptanTerminosYCondiciones = false
        final Long actividadProgramadaId = 3L;
        VisitanteDTO visitanteDTO = new VisitanteDTO("Juana Pereza", 77777777, 21, 1L);
        InscripcionDTO inscripcionDTO = new InscripcionDTO(
                LocalDateTime.of(2025, 10, 8, 10, 45, 0), // Inscripción durante el horario válido
                actividadProgramadaId,
                false, // No acepta T&C
                List.of(visitanteDTO)
        );

        long countInscripcionesAntes = inscripcionRepository.count();

        // Act & Assert
        assertThrows(TerminosNoAceptadosException.class, () -> inscripcionService.inscribirVisitantes(inscripcionDTO), "Debe fallar porque los T&C no fueron aceptados.");
        assertEquals(countInscripcionesAntes, inscripcionRepository.count(), "No debe guardarse ninguna inscripción.");
    }

    @Test
    public void testInscribirseActividadTalleRequeridoNoIngresado() {
        // Arrange: La actividad con ID 3 requiere vestimenta, pero el visitante no provee talla.
        final Long actividadProgramadaId = 3L;
        VisitanteDTO visitanteDTO = new VisitanteDTO("Dani Vestimenta", 88888888, 21, null); // Talla es null
        InscripcionDTO inscripcionDTO = new InscripcionDTO(
                LocalDateTime.of(2025, 10, 8, 10, 0, 0), // Inscripción durante el horario válido
                actividadProgramadaId,
                true,
                List.of(visitanteDTO)
        );

        long countInscripcionesAntes = inscripcionRepository.count();

        // Act & Assert
        assertThrows(DatosIncompletosException.class, () -> inscripcionService.inscribirVisitantes(inscripcionDTO), "Debe fallar porque la talla es requerida y no se proveyó.");
        assertEquals(countInscripcionesAntes, inscripcionRepository.count(), "No debe guardarse ninguna inscripción.");
    }
}
